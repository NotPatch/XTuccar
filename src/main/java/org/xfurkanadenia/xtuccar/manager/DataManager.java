package org.xfurkanadenia.xtuccar.manager;

import org.bukkit.entity.Player;
import org.xfurkanadenia.xtuccar.model.MarketSellingItem;

import java.util.HashMap;
import java.util.Map;

public class DataManager {
    private final Map<Player, MarketSellingItem> playerItemBuyChat;
    public DataManager() {
        playerItemBuyChat = new HashMap<>();
    }

    public Map<Player, MarketSellingItem> getPlayerItemBuyChat() {
        return playerItemBuyChat;
    }
}
