package org.xfurkanadenia.xtuccar.integration;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.xfurkanadenia.xtuccar.Logger;
import org.xfurkanadenia.xtuccar.XTuccar;

public class VaultIntegration {
    private Economy economy;
    public VaultIntegration() {
        if(!setupEconomy()) {
            Logger.error("Economy cant setup!");
            Logger.error("Plugin disabling...");
            Bukkit.getServer().getPluginManager().disablePlugin(XTuccar.getInstance());
        }
    }

    public Economy getEconomy() {
        return economy;
    }

    public boolean setupEconomy() {
        if(Bukkit.getServer().getPluginManager().getPlugin("Vault") == null) Bukkit.getServer().getPluginManager().disablePlugin(XTuccar.getInstance());
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if(rsp == null) return false;
        economy = rsp.getProvider();
        if(economy == null) return false;

        return true;
    }
}
