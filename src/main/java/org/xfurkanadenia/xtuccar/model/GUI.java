package org.xfurkanadenia.xtuccar.model;

import org.xfurkanadenia.xtuccar.util.ActionUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GUI {
    private String title;
    private Set<GUIItem> items;
    private int size;
    public GUI(int size) {
        this("Chest", size, new HashSet<>());
    }
    public GUI (String title, int size) {
        this(title, size, new HashSet<>());
    }
    public GUI(String title, int size, Set<GUIItem> items) {
        this.title = title;
        this.size = size;
        this.items = items;
        this.items.forEach(item -> item.setActions(ActionUtils.getActions(item.getRawActions())));
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setItems(Set<GUIItem> items) {
        this.items = items;
    }

    public int getSize() {
        return size;
    }

    public Set<GUIItem> getItems() {
        return items;
    }

    public GUIItem getItem(int slot) {
        return items.stream().filter(itm -> itm.getSlots().contains(slot)).findFirst().orElse(null);
    }

    public void setItems(List<GUIItem> items) {
        for(GUIItem item : items) {
            setItem(item);
        }
    }

    public void setItem(GUIItem item) {
        this.items.forEach(itm -> itm.getSlots().removeAll(item.getSlots()));
        item.setActions(ActionUtils.getActions(item.getRawActions()));
        this.items.add(item);
    }

}
