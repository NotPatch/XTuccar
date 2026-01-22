package org.xfurkanadenia.xtuccar.gui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.xfurkanadenia.xtuccar.XTuccar;
import org.xfurkanadenia.xtuccar.manager.TuccarManager;
import org.xfurkanadenia.xtuccar.model.Category;
import org.xfurkanadenia.xtuccar.model.MarketItem;
import org.xfurkanadenia.xtuccar.model.cFastInv;
import org.xfurkanadenia.xtuccar.util.Utils;

import java.util.List;

public class TuccarCategoryItemsMenu extends cFastInv {
    public TuccarCategoryItemsMenu(Category category, Player player) {
        super("tuccarCategoryItems", Utils.getCategoryPlaceholders(category));
        XTuccar plugin = XTuccar.getInstance();
        TuccarManager tuccarManager = plugin.getTuccarManager();
        List<MarketItem> items = tuccarManager.getCategoryItems(category);
        getItems().forEach(item -> {
            switch (item.getType().toLowerCase()) {
                case "items" -> {
                    if(items.isEmpty()) return;
                    items.forEach(mItem -> {
                        if(item.getSlots().size() - 1 < items.indexOf(mItem)) return;
                        int slot = item.getSlots().get(items.indexOf(mItem));
                        MarketItem _mItem = new MarketItem(mItem);
                        ItemStack itemStack = _mItem.getItem();
                        ItemMeta itemMeta = itemStack.getItemMeta();
                        itemStack.setAmount(1);
                        itemMeta.setLore(item.getItem().getItemMeta().getLore());
                        itemStack.setItemMeta(itemMeta);
                        setItem(slot, Utils.getFormattedItem(itemStack, player, Utils.getItemPlaceholders(mItem), false), e -> {
                            if(items.size() - 1 < items.indexOf(mItem)) return;
                            new TuccarSellingItemsMenu(player, mItem.getItemId(), 1).open(player);
                        });
                    });
                }
                case "back" -> setItems(item.getSlots(), Utils.getFormattedItem(item.getItem(), player, Utils.getCategoryPlaceholders(category)), e -> {
                        new TuccarCategoryMenu(player).open(player);
                    });

                default -> item.getSlots().forEach(slot -> setItem(slot, Utils.getFormattedItem(item.getItem(), player, Utils.getCategoryPlaceholders(category))));

            }
        });
    }
}
