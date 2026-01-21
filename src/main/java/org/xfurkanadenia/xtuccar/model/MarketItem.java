package org.xfurkanadenia.xtuccar.model;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.xfurkanadenia.xtuccar.XTuccar;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MarketItem {
    private ItemStack item;
    private String itemid;
    private String displayName;
    private Category category;
    public MarketItem(MarketItem marketItem) {
        this.item = new ItemStack(marketItem.getItem());
        this.itemid = marketItem.getItemId();
        this.category = marketItem.getCategory();
    }
    public MarketItem(String itemid) {
        FileConfiguration items = XTuccar.getInstance().getMarketItemsCfg().getConfig();
        List<Category> categories = XTuccar.getInstance().getCategories();
        if(!items.contains(itemid)) throw new IllegalArgumentException("Item " + itemid + " does not exist");
        ConfigurationSection itemCfg = items.getConfigurationSection(itemid);

        String category = itemCfg.getString("category", "");
        if(!categories.stream().anyMatch(v -> v.getName().equalsIgnoreCase(category))) throw new IllegalArgumentException("Category " + category + " does not exist");
        this.item = ItemStack.deserialize(itemCfg.getConfigurationSection("item").getValues(false));
        if(this.item == null) throw new IllegalArgumentException("Item " + itemid + " does not exist");
        if(itemCfg.contains("displayName")) displayName = itemCfg.getString("displayName");
        this.itemid = itemid;
        this.category = categories.stream().filter(v -> v.getName().equalsIgnoreCase(itemCfg.getString("category", ""))).findFirst().orElse(null);
    }

    public ItemStack getItem() {
        return item;
    }

    public Category getCategory() {
        return category;
    }

    public String getItemId() {
        return itemid;
    }
    public String getDisplayName() {
        return displayName;
    }
}
