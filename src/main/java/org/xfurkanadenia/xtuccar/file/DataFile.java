package org.xfurkanadenia.xtuccar.file;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.xfurkanadenia.xtuccar.XTuccar;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

public class DataFile {


    private File path;
    private String fileName;

    private FileConfiguration config;

    public DataFile(String fileName) {
        this.fileName = fileName;
    }

    public void reloadConfig() {
        if(this.path == null)
            this.path = new File(XTuccar.getInstance().getDataFolder(), fileName);
        config = YamlConfiguration.loadConfiguration(path);
        InputStream inputStream = XTuccar.getInstance().getResource(fileName);
    }

    public FileConfiguration getConfig() {
        if(config == null) reloadConfig();
        return config;
    }

    public void saveConfig() {
        if(config == null || path == null) return;

        try {
            config.save(this.path);
        } catch (Exception err) {
            XTuccar.getInstance().getLogger().severe(String.format("Config saving failed (%s): ", path) + err);
        }
    }

    public void saveDefaultConfig() {
        if(path == null)
            this.path = new File(XTuccar.getInstance().getDataFolder(), fileName);
        if(!this.path.exists())
            XTuccar.getInstance().saveResource(fileName, false);
    }

}
