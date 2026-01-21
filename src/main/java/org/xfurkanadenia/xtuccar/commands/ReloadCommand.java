package org.xfurkanadenia.xtuccar.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.xfurkanadenia.xtuccar.XTuccar;
import org.xfurkanadenia.xtuccar.file.Locale;
import org.xfurkanadenia.xtuccar.model.SubCommand;

import java.util.HashMap;
import java.util.Map;

public class ReloadCommand extends SubCommand {
    public ReloadCommand() {
        super(XTuccar.getInstance().getConfig().getString("subcommands.reload"), "xtuccar.reload");
    }

    @Override
    public void execute(CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        XTuccar plugin = XTuccar.getInstance();
        Locale locale = plugin.getLocale();
        long startTimeStamp = System.currentTimeMillis();
        plugin.reload(v -> {
            Map<String, String> vars = new HashMap<>();
            long endTimeStamp = System.currentTimeMillis();
            double elapsedTime = endTimeStamp - startTimeStamp;
            vars.put("elapsed", String.valueOf(elapsedTime));
            vars.put("elapsed_seconds", String.valueOf(elapsedTime/1000));
            locale.sendMessage(sender, "reloaded", vars);
        });
    }
}
