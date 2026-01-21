package org.xfurkanadenia.xtuccar.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.xfurkanadenia.xtuccar.gui.TuccarCategoryMenu;
import org.xfurkanadenia.xtuccar.model.SubCommand;
import org.xfurkanadenia.xtuccar.model.cTabExecutor;

import java.util.List;

public class MainCommand extends cTabExecutor {
    public MainCommand() {
        this.register(new SellCommand());
        this.register(new AddStockCommand());
        this.register(new ChangePriceCommand());
        this.register(new ReloadCommand());
        this.register(new SaveAllCommand());
        this.register(new AddItemCommand());
    }

    public boolean execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!ValidateIsPlayer(sender)) {
            return false;
        } else {
            (new TuccarCategoryMenu((Player)sender)).open((Player)sender);
            return true;
        }
    }

    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return this.subCommands.entrySet().stream().filter((e) -> ((SubCommand)e.getValue()).getPermission() == null || sender.hasPermission(((SubCommand)e.getValue()).getPermission())).map((v) -> ((String)v.getKey()).toLowerCase()).toList();
    }
}
