package org.xfurkanadenia.xtuccar.gui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.xfurkanadenia.xtuccar.XTuccar;
import org.xfurkanadenia.xtuccar.manager.TuccarManager;
import org.xfurkanadenia.xtuccar.model.MarketSellingItem;
import org.xfurkanadenia.xtuccar.model.cFastInv;
import org.xfurkanadenia.xtuccar.util.Utils;

import java.util.*;

public class PlayerSellingItemsMenu extends cFastInv {

    private int page;
    private boolean hasNextPage = false;

    public PlayerSellingItemsMenu(Player p) {
        this(p, 1);
    }

    public PlayerSellingItemsMenu(Player p, int page) {
        super("playerSellingItems", getPlaceholders(page));
        this.page = page;

        getItems().forEach(item -> {
            int[] slots = item.getSlots().stream().mapToInt(Integer::intValue).toArray();

            switch (item.getType().toLowerCase()) {
                case "items" -> {}
                case "previous_page" -> setItems(slots,
                        Utils.getFormattedItem(item.getItem(), p, getPlaceholders(page)),
                        e -> {
                            if (page > 1) new PlayerSellingItemsMenu(p, page - 1).open(p);
                        }
                );
                case "next_page" -> setItems(slots,
                        Utils.getFormattedItem(item.getItem(), p, getPlaceholders(page)),
                        e -> {
                            if (hasNextPage) new PlayerSellingItemsMenu(p, page + 1).open(p);
                        }
                );
                case "back" -> setItems(slots,
                        Utils.getFormattedItem(item.getItem(), p, getPlaceholders(page)),
                        e -> {
                             new TuccarCategoryMenu(p).open(p);
                        }
                );
                default -> setItems(slots, Utils.getFormattedItem(item.getItem(), p, getPlaceholders(page)));
            }
        });

        setSellingItems(p);
    }

    private void setSellingItems(Player p) {
        XTuccar plugin = XTuccar.getInstance();
        TuccarManager tuccarManager = plugin.getTuccarManager();

        var itemsConfig = getItems().stream()
                .filter(i -> i.getType().equalsIgnoreCase("items"))
                .findFirst()
                .orElse(null);

        if (itemsConfig == null) return;

        int pageSize = itemsConfig.getSlots().size();
        int[] slots = itemsConfig.getSlots().stream().mapToInt(Integer::intValue).toArray();

        tuccarManager.getAllItems((allItems) -> {
            List<MarketSellingItem> list = new ArrayList<>(allItems.values());

            int start = (page - 1) * pageSize;
            int end = Math.min(start + pageSize, list.size());

            hasNextPage = end < list.size();
            if (start >= list.size()) return;

            List<MarketSellingItem> pageItems = list.subList(start, end);

            for (int i = 0; i < pageItems.size() && i < slots.length; i++) {
                MarketSellingItem v = pageItems.get(i);
                if(v.getMarketItem() == null) continue;
                ItemStack cItem = v.getMarketItem().getItem();
                ItemMeta cItemMeta = cItem.getItemMeta();
                cItemMeta.setLore(itemsConfig.getItem().getItemMeta().getLore());
                cItem.setItemMeta(cItemMeta);
                setItem(
                        slots[i],
                        Utils.getFormattedItem(cItem, p, getPlaceholders(v)),
                        e -> {

                        }
                );
            }
        });
    }

    private static Map<String, String> getPlaceholders(int page) {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("page", String.valueOf(page));
        return placeholders;
    }

    private static Map<String, String> getPlaceholders(MarketSellingItem marketSellingItem) {
        return Utils.getSellingItemPlaceholders(marketSellingItem);
    }
}
