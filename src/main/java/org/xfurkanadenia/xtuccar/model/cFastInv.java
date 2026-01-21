package org.xfurkanadenia.xtuccar.model;

import fr.mrmicky.fastinv.FastInv;
import org.bukkit.entity.Player;
import org.xfurkanadenia.xtuccar.XTuccar;
import org.xfurkanadenia.xtuccar.util.ActionUtils;
import org.xfurkanadenia.xtuccar.util.Utils;

import java.util.Map;
import java.util.Set;

import static org.xfurkanadenia.xtuccar.manager.GUIManager.openedGuis;

public abstract class cFastInv extends FastInv {
    private GUI gui;

    public cFastInv(String guiName) {
        this(guiName, Map.of());
    }

    public cFastInv(String guiName, Map<String, String> vars) {
        super(
                XTuccar.getInstance().getGUIManager().getGui(guiName).getSize(),
                Utils.getFormatted(XTuccar.getInstance().getGUIManager().getGui(guiName).getTitle(), vars)
        );
        this.gui = XTuccar.getInstance().getGUIManager().getGui(guiName);
    }

    public GUI getGui() {
        return gui;
    }

    public String getTitle() {
        return gui.getTitle();
    }
    public Set<GUIItem> getItems() {
        return gui.getItems();
    }
    public void open(Player player) {
        addCloseHandler(e -> openedGuis.remove(player));
        addOpenHandler(e -> openedGuis.put(player, this));
        addClickHandler(e -> ActionUtils.executeActions(player, getGui().getItem(e.getRawSlot()).getActions(), Map.of()));
        super.open(player);
    }
}
