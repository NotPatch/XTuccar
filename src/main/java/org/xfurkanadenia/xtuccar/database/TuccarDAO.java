package org.xfurkanadenia.xtuccar.database;

import org.xfurkanadenia.xtuccar.XTuccar;
import org.xfurkanadenia.xtuccar.model.MarketItem;
import org.xfurkanadenia.xtuccar.model.MarketSellingItem;
import org.xfurkanadenia.xtuccar.util.DatabaseUtils;
import org.xfurkanadenia.xtuccar.util.Serializer;

import javax.sql.DataSource;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class TuccarDAO {
    private DataSource dataSource;
    private AtomicInteger id = new AtomicInteger(1);
    public String saveItemSQLite = """
        INSERT INTO items (id, seller, itemid, item, amount, price)
        VALUES (?, ?, ?, ?, ?, ?)
        ON CONFLICT(id) DO UPDATE SET
          seller = excluded.seller,
          itemid = excluded.itemid,
          item = excluded.item,
          amount = excluded.amount,
          price = excluded.price;
        """;
    public String saveItemMySQL = """
            INSERT INTO items (id, seller, itemid, item, amount, price)
            VALUES (?, ?, ?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE
              seller = VALUES(seller),
              itemid = VALUES(itemid),
              item = VALUES(item),
              amount = VALUES(amount),
              price = VALUES(price);
            """;
    public TuccarDAO() {
        dataSource = XTuccar.getInstance().getDatabaseManager().getDataSource();
    }
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public void saveItem(MarketSellingItem item) {
        DatabaseType dbType = DatabaseType.valueOf(XTuccar.getInstance().getConfig().getString("database.type"));
        if(item.getMarketItem() == null) return;
        try (Connection conn = getConnection()) {
            PreparedStatement stmt;
            if (dbType == DatabaseType.MYSQL)
                stmt = conn.prepareStatement(saveItemMySQL);
            else if (dbType == DatabaseType.SQLITE)
                stmt = conn.prepareStatement(saveItemSQLite);
            else return;
            stmt.setInt(1, item.getId());
            stmt.setString(2, item.getSeller());
            stmt.setString(3, item.getItemId());
            stmt.setBytes(4, Serializer.serializeItem(item.getMarketItem().getItem()));
            stmt.setInt(5, item.getAmount());
            stmt.setDouble(6, item.getPrice());
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public Map<Integer, MarketSellingItem> getAllItems() {
        Map<Integer, MarketSellingItem> items = new HashMap<>();
        String sql = "SELECT * FROM items";
        try(Connection conn = getConnection()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()) {
                int id = rs.getInt("id");
                String seller = rs.getString("seller");
                String itemId = rs.getString("itemid");
                int amount = rs.getInt("amount");
                double price = rs.getDouble("price");
                items.put(id, new MarketSellingItem(seller, itemId, price, amount, id));
            }
            int maxId;
            if(!items.isEmpty()) {
                maxId = items.keySet().stream().max(Integer::compareTo).get();
                id.set(maxId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return items;
    }

    public Map<Integer, MarketSellingItem> getItemsBySeller(String seller) {
        Map<Integer, MarketSellingItem> items = new HashMap<>();
        String sql = "SELECT * FROM items WHERE seller = ?";
        try(Connection conn = getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, seller);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()) {
                int id = rs.getInt("id");
                String itemId = rs.getString("itemid");
                int amount = rs.getInt("amount");
                double price = rs.getDouble("price");
                items.put(id, new MarketSellingItem(seller, itemId, price, amount, id));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return items;
    }

    public MarketSellingItem getItem(int id) {
        String sql = "SELECT * FROM items WHERE id = ?";
        try(Connection conn = getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()) {
                String seller = rs.getString("seller");
                String itemId = rs.getString("itemid");
                int amount = rs.getInt("amount");
                double price = rs.getDouble("price");
                return new MarketSellingItem(seller, itemId, price, amount, id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public MarketSellingItem getItemBySeller(String seller, String itemId) {
        String sql = "SELECT * FROM items WHERE seller = ? AND itemid = ?";
        try (Connection conn = getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, seller);
            stmt.setString(2, itemId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                double price = rs.getDouble("price");
                int amount = rs.getInt("amount");
                int id = rs.getInt("id");
                return new MarketSellingItem(seller, itemId, price, amount, id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isPlayerSellingItem(String seller, String itemId) {
        String sql = "SELECT * FROM items WHERE seller = ? AND itemid = ?";
        try(Connection conn = getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, seller);
            stmt.setString(2, itemId);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()) return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }



    public void sellItem(MarketItem item, String seller, double price, int amount) {
        DatabaseType dbType = DatabaseType.valueOf(XTuccar.getInstance().getConfig().getString("database.type"));

        String sql = """
            INSERT INTO items (seller, itemid, amount, price, item)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, seller);
            stmt.setString(2, item.getItemId());
            stmt.setInt(3, amount);
            stmt.setDouble(4, price);
            stmt.setBytes(5, Serializer.serializeItem(item.getItem()));
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addStocktoItem(String seller, String itemId, int amount) {
        String sql = "UPDATE items SET amount = amount + ? WHERE seller = ? AND itemid = ?";
        try(Connection conn = getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, amount);
            stmt.setString(2, seller);
            stmt.setString(3, itemId);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean removeItem(int id) {
        String sql = "DELETE FROM items where id = ?";
        try(Connection conn = getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            int amount = stmt.executeUpdate();
            return amount >= 1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public AtomicInteger getId() {
        return id;
    }

    public DataSource getDataSource() {
        return dataSource;
    }
}
