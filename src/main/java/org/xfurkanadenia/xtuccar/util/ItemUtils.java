package org.xfurkanadenia.xtuccar.util;

import fr.mrmicky.fastinv.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.xfurkanadenia.xtuccar.XTuccar;
import org.xfurkanadenia.xtuccar.Logger;

import java.io.File;
import java.util.*;

public class ItemUtils {
    private static final ItemStack ERROR_ITEM = new ItemBuilder(Material.BARRIER).name("ERROR").build();
    private static final List<String> REQUIRED_ITEM_KEYS = List.of("material");
    public static ItemStack getItem(ConfigurationSection itemCfg) {
        XTuccar plugin = XTuccar.getInstance();
        String fileName = new File(itemCfg.getCurrentPath()).getName();
        String itemName = itemCfg.getName();
        if(!checkItem(itemName, fileName, itemCfg)) return ERROR_ITEM;
        Material material = Material.getMaterial(Objects.requireNonNull(itemCfg.getString("material")));
        if (material == null) {
            plugin.getLogger().severe("Material \"" + itemCfg.getString("material") + "\" not found in " + itemCfg.getName());
            return null;
        }

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (itemCfg.contains("displayName") && meta != null) {
            meta.setDisplayName(itemCfg.getString("displayName"));
        }

        if (itemCfg.contains("lore") && meta != null) {
            List<String> formattedLore = new ArrayList<>();
            for (String line : itemCfg.getStringList("lore")) {
                formattedLore.add(line);
            }
            meta.setLore(formattedLore);
        }

        if (itemCfg.contains("customModelData") && meta != null) {
            meta.setCustomModelData(itemCfg.getInt("customModelData"));
        }

        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ADDITIONAL_TOOLTIP, ItemFlag.HIDE_PLACED_ON, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_DYE);
        item.setItemMeta(meta);
        return item;
    }

    public static boolean checkItem(String itemName, String fileName, ConfigurationSection config) {
        for (String key : REQUIRED_ITEM_KEYS) {
            if(!config.contains(key)) {
                Logger.error("Invalid GUI Key (%s): ".formatted(config.getName()) + key);
                return false;
            }
        }
        return true;
    }
}
