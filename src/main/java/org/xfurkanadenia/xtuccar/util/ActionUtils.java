package org.xfurkanadenia.xtuccar.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.xfurkanadenia.xtuccar.XTuccar;
import org.xfurkanadenia.xtuccar.manager.GUIManager;
import org.xfurkanadenia.xtuccar.model.Action;
import org.xfurkanadenia.xtuccar.model.GUI;
import org.xfurkanadenia.xtuccar.model.GUIItem;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ActionUtils {
    private static final Pattern actionPattern = Pattern.compile("\\[(.*?)]\\s*(.*)");
    public static List<Action> getActions(List<String> rawActions) {
        return parseActions(rawActions);
    }
    public static List<Action> getActions(String guiName, Integer slot) {
        XTuccar plugin = XTuccar.getInstance();
        GUIManager guiManager = plugin.getGUIManager();
        GUI gui = guiManager.getGuis().get(guiName);

        if (gui == null || gui.getItems().isEmpty()) return List.of();

        GUIItem item = gui.getItem(slot);
        if (item == null) return List.of();


        if (item.getActions().isEmpty()) return List.of();

        return parseActions(item.getRawActions());
    }


    public static List<Action> parseActions(List<String> rawActions) {
        List<Action> actions = new ArrayList<>();
        for (String action : rawActions) {
            if (action == null || action.trim().isEmpty()) continue;
            Matcher matcher = actionPattern.matcher(action.trim());
            if (matcher.matches()) {
                String type = matcher.group(1).trim();
                String value = matcher.group(2) != null ? matcher.group(2).trim() : "";
                actions.add(new Action(type, value));
            }
        }
        return actions;
    }

    public static void executeActions(Player player, List<Action> actions, Map<String, String> vars) {
        actions.forEach(action -> {
            String value = action.getValue();
            System.out.println(action + " " + value);
            switch (action.getName().toLowerCase()) {
                case "player":
                    Bukkit.dispatchCommand(player, Utils.placeholders(value, player, vars));
                    break;
                case "console":
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), Utils.placeholders(value, player, vars));
                    break;
                case "message":
                    player.sendMessage(Utils.placeholders(value, player, vars));
                    break;
                case "msg":
                    player.sendMessage(Utils.translateColorCodes(Utils.placeholders(value, player, vars)));
                    break;
                case "close":
                    player.closeInventory();
                    break;
                case "broadcast":
                    Bukkit.broadcastMessage(Utils.translateColorCodes(Utils.placeholders(value, player, vars)));
                    break;
            }
        });
    }
}
