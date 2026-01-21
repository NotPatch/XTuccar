package org.xfurkanadenia.xtuccar.file;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.xfurkanadenia.xtuccar.XTuccar;
import org.xfurkanadenia.xtuccar.util.Utils;

import java.io.File;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Locale {
    private final List<String> Locales = List.of("tr_TR", "en_US");
    private FileConfiguration messages;

    private final XTuccar plugin;

    public Locale(XTuccar plugin) {
        this.plugin = plugin;
    }

    public void loadLocale(String locale) {
        if (!Locales.contains(locale)) locale = "en_US";

        // Dosyayı kaydet ve yükle
        if (plugin.getResource("locales/" + locale + ".yml") != null) {
            File file = new File(plugin.getDataFolder(), "locales/" + locale + ".yml"); // Dosyayı yükle

            if(!file.exists()) plugin.saveResource("locales/" + locale + ".yml", false);
            // Dosyayı kaydet
            messages = YamlConfiguration.loadConfiguration(file);
            messages.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(Objects.requireNonNull(plugin.getResource("locales/" + locale + ".yml")))));
        } else {
            plugin.getLogger().severe("Error while trying to load the " + locale + " locale! File not found in resources.");
        }
    }

    public FileConfiguration getLocale() {
        return messages;
    }

    public String getMessage(String key) {
        if (messages != null && messages.contains(key)) {
            return messages.getString(key);
        } else if (messages != null && messages.getDefaults() != null && messages.getDefaults().contains(key)) {
            return messages.getDefaults().getString(key);
        } else {
            plugin.getLogger().warning("Message key not found: " + key);
            return "§cMessage not found: " + key; // Hata mesajı döndür
        }
    }


    public void sendMessage(CommandSender sender, String message) {
        sendMessage(sender, message, Map.of());
    }
    public void sendMessage(CommandSender sender, String key, Map<String, String> vars) {
        Object message = messages.get(key);
        if(message instanceof List) {
            for(String line : (List<String>) message) {
                sender.sendMessage(Utils.getFormatted(String.valueOf(line), sender, vars));
            }
        } else {
            sender.sendMessage(Utils.getFormatted(String.valueOf(message), sender, vars));
        }
    }
}