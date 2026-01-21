package org.xfurkanadenia.xtuccar.model;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class GUIItem {
    private ItemStack item;
    private List<Integer> slots;
    private String type = "";
    private List<String> rawActions = new ArrayList<>();
    private List<Action> actions = new ArrayList<>();
    public GUIItem(ItemStack item) {
        this.item = item;
    }
    public GUIItem(ItemStack item, List<Integer> slots) {
        this.item = item;
        this.slots = slots;
    }
    public GUIItem(Material material, List<Integer> slots, int amount) {
        item = new ItemStack(material, amount);
        this.slots = slots;
    }
    public GUIItem(Material material, List<Integer> slots) {
        this.item = new ItemStack(material);
        this.slots = slots;
    }

    public List<Integer> getSlots() {
        return slots;
    }

    public void setSlots(List<Integer> slot) {
        this.slots = slot;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    public ItemStack getItem() {
        return item;
    }

    public void setCustoModelData(int modelData) {
        if(!item.hasItemMeta()) return;
        ItemMeta meta = item.getItemMeta();
        meta.setCustomModelData(modelData);
        item.setItemMeta(meta);
    }

    public int getCustomModelData() {
        if(!item.hasItemMeta()) return 0;
        ItemMeta meta = item.getItemMeta();
        return meta.getCustomModelData();
    }

    public void setDisplayName(String displayName) {
        if(!item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) return;
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(displayName);
        item.setItemMeta(meta);
    }

    public String getDisplayName() {
        if(!item.hasItemMeta()) return "";
        ItemMeta meta = item.getItemMeta();
        return meta.getDisplayName();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    public List<Action> getActions() {
        return actions;
    }
    public List<String> getRawActions() {
        return rawActions;
    }
    public void setRawActions(List<String> rawActions) {
        this.rawActions = rawActions;
    }
    public void setActions(List<Action> actions) {
        this.actions = actions;
    }
    public void removeAction(String action) {
        Action item = getActions().stream().filter(a -> a.getName().equalsIgnoreCase(action)).findFirst().orElse(null);
        int index = actions.indexOf(item);
        if(item == null) return;
        actions.remove(index);
    }
}
