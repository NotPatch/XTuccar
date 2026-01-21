package org.xfurkanadenia.xtuccar.util;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.xfurkanadenia.xtuccar.XTuccar;
import org.xfurkanadenia.xtuccar.model.Category;
import org.xfurkanadenia.xtuccar.model.MarketItem;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class TuccarUtils {
    public static void setMarketItems() {
        XTuccar main = XTuccar.getInstance();
        FileConfiguration marketItemsCfg = main.getMarketItemsCfg().getConfig();
        Map<String, MarketItem> marketItems = XTuccar.getInstance().getMarketItems();

        main.getMarketItems().clear();

        marketItemsCfg.getKeys(false).forEach(key -> {
            marketItems.put(key, new MarketItem(key));
        });
    }

    public static void setCategories() {
        XTuccar main = XTuccar.getInstance();
        FileConfiguration categoriesCfg = main.getCategoriesCfg().getConfig();

        List<Category> categories = main.getCategories();
        categories.clear();

        ConfigurationSection categoriesSection = categoriesCfg.getConfigurationSection("categories");

        categoriesSection.getKeys(false).forEach(key -> {
                ConfigurationSection section = categoriesCfg.getConfigurationSection("categories." + key);
                Material material;
                material = Material.getMaterial(Objects.requireNonNull(section.getString("material", "STONE").toUpperCase()));

                if (material == null) {
                    main.getLogger().severe("Material \"" + section.getString("material").toUpperCase() + "\" not found in " + section.getName());
                    return;
                }
                Category category = new Category(key, material);
                if (section.contains("displayName"))
                    category.setDisplayName(Utils.translateColorCodes(section.getString("displayName")));
                if (section.contains("lore"))
                    category.setLore(Utils.translateColorCodes(section.getStringList("lore")));
                if(section.contains("customModelData"))
                    category.setCustomModelData(section.getInt("customModelData", -1));
                if(section.contains("amount"))
                    category.setAmount(section.getInt("amount", 1));
                categories.add(category);
            });
        }
}
