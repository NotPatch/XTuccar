package org.xfurkanadenia.xtuccar.gui;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.xfurkanadenia.xtuccar.XTuccar;
import org.xfurkanadenia.xtuccar.model.MarketSellingItem;
import org.xfurkanadenia.xtuccar.model.Validator;
import org.xfurkanadenia.xtuccar.model.cFastInv;
import org.xfurkanadenia.xtuccar.util.Utils;

public class BuyConfirmMenu extends cFastInv {
    public BuyConfirmMenu(MarketSellingItem marketSellingItem, Player player, int amount) {
        super("buyConfirm", Utils.getSellingItemPlaceholders(marketSellingItem));
        getItems().forEach(item -> {
            int[] slots = item.getSlots().stream().mapToInt(Integer::intValue).toArray();
            Economy economy = XTuccar.getInstance().getVaultIntegration().getEconomy();
            setItems(slots, Utils.getFormattedItem(item.getItem(), player, Utils.getSellingItemPlaceholders(marketSellingItem)), e -> {
                switch (item.getType().toLowerCase()) {
                    case "confirm" -> {
                        OfflinePlayer seller = Bukkit.getOfflinePlayer(marketSellingItem.getSeller());
                        double price = amount * marketSellingItem.getPrice();
                        if(!Validator.ValidateStock(marketSellingItem, amount, player)) {
                            openMenu(player, marketSellingItem);
                            return;
                        }
                        if(!Validator.ValidateMoney(player, price)) {
                            openMenu(player, marketSellingItem);
                            return;
                        }
                        if(!Validator.ValidateHasSpace(player, marketSellingItem.getMarketItem().getItem())) {
                            openMenu(player, marketSellingItem);
                            return;
                        }
                        marketSellingItem.removeAmount(amount);
                        economy.withdrawPlayer(player, amount);
                        economy.depositPlayer(seller, amount);
                        ItemStack sItem = new ItemStack(marketSellingItem.getMarketItem().getItem());
                        sItem.setAmount(amount);
                        player.getInventory().addItem(sItem);
                        player.sendMessage(marketSellingItem.getAmount().toString());
                        if(marketSellingItem.getAmount() <= 0) XTuccar.getInstance().getTuccarManager().removeSellingItem(marketSellingItem, v -> openMenu(player, marketSellingItem));
                        else openMenu(player, marketSellingItem);
                    }
                    case "cancel" -> openMenu(player, marketSellingItem);

                }
            });
        });
    }

    private void openMenu(Player player, MarketSellingItem marketSellingItem) {
        Bukkit.getScheduler().runTask(XTuccar.getInstance(), () -> new TuccarSellingItemsMenu(player, marketSellingItem.getItemId(), 1).open(player));
    }
}
