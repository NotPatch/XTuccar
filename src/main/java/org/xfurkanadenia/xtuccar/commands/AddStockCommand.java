package org.xfurkanadenia.xtuccar.commands;

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

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddStockCommand extends SubCommand {
    public AddStockCommand() {
        super(XTuccar.getInstance().getConfig().getString("subcommands.addStock"));
    }

    @Override
    public void execute(CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!ValidateIsPlayer(sender)) return;
        XTuccar main = XTuccar.getInstance();
        TuccarManager tuccarManager = main.getTuccarManager();
        Locale locale = main.getLocale();
        Player player = (Player) sender;
        int amount = -1;
        ItemStack heldItem = player.getInventory().getItemInMainHand();


        String allAmount = locale.getMessage("amounts.all");
        String handAmount = locale.getMessage("amounts.hand");

        if(args.length == 0) {
            locale.sendMessage(player, "usages.addStock");
            return;
        }

        if(args[0].equalsIgnoreCase(allAmount)) amount = Utils.getItemAmount(heldItem, player);
        else if(args[0].equalsIgnoreCase(handAmount)) amount = heldItem.getAmount();

        MarketItem marketItem = tuccarManager.getMarketItem(heldItem);

        if (amount == -1 && !Utils.ValidateInteger(player, args[0])) return;
        if(amount == -1) amount = Integer.parseInt(args[0]);

        Map<String, String> vars = new HashMap<>(Map.of(
                "amount", String.valueOf(amount),
                "amount_formatted", NumberFormat.getInstance().format(amount)
        ));


        if (Utils.getItemAmount(heldItem, player) < amount) {
            sender.sendMessage(String.valueOf(Utils.getItemAmount(heldItem, player)));
            locale.sendMessage(player, "player-not-have-enough-item", vars);
            return;
        }

        if(marketItem == null) {
            locale.sendMessage(player, "item-not-found", vars);
            return;
        }

        // /tüccar addstock 
        int finalAmount = amount;
        tuccarManager.getItemBySeller(player.getName(), marketItem.getItemId(), item -> {
            if (item != null) {
                vars.putAll(Utils.getSellingItemPlaceholders(item));
                vars.put("stock_oldStock", String.valueOf(item.getAmount()));
                boolean removeItem = !args[0].equalsIgnoreCase(handAmount);
                tuccarManager.addStockToItem(player.getName(), heldItem, finalAmount, removeItem, isAdded -> {
                    if (isAdded) {
                        if(!removeItem) player.getInventory().setItemInMainHand(null);
                        vars.put("stock_newStock", String.valueOf(item.getAmount()));
                        locale.sendMessage(player, "item-stock-added", vars);
                    } else {
                        locale.sendMessage(player, "item-stock-not-added", vars);
                    }
                });
            } else {
                locale.sendMessage(player, "player-not-selling-item", vars);
            }
        });
    }

    @Override
    public List<String> tabComplete(CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        XTuccar plugin = XTuccar.getInstance();
        Locale locale = plugin.getLocale();
        if(args.length == 1) return List.of(
                locale.getMessage("completers.amount"),
                locale.getMessage("amounts.all"),
                locale.getMessage("amounts.hand")
                );

        return List.of();
    }
}
