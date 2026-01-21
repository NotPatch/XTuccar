package org.xfurkanadenia.xtuccar.model;

import org.bukkit.Material;
import org.bukkit.entity.Cat;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;
import java.util.List;

public class Category {
    private String name;
    private List<String> lore;
    private String displayName;
    private Material material;
    private int amount;
    private int customModelData = -1;
    public Category(Category category) {
        this.name = category.getName();
        this.lore = category.getLore();
        this.displayName = category.getDisplayName();
        this.material = category.getMaterial();
    }

    public Category(String name, Material material) {
        this.name = name;
        this.material = material;
        this.lore = new ArrayList<>();
        this.amount = 1;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public List<String> getLore() {
        return lore;
    }

    public Material getMaterial() {
        return material;
    }

    public int getAmount() {
        return amount;
    }

    public int getCustomModelData() {
        return customModelData;
    }

    public ItemStack getItem() {
        ItemStack item = new ItemStack(material);
        item.setAmount(amount);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(displayName);
        meta.setLore(lore);
        if(customModelData != -1) meta.setCustomModelData(customModelData);
        item.setItemMeta(meta);
        return item;
    }

    public void setLore(List<String> lore) {
        this.lore = lore;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setCustomModelData(int customModelData) {
        this.customModelData = customModelData;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setName(String name) {
        this.name = name;
    }
}
