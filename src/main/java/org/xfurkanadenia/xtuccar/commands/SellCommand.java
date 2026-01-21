package org.xfurkanadenia.xtuccar.commands;

import net.md_5.bungee.api.ChatColor;
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

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SellCommand extends SubCommand {
    public SellCommand() {
        super(XTuccar.getInstance().getConfig().getString("subcommands.sell"));
    }

    @Override
    public void execute(CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if(!ValidateIsPlayer(sender)) return;
        XTuccar plugin = XTuccar.getInstance();
        TuccarManager tuccarManager = plugin.getTuccarManager();
        Locale locale = plugin.getLocale();
        Player player = (Player) sender;
        ItemStack item = player.getInventory().getItemInMainHand();
        int amount = -1;

        String allAmount = locale.getMessage("amounts.all");
        String handAmount = locale.getMessage("amounts.hand");

        /* /tüccar sell <amount> <price> */
        if(args.length < 2) {
            locale.sendMessage(player, "usages.sell");
            return;
        }

        if(args[0].equalsIgnoreCase(allAmount)) amount = Utils.getItemAmount(item, player);
        else if(args[0].equalsIgnoreCase(handAmount)) amount = item.getAmount();



        if(item == null || item.getType() == Material.AIR) {
            locale.sendMessage(player, "player-hand-empty");
            return;
        }

        if(amount == -1 && !Utils.ValidateInteger(player, args[0])) return;
        if(!Utils.ValidateInteger(player, args[1])) return;

        if(amount == -1) amount = Integer.parseInt(args[0]);
        double price = Double.parseDouble(args[1]);


        Map<String, String> vars = new HashMap<>(Map.of(
                "amount", String.valueOf(amount),
                "amount_formatted", NumberFormat.getInstance().format(amount),
                "price", String.valueOf(price),
                "price_formatted",NumberFormat.getInstance().format(price)
        ));

        MarketItem marketItem = tuccarManager.getMarketItem(item);
        if(marketItem == null) {
            vars.put("item", item.getType().toString());
            locale.sendMessage(player, "item-cant-sell", vars);
            return;
        }

        vars.putAll(Utils.getItemPlaceholders(marketItem));

        if(Utils.getItemAmount(item, player) < amount) {
            sender.sendMessage(String.valueOf(Utils.getItemAmount(item, player)));
            locale.sendMessage(player, "player-not-have-enough-item", vars);
            return;
        }


        int finalAmount = amount;
        tuccarManager.isPlayerSellingItem(sender.getName(), marketItem.getItemId(), (isSelling) -> {
            if(!isSelling) {
                boolean isSelled = tuccarManager.sellItem(player.getName(), item, price, finalAmount);
                    if(isSelled) {
                        locale.sendMessage(player, "item-selled", vars);
                    } else {
                        locale.sendMessage(player, "item-not-selled", vars);
                    }

            } else {
                locale.sendMessage(player, "player-already-selling-item", vars);
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
                locale.getMessage("amounts.hand"));
        if(args.length == 2) return List.of(locale.getMessage("completers.price"));
        return List.of();
    }
}
