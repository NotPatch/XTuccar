package org.xfurkanadenia.xtuccar;

import fr.mrmicky.fastinv.FastInvManager;
import org.xfurkanadenia.xtuccar.commands.MainCommand;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.xfurkanadenia.xtuccar.database.DatabaseManager;
import org.xfurkanadenia.xtuccar.file.DataFile;
import org.xfurkanadenia.xtuccar.file.Locale;
import org.xfurkanadenia.xtuccar.integration.VaultIntegration;
import org.xfurkanadenia.xtuccar.listener.PlayerChatListener;
import org.xfurkanadenia.xtuccar.manager.DataManager;
import org.xfurkanadenia.xtuccar.manager.GUIManager;
import org.xfurkanadenia.xtuccar.manager.TuccarManager;
import org.xfurkanadenia.xtuccar.model.Category;
import org.xfurkanadenia.xtuccar.model.MarketItem;
import org.xfurkanadenia.xtuccar.util.DatabaseUtils;
import org.xfurkanadenia.xtuccar.util.TuccarUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public final class XTuccar extends JavaPlugin {
    private Locale locale;
    private GUIManager guiManager;
    private DatabaseManager databaseManager;
    private TuccarManager tuccarManager;
    private Map<String, MarketItem> marketItems;
    private List<Category> categories;
    private DataFile marketItemsCfg;
    private DataFile categoriesCfg;
    private VaultIntegration vaultIntegration;
    private DataManager dataManager;
    @Override
    public void onEnable() {
        vaultIntegration = new VaultIntegration();
        vaultIntegration.setupEconomy();
        locale = new Locale(this);
        guiManager = new GUIManager(this);
        dataManager = new DataManager();
        FastInvManager.register(this);
        this.getServer().getPluginManager().registerEvents(new PlayerChatListener(), this);

        reload(v -> {});
    }

    public void reload(Consumer<Void> cb) {
        guiManager.closeAllGuis();
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            saveDefaultConfig();
            reloadConfig();

            if(getTuccarManager() != null) getTuccarManager().flushSync();
            getCommand("tüccar").setExecutor(new MainCommand());
            marketItemsCfg = new DataFile("items.yml");
            marketItemsCfg.saveDefaultConfig();
            marketItemsCfg.reloadConfig();

            categoriesCfg = new DataFile("categories.yml");
            categoriesCfg.saveDefaultConfig();
            categoriesCfg.reloadConfig();

            locale.loadLocale(getConfig().getString("locale", "en_US"));
            if (databaseManager != null) {
                databaseManager.close();
            }
            DatabaseUtils.setupDatabase();
            guiManager.loadGuis();
            tuccarManager = new TuccarManager();
            tuccarManager.preload(v -> {
                marketItems = new HashMap<>();
                categories = new ArrayList<>();
                TuccarUtils.setCategories();
                TuccarUtils.setMarketItems();
                Bukkit.getScheduler().runTask(this, vv -> cb.accept(null));
            });


        });
    }

    @Override
    public void onDisable() {
        if(guiManager != null) guiManager.closeAllGuis();
        if(tuccarManager != null) tuccarManager.flushSync();
    }
    public static XTuccar getInstance() {
        return getPlugin(XTuccar.class);
    }
    public GUIManager getGUIManager() {
        return guiManager;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public void setDatabaseManager(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public DataFile getMarketItemsCfg() {
        return marketItemsCfg;
    }

    public DataFile getCategoriesCfg() {
        return categoriesCfg;
    }

    public List<Category> getCategories() {
        return categories;
    }


    public Map<String, MarketItem> getMarketItems() {
        return marketItems;
    }

    public Locale getLocale() {
        return locale;
    }

    public TuccarManager getTuccarManager() {
        return tuccarManager;
    }

    public VaultIntegration getVaultIntegration() {
        return vaultIntegration;
    }

    public DataManager getDataManager() {
        return dataManager;
    }
}
