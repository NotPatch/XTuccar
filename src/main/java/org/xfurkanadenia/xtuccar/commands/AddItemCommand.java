package org.xfurkanadenia.xtuccar.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.xfurkanadenia.xtuccar.XTuccar;
import org.xfurkanadenia.xtuccar.file.Locale;
import org.xfurkanadenia.xtuccar.model.Category;
import org.xfurkanadenia.xtuccar.model.SubCommand;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AddItemCommand extends SubCommand {
    public AddItemCommand() {
        super("addItem");
    }

    @Override
    public void execute(CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        Locale locale = XTuccar.getInstance().getLocale();
        if(!ValidatePermission(sender, "xtuccar.addItem")) return;
        if(!ValidateIsPlayer(sender)) return;
        if(args.length < 2) {
            locale.sendMessage(sender, "usages.addItem", Map.of());
            return;
        }
        Player player = (Player) sender;
        ItemStack item = player.getInventory().getItemInMainHand();
        XTuccar plugin = XTuccar.getInstance();
        plugin.getMarketItemsCfg().getConfig().set(args[0] + ".category", args[1]);
        plugin.getMarketItemsCfg().getConfig().set(args[0] + ".item", item.serialize());
        plugin.getMarketItemsCfg().saveConfig();
    }

    @Override
    public List<String> tabComplete(CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        XTuccar plugin = XTuccar.getInstance();
        Locale locale = plugin.getLocale();
        if(args.length == 1) return List.of(locale.getMessage("completers.item-name"));
        if(args.length == 2) return plugin.getCategories().stream().map(Category::getName).toList();
        return List.of();
    }
}
