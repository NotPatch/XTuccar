package org.xfurkanadenia.xtuccar.model;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class SubCommand extends Validator {
    private String name;
    private String permission;

    public SubCommand(String name) {
        this(name, null);
    }
    public SubCommand(String name, String permission) {
        this.name = name;
        this.permission = permission;
    }
    public String getName() {
        return name;
    }

    public abstract void execute(CommandSender sender, @NotNull Command command, @NotNull String label, String[] args);
    public List<String> tabComplete(CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        return List.of();
    }

    public String getPermission() {
        return permission;
    }
}
