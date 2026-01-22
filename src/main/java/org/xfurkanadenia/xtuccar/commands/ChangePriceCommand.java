package org.xfurkanadenia.xtuccar.commands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.xfurkanadenia.xtuccar.XTuccar;
import org.xfurkanadenia.xtuccar.file.Locale;
import org.xfurkanadenia.xtuccar.manager.TuccarManager;
import org.xfurkanadenia.xtuccar.model.MarketItem;
import org.xfurkanadenia.xtuccar.model.SubCommand;
import org.xfurkanadenia.xtuccar.util.Utils;

import java.util.Map;

public class ChangePriceCommand extends SubCommand {
    public ChangePriceCommand() {
        super(XTuccar.getInstance().getConfig().getString("subcommands.change-price"));
    }

    @Override
    public void execute(CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        XTuccar plugin = XTuccar.getInstance();
        TuccarManager tuccarManager = plugin.getTuccarManager();
        Locale locale = plugin.getLocale();
        Player player = (Player) sender;
        ItemStack item = player.getInventory().getItemInMainHand();
        MarketItem marketItem = tuccarManager.getMarketItem(item);
        if(!ValidateIsPlayer(sender)) return;
        if(item == null || item.getType() == Material.AIR) {
            locale.sendMessage(player, "player-hand-empty", Map.of());
            return;
        }
        if(marketItem == null) {
            locale.sendMessage(player, "item-not-found", Map.of());
            return;
        }

        if(args.length == 0) {
            locale.sendMessage(player, "usages.changePrice", Map.of());
        }

        if(!Utils.ValidateDouble(player, args[0])) return;

        double price = Double.parseDouble(args[0]);
        tuccarManager.changePriceItem(sender.getName(), marketItem.getItemId(), price, changed -> {
            if(changed) {
                locale.sendMessage(player, "item-price-changed", Utils.getItemPlaceholders(marketItem));
            } else {
                locale.sendMessage(player, "item-price-not-changed",  Utils.getItemPlaceholders(marketItem));
            }
        });
    }
}
