package org.xfurkanadenia.xtuccar.model;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public abstract class cTabExecutor extends Validator implements TabExecutor {
    public Map<String, SubCommand> subCommands = new HashMap<>();
    public void register(SubCommand cmd) {
        subCommands.put(cmd.getName().toLowerCase(), cmd);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 0) return execute(sender, command, label, args);
        else if(subCommands.containsKey(args[0])) if(subCommands.get(args[0]).getPermission() == null || ValidatePermission(sender, subCommands.get(args[0]).getPermission())) subCommands.get(args[0]).execute(sender, command, label, Arrays.copyOfRange(args, 1, args.length));
        return false;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 1) return tabComplete(sender, command, label, args);
        else if(subCommands.containsKey(args[0])) {
            String[] subArgs;
            if (args.length >= 2) subArgs = Arrays.copyOfRange(args, 1, args.length);
            else subArgs = new String[]{};

            return subCommands.get(args[0]).tabComplete(sender, command, label, subArgs);
        }
        return List.of();
    }

    public abstract boolean execute(CommandSender sender, @NotNull Command command, @NotNull String label, String[] args);
    public abstract List<String> tabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args);
}
