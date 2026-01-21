package org.xfurkanadenia.xtuccar.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.xfurkanadenia.xtuccar.XTuccar;
import org.xfurkanadenia.xtuccar.file.Locale;
import org.xfurkanadenia.xtuccar.gui.BuyConfirmMenu;
import org.xfurkanadenia.xtuccar.gui.TuccarSellingItemsMenu;
import org.xfurkanadenia.xtuccar.manager.DataManager;
import org.xfurkanadenia.xtuccar.model.MarketSellingItem;
import org.xfurkanadenia.xtuccar.model.Validator;
import org.xfurkanadenia.xtuccar.util.Utils;

import java.util.Map;

public class PlayerChatListener implements Listener {
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        XTuccar plugin = XTuccar.getInstance();
        Locale locale = plugin.getLocale();
        DataManager dataManager = plugin.getDataManager();
        Map<Player, MarketSellingItem> playerItemBuyChat = dataManager.getPlayerItemBuyChat();
        String message = e.getMessage();
        if(playerItemBuyChat.containsKey(e.getPlayer())) {
            e.setCancelled(true);
            MarketSellingItem item = playerItemBuyChat.get(e.getPlayer());
            if(message.equalsIgnoreCase(locale.getMessage("cancel"))) {
                Bukkit.getScheduler().runTask(plugin, () -> new TuccarSellingItemsMenu(e.getPlayer(), item.getItemId(), 1).open(e.getPlayer()));
                locale.sendMessage(e.getPlayer(), "cancelled");
                playerItemBuyChat.remove(e.getPlayer());
                return;
            }

            if(!Utils.ValidateInteger(e.getPlayer(), message)) return;
            int amount = Integer.parseInt(message);
            if(!Validator.ValidateStock(item, amount, e.getPlayer())) return;
            if(!Validator.ValidateMoney(e.getPlayer(), amount * item.getPrice())) return;
            if(!Validator.ValidateHasSpace(e.getPlayer(), item.getMarketItem().getItem(), amount)) return;
            Bukkit.getScheduler().runTask(plugin, () -> new BuyConfirmMenu(item, e.getPlayer(), amount).open(e.getPlayer()));
            playerItemBuyChat.remove(e.getPlayer());
        }
    }
}
