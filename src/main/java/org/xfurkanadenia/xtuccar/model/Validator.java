package org.xfurkanadenia.xtuccar.model;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.xfurkanadenia.xtuccar.XTuccar;
import org.xfurkanadenia.xtuccar.file.Locale;
import org.xfurkanadenia.xtuccar.util.Utils;

public class Validator {
    public static boolean ValidateIsPlayer(CommandSender sender) {
        XTuccar plugin = XTuccar.getInstance();
        Locale locale = plugin.getLocale();
        if(!(sender instanceof Player)) {
            locale.sendMessage(sender, "console-cant-run");
            return false;
        }
        return true;
    }

    public static boolean ValidatePermission(CommandSender sender, String permission) {
        XTuccar plugin = XTuccar.getInstance();
        Locale locale = plugin.getLocale();
        if(!sender.hasPermission(permission)) {
            locale.sendMessage(sender, "no-permission");
            return false;
        }
        return true;
    }

    public static boolean ValidateStock(MarketSellingItem item, int amount, Player player) {
        XTuccar plugin = XTuccar.getInstance();
        Locale locale = plugin.getLocale();
        if(item.getAmount() < amount) {
            locale.sendMessage(player, "item-not-have-enough-stock");
            return false;
        }
        return true;
    }

    public static boolean ValidateMoney (Player player, double amount) {
        XTuccar plugin = XTuccar.getInstance();
        Locale locale = plugin.getLocale();
        Economy economy = plugin.getVaultIntegration().getEconomy();
        if(economy.getBalance(player) < amount) {
            locale.sendMessage(player, "player-not-have-enough-money");
            return false;
        }
        return true;
    }

    public static boolean ValidateHasSpace(Player player, ItemStack item) {
        boolean hasSpace = Utils.hasSpace(player, item);
        XTuccar plugin = XTuccar.getInstance();
        Locale locale = plugin.getLocale();
        if(!hasSpace) {
            locale.sendMessage(player, "player-not-have-enough-space");
            return false;
        }
        return true;
    }
}
