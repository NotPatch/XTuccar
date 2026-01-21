package org.xfurkanadenia.xtuccar.util;


import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.xfurkanadenia.xtuccar.XTuccar;


import java.text.NumberFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.clip.placeholderapi.PlaceholderAPI;
import org.xfurkanadenia.xtuccar.file.Locale;
import org.xfurkanadenia.xtuccar.model.Category;
import org.xfurkanadenia.xtuccar.model.MarketItem;
import org.xfurkanadenia.xtuccar.model.MarketSellingItem;

public class Utils {
    public static List<String> translateColorCodes(List<String> list) {
        List<String> _list = new ArrayList<>(list);
        for(String line : _list) {
            line = String.valueOf(line);
            int index = _list.indexOf(line);
            _list.set(index, Utils.translateColorCodes(line));
        }
        return _list;
    }
    public static String translateColorCodes(String message) {
        if (message == null) return "";

        Pattern pattern = Pattern.compile("&#[a-fA-F0-9]{6}");
        Matcher matcher = pattern.matcher(message);

        while (matcher.find()) {
            String hexCode = matcher.group(); // Örn: &#FFAA00
            String color = hexCode.substring(1); // '#' ile başlasın: #FFAA00
            message = message.replace(hexCode, ChatColor.of(color).toString());
            matcher = pattern.matcher(message);
        }

        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static String placeholders(String txt, Map<String, String> vars) {
        for (Map.Entry<String, String> entry : vars.entrySet()) {
            txt = txt.replace("%" + entry.getKey() + "%", String.valueOf(entry.getValue()));
        }
        return txt;
    }
    public static String placeholders(String txt, CommandSender sender, Map<String, String> vars) {
        if (XTuccar.getInstance().getServer().getPluginManager().getPlugin("PlaceHolderAPI") != null && sender instanceof Player) {
            boolean parse = true;
            String pMsg = txt;
            while (parse) {
                pMsg = PlaceholderAPI.setPlaceholders((Player) sender, pMsg);
                if (txt.equalsIgnoreCase(pMsg)) {
                    parse = false;
                }
                txt = pMsg;
            }
        }

        txt = txt
                .replaceAll("%player%", sender.getName())
                .replaceAll("%prefix%", translateColorCodes(XTuccar.getInstance().getConfig().getString("prefix")));

        for (Map.Entry<String, String> entry : vars.entrySet()) {
            txt = txt.replace("%" + entry.getKey() + "%", String.valueOf(entry.getValue()));
        }
        return txt;
    }

    public static List<String> getFormatted(List<String> list, Map<String, String> vars) {
        List<String> formatted = new ArrayList<>();
        for (String txt : list) {
            formatted.add(getFormatted(txt, vars));
        }
        return formatted;
    }
    public static List<String> getFormatted(List<String> list, Player player, Map<String, String> vars) {
        List<String> formatted = new ArrayList<>();
        for (String txt : list) {
            formatted.add(getFormatted(txt, player, vars));
        }
        return formatted;
    }

    public static String getFormatted(String text, Map<String, String> vars) {
        String processed = placeholders(text, vars);

        return translateColorCodes(processed);
    }

    public static String getFormatted(String text, CommandSender sender, Map<String, String> vars) {
        String processed = placeholders(text, sender, vars);

        return translateColorCodes(processed);
    }

    public static ItemStack getFormattedItem(ItemStack i, Player p, Map<String, String> vars) {
        return getFormattedItem(i, p, vars, true);
    }
    public static ItemStack getFormattedItem(ItemStack i, Player p, Map<String, String> vars, boolean hide) {
        ItemStack item = i.clone();
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta.hasDisplayName()) itemMeta.setDisplayName(getFormatted(itemMeta.getDisplayName(), p, vars));
        if (itemMeta.hasLore()) {
            List<String> lore = itemMeta.getLore();
            lore.forEach(txt ->
                    lore.set(lore.indexOf(txt), getFormatted(txt, p, vars))
            );
            itemMeta.setLore(lore);
        }
        if(hide) itemMeta.addItemFlags(ItemFlag.values());
        item.setItemMeta(itemMeta);
        return item;
    }

    public static Map<String, String> getItemPlaceholders(MarketItem marketItem) {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("item_material", marketItem.getItem().getType().toString());
        placeholders.put("item_material_lower", marketItem.getItem().getType().toString().toLowerCase());
        String upperCharMaterial = marketItem.getItem().getType().toString().substring(0, 1).toUpperCase() + marketItem.getItem().getType().toString().substring(1).toLowerCase();
        placeholders.put("item_material_upper", upperCharMaterial);
        placeholders.put("item_id", marketItem.getItemId());
        placeholders.put("item_category_name", marketItem.getCategory().getName());
        placeholders.put("item_category_displayName", Utils.translateColorCodes(marketItem.getCategory().getDisplayName()));
        placeholders.put("item_category_lore", marketItem.getCategory().getLore().toString());
        placeholders.put("item_category_material", marketItem.getCategory().getMaterial().toString());
        return placeholders;
    }

    public static Map<String, String> getSellingItemPlaceholders(MarketSellingItem item) {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.putAll(getItemPlaceholders(item.getMarketItem()));
        placeholders.putAll(Map.of(
                "seller", item.getSeller(),
                "price", String.valueOf(item.getPrice()),
                "price_formatted", NumberFormat.getInstance().format(item.getPrice()),
                "stock", String.valueOf(item.getAmount()),
                "stock_formatted", NumberFormat.getInstance().format(item.getAmount())
        ));
        return placeholders;
    }

    public static Map<String, String> getCategoryPlaceholders(Category category) {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("category_name", category.getName());
        placeholders.put("category_material", category.getMaterial().toString());
        placeholders.put("category_displayName", category.getDisplayName());
        placeholders.put("category_lore", category.getLore().toString());
        return placeholders;
    }

    public static boolean isInt(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isDouble(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean ValidateInteger(Player player, String str) {
        XTuccar plugin = XTuccar.getInstance();
        Locale locale = plugin.getLocale();
        if(!isInt(str)) {
            locale.sendMessage(player, "invalid-number");
            return false;
        }
        return true;
    }
    public static boolean ValidateDouble(Player player, String str) {
        XTuccar plugin = XTuccar.getInstance();
        Locale locale = plugin.getLocale();
        if(!isDouble(str)) {
            locale.sendMessage(player, "invalid-number");
            return false;
        }
        return true;
    }

    public boolean isPotion(ItemStack item) {
        return item.getType().toString().endsWith("POTION");
    }

    public static int getItemAmount(ItemStack i, Player p) {
        int amount = 0;
        for(ItemStack item : p.getInventory().getContents()) {
            if(item != null && item.isSimilar(i)) amount += item.getAmount();
        }
        return amount;
    }

    public static boolean hasSpace(Player player, ItemStack item, int amount) {
        int remaining = amount;
        ItemStack[] contents = player.getInventory().getStorageContents();

        for (ItemStack content : contents) {
            if (content == null || content.getType() == Material.AIR) {
                // Boş slot → maksimum stack size kadar yer var
                remaining -= item.getMaxStackSize();
            } else if (content.isSimilar(item)) {
                // Aynı item varsa, boş kalan kapasiteyi hesapla
                int availableSpace = item.getMaxStackSize() - content.getAmount();
                if (availableSpace > 0) {
                    remaining -= availableSpace;
                }
            }

            if (remaining <= 0) {
                return true; // Yeterli yer bulundu
            }
        }

        return remaining <= 0; // Son kontrol
    }

}