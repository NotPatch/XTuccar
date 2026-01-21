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
            locale.sendMessage(player, "player-hand-empty");
            return;
        }
        if(marketItem == null) {
            locale.sendMessage(player, "item-not-found");
            return;
        }

        if(args.length == 0) {
            locale.sendMessage(player, "usages.changePrice");
        }

        if(!Utils.ValidateDouble(player, args[0])) return;

        double price = Double.parseDouble(args[0]);
        tuccarManager.changePriceItem(sender.getName(), marketItem.getItemId(), price, changed -> {
            if(changed) {
                locale.sendMessage(player, "item-price-changed");
            } else {
                locale.sendMessage(player, "item-price-not-changed");
            }
        });
    }
}
