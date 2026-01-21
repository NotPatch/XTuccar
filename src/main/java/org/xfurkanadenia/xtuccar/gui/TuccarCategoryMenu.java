package org.xfurkanadenia.xtuccar.gui;

import org.bukkit.entity.Player;
import org.xfurkanadenia.xtuccar.XTuccar;
import org.xfurkanadenia.xtuccar.model.Category;
import org.xfurkanadenia.xtuccar.model.cFastInv;
import org.xfurkanadenia.xtuccar.util.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TuccarCategoryMenu extends cFastInv {

    private final int page;
    private final Player player;

    public TuccarCategoryMenu(Player player) {
        this(player, 1);
    }

    public TuccarCategoryMenu(Player player, int page) {
        super("tuccarCategory", getPlaceholders(page, Map.of()));
        this.page = page;
        this.player = player;

        setupMenu();
    }

    private void setupMenu() {
        XTuccar plugin = XTuccar.getInstance();
        List<Category> categories = plugin.getCategories();

        var categoriesConfig = getItems().stream()
                .filter(i -> i.getType().equalsIgnoreCase("categories"))
                .findFirst()
                .orElse(null);

        if (categoriesConfig == null) {
            loadDefaultItems();
            return;
        }

        int pageSize = categoriesConfig.getSlots().size();

        // Pagination hesapla
        int startIndex = (page - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, categories.size());
        boolean hasNextPage = endIndex < categories.size();

        getItems().forEach(item -> {
            int[] slots = item.getSlots().stream().mapToInt(Integer::intValue).toArray();

            switch (item.getType().toLowerCase()) {
                case "categories" -> {
                    if (startIndex < categories.size()) {
                        List<Category> pageCategories = categories.subList(startIndex, endIndex);

                        for (int i = 0; i < pageCategories.size() && i < slots.length; i++) {
                            Category category = pageCategories.get(i);
                            int slot = slots[i];

                            setItem(slot, Utils.getFormattedItem(category.getItem(), player, Utils.getCategoryPlaceholders(category)),
                                    e -> new TuccarCategoryItemsMenu(category, player).open(player));
                        }
                    }
                }
                case "previous_page" -> {
                    setItems(slots, Utils.getFormattedItem(item.getItem(), player, getItemPlaceholders(getPlaceholders(page, Map.of()))), e -> {
                        if (page > 1) new TuccarCategoryMenu(player, page - 1).open(player);
                    });
                }
                case "next_page" -> {
                    setItems(slots, Utils.getFormattedItem(item.getItem(), player, getItemPlaceholders(getPlaceholders(page, Map.of()))), e -> {
                            if (hasNextPage) new TuccarCategoryMenu(player, page + 1).open(player);
                    });
                }
                case "player_selling_items" -> {
                    setItems(slots, Utils.getFormattedItem(item.getItem(), player, getItemPlaceholders(getPlaceholders(page, Map.of()))), e -> {
                        new PlayerSellingItemsMenu(player).open(player);
                    });
                }
                default -> {
                    setItems(slots, Utils.getFormattedItem(item.getItem(), player, getItemPlaceholders(getPlaceholders(page, Map.of()))));
                }
            }
        });
    }

    private void loadDefaultItems() {
        // Fallback: pagination config yoksa eski mantık
        XTuccar plugin = XTuccar.getInstance();
        List<Category> categories = plugin.getCategories();

        getItems().forEach(item -> {
            int[] slots = item.getSlots().stream().mapToInt(Integer::intValue).toArray();

            if (item.getType().equalsIgnoreCase("categories")) {
                for (int i = 0; i < Math.min(categories.size(), slots.length); i++) {
                    Category category = categories.get(i);
                    int slot = slots[i];

                    setItem(slot, Utils.getFormattedItem(category.getItem(), player, Utils.getCategoryPlaceholders(category)),
                            e -> new TuccarCategoryItemsMenu(category, player).open(player));
                }
            } else {
                setItems(slots, Utils.getFormattedItem(item.getItem(), player, getItemPlaceholders(getPlaceholders(page, Map.of()))));
            }
        });
    }

    private Map<String, String> getItemPlaceholders(Map<String, String> vars) {
        Map<String, String> placeholders = new HashMap<>(vars);
        placeholders.put("page", String.valueOf(page));

        XTuccar plugin = XTuccar.getInstance();
        List<Category> categories = plugin.getCategories();

        var categoriesConfig = getItems().stream()
                .filter(i -> i.getType().equalsIgnoreCase("categories"))
                .findFirst()
                .orElse(null);

        if (categoriesConfig != null) {
            int pageSize = categoriesConfig.getSlots().size();
            int totalPages = (int) Math.ceil((double) categories.size() / pageSize);
            placeholders.put("total_pages", String.valueOf(totalPages));
            placeholders.put("total_categories", String.valueOf(categories.size()));
        }

        return placeholders;
    }

    private static Map<String, String> getPlaceholders(int page, Map<String, String> vars) {
        Map<String, String> placeholders = new HashMap<>(vars);
        placeholders.put("page", String.valueOf(page));

        XTuccar plugin = XTuccar.getInstance();
        if (plugin != null) {
            List<Category> categories = plugin.getCategories();
            placeholders.put("total_categories", String.valueOf(categories.size()));
        }

        return placeholders;
    }
}