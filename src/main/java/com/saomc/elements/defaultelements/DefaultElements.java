package com.saomc.elements.defaultelements;

import com.saomc.api.screens.GuiSelection;
import com.saomc.api.screens.IElement;
import com.saomc.screens.inventory.InventoryCore;
import com.saomc.util.IconCore;
import com.saomc.util.LogCore;

/**
 * These are all the default elements used within the UI
 * <p>
 * Created by Tencao on 31/07/2016.
 */
public class DefaultElements extends IElement {

    public void dispatch() {
        LogCore.logInfo("Starting Element Dispatch");
        ingameMenu();
    }

    private void ingameMenu() {
        addMenu("Profile", IconCore.PROFILE, GuiSelection.IngameMenuGUI);
        addMenu("Social", IconCore.SOCIAL, GuiSelection.IngameMenuGUI);
        addMenu("Message", IconCore.MESSAGE, GuiSelection.IngameMenuGUI);
        addMenu("Navigation", IconCore.NAVIGATION, GuiSelection.IngameMenuGUI);
        addMenu("Settings", IconCore.SETTINGS, GuiSelection.IngameMenuGUI);

        addSlot("Equipment", "Profile", IconCore.EQUIPMENT, GuiSelection.IngameMenuGUI);
        addSlot("Items", "Profile", IconCore.ITEMS, GuiSelection.IngameMenuGUI);
        addSlot("Skills", "Profile", IconCore.SKILLS, GuiSelection.IngameMenuGUI);

        addSlot("Guild", "Social", IconCore.GUILD, GuiSelection.IngameMenuGUI);
        addSlot("Party", "Social", IconCore.PARTY, GuiSelection.IngameMenuGUI);
        addSlot("Friends", "Social", IconCore.FRIEND, GuiSelection.IngameMenuGUI);

        addSlot("Message Box", "Message", IconCore.MESSAGE, GuiSelection.IngameMenuGUI);

        addSlot("Quests", "Navigation", IconCore.QUEST, GuiSelection.IngameMenuGUI);
        addSlot("Field Map", "Navigation", IconCore.FIELD_MAP, GuiSelection.IngameMenuGUI);
        addSlot("Dungeon Map", "Navigation", IconCore.DUNGEON_MAP, GuiSelection.IngameMenuGUI);

        addSlot("Options", "Settings", IconCore.OPTION, GuiSelection.IngameMenuGUI);
        addSlot("Menu", "Settings", IconCore.HELP, GuiSelection.IngameMenuGUI);
        addSlot("Logout", "Settings", IconCore.LOGOUT, GuiSelection.IngameMenuGUI);

        addSlot("Tools", "Equipment", IconCore.EQUIPMENT, GuiSelection.IngameMenuGUI);
        addSlot("Armor", "Equipment", IconCore.ARMOR, GuiSelection.IngameMenuGUI);
        if (InventoryCore.isBaublesLoaded())
            addSlot("Accessory", "Equipment", IconCore.ACCESSORY, GuiSelection.IngameMenuGUI);
        addSlot("Consumables", "Navigation", IconCore.ITEMS, GuiSelection.IngameMenuGUI);

        // Items need adding

        // Skills need adding
        addSlot("Quests", "Navigation", IconCore.QUEST, GuiSelection.IngameMenuGUI);
        addSlot("Quests", "Navigation", IconCore.QUEST, GuiSelection.IngameMenuGUI);
        addSlot("Quests", "Navigation", IconCore.QUEST, GuiSelection.IngameMenuGUI);
        LogCore.logInfo("Finished Element Dispatch");
    }

}
