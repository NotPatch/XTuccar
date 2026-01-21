package org.xfurkanadenia.xtuccar;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;

public class Logger {
    public static void info(String message) {
        String prefix = XTuccar.getInstance().getDescription().getPrefix() + " ";
        Bukkit.getConsoleSender().sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', message));
    }

    public static void warn(String message) {
        String prefix = XTuccar.getInstance().getDescription().getPrefix() + " ";
        Bukkit.getConsoleSender().sendMessage(prefix + ChatColor.YELLOW + message);
    }

    public static void error(String message) {
        String prefix = XTuccar.getInstance().getDescription().getPrefix() + " ";
        Bukkit.getConsoleSender().sendMessage(prefix + ChatColor.RED + message);
    }
}
