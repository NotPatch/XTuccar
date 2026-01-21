package org.xfurkanadenia.xtuccar.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.xfurkanadenia.xtuccar.XTuccar;
import org.xfurkanadenia.xtuccar.file.Locale;
import org.xfurkanadenia.xtuccar.manager.TuccarManager;
import org.xfurkanadenia.xtuccar.model.SubCommand;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

public class SaveAllCommand extends SubCommand {
    public SaveAllCommand() {
        super(XTuccar.getInstance().getConfig().getString("subcommands.saveAll"));
    }
    @Override
    public void execute(CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if(!ValidatePermission(sender, "xtuccar.flush")) return;
        XTuccar plugin = XTuccar.getInstance();
        Player player = (Player) sender;
        TuccarManager tuccarManager = plugin.getTuccarManager();
        Locale locale = plugin.getLocale();
        long startTimeStamp = System.currentTimeMillis();
        tuccarManager.saveAll(v -> {
            long endTimeStamp = System.currentTimeMillis();
            double elapsedTime = endTimeStamp - startTimeStamp;
            Map<String, String> vars = new HashMap<>();
            vars.put("elapsed", String.valueOf(elapsedTime));
            vars.put("elapsed_seconds", String.valueOf(elapsedTime/1000));
            locale.sendMessage(player, "saved", vars);
        });
    }
}
