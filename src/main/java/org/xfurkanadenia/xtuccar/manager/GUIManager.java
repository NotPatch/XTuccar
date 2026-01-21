package org.xfurkanadenia.xtuccar.manager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.xfurkanadenia.xtuccar.XTuccar;
import org.xfurkanadenia.xtuccar.Logger;
import org.xfurkanadenia.xtuccar.model.GUI;
import org.xfurkanadenia.xtuccar.model.GUIItem;
import org.xfurkanadenia.xtuccar.util.FileUtils;
import fr.mrmicky.fastinv.FastInv;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.xfurkanadenia.xtuccar.util.ItemUtils;

import javax.annotation.Nullable;
import java.io.File;
import java.util.*;

public class GUIManager {

    private static List<String> REQUIRED_GUI_KEYS = List.of("title", "size", "items");

    private final XTuccar plugin;
    private final Map<String, GUI> guis = new HashMap<>();
    public static final Map<Player, FastInv> openedGuis = new HashMap<>();

    public GUIManager(XTuccar plugin) {
        this.plugin = plugin;
    }


    public void loadGuis() {
        guis.clear();

        loadDefaultFiles();


        File[] files = getGuiFiles();
        if (files != null) {
            for (File file : files) {
                String nameWithoutExt = file.getName().replaceFirst("[.][^.]+$", "");
                YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
                if(!checkGui(cfg)) return;
                GUI gui = new GUI(cfg.getString("title"), cfg.getInt("size"));
                loadItems(cfg, gui);
                guis.put(nameWithoutExt, gui);
                Logger.info("&f%s &bsuccessfully loaded!".formatted(nameWithoutExt));
            }
        }
    }

    public File[] getGuiFiles() {
        File guisFolder = new File(plugin.getDataFolder(), "guis");
        return guisFolder.listFiles((dir, name) -> name.endsWith(".yml"));

    }

    public void loadItems(FileConfiguration cfg, GUI gui) {
        cfg.getConfigurationSection("items").getKeys(false).forEach(key -> {
            ConfigurationSection itemSection = cfg.getConfigurationSection("items." + key);
            List<Integer> slots;
            if (itemSection.contains("slots")) slots = itemSection.getIntegerList("slots");
            else if (itemSection.contains("slot")) slots = new ArrayList<>(List.of(itemSection.getInt("slot")));
            else return;
            assert itemSection != null;

            GUIItem item = new GUIItem(ItemUtils.getItem(itemSection), slots);
            if (itemSection.contains("actions")) item.setRawActions(itemSection.getStringList("actions"));
            if (itemSection.contains("type")) item.setType(itemSection.getString("type"));

            gui.setItem(item);

        });
    }

    public void loadDefaultFiles() {
        File guisFolder = new File(plugin.getDataFolder(), "guis");
        List<String> menus = FileUtils.listResources("guis");
        for (String menuFile : menus) {
            File file = new File(guisFolder, menuFile);
            if (!file.exists()) {
                plugin.saveResource("guis/" + menuFile, false);
                plugin.getLogger().info(menuFile + " &bcreated.");
            }
        }

    }

    public boolean checkGui(FileConfiguration config) {
        for (String key : REQUIRED_GUI_KEYS) {
            if(!config.contains(key)) {
                Logger.error("Invalid GUI Key (%s): ".formatted(config.getName()) + key);
                return false;
            }
        }
        return true;
    }

    public Map<String, GUI> getGuis() {
        return guis;
    }

    public void closeAllGuis() {
        for(Player player : Bukkit.getOnlinePlayers()) {
            if(openedGuis.containsKey(player)) player.closeInventory();
        }
    }

    public @Nullable GUI getGui(String name) {
        return guis.get(name);
    }
}