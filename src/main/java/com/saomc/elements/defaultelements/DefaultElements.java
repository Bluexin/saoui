package com.saomc.elements.defaultelements;

import com.saomc.api.screens.GuiSelection;
import com.saomc.api.screens.IElementBuilder;
import com.saomc.elements.ElementProvider;
import com.saomc.screens.inventory.InventoryCore;
import com.saomc.util.IconCore;
import com.saomc.util.LogCore;

/**
 * These are all the default elements used within the UI
 * <p>
 * Created by Tencao on 31/07/2016.
 */
public class DefaultElements {

    public static void dispatch() {
        LogCore.logInfo("Starting Element Dispatch");
        ingameMenu();
    }

    private static void ingameMenu() {
        IElementBuilder b = ElementProvider.instance().getBuilder();

        b.addMenu("Profile", IconCore.PROFILE, GuiSelection.IngameMenuGUI);
        b.addMenu("Social", IconCore.SOCIAL, GuiSelection.IngameMenuGUI);
        b.addMenu("Message", IconCore.MESSAGE, GuiSelection.IngameMenuGUI);
        b.addMenu("Navigation", IconCore.NAVIGATION, GuiSelection.IngameMenuGUI);
        b.addMenu("Settings", IconCore.SETTINGS, GuiSelection.IngameMenuGUI);

        b.addSlot("Equipment", "Profile", IconCore.EQUIPMENT, GuiSelection.IngameMenuGUI);
        b.addSlot("Items", "Profile", IconCore.ITEMS, GuiSelection.IngameMenuGUI);
        b.addSlot("Skills", "Profile", IconCore.SKILLS, GuiSelection.IngameMenuGUI);

        b.addSlot("Guild", "Social", IconCore.GUILD, GuiSelection.IngameMenuGUI);
        b.addSlot("Party", "Social", IconCore.PARTY, GuiSelection.IngameMenuGUI);
        b.addSlot("Friends", "Social", IconCore.FRIEND, GuiSelection.IngameMenuGUI);

        b.addSlot("Message Box", "Message", IconCore.MESSAGE, GuiSelection.IngameMenuGUI);

        b.addSlot("Quests", "Navigation", IconCore.QUEST, GuiSelection.IngameMenuGUI);
        b.addSlot("Field Map", "Navigation", IconCore.FIELD_MAP, GuiSelection.IngameMenuGUI);
        b.addSlot("Dungeon Map", "Navigation", IconCore.DUNGEON_MAP, GuiSelection.IngameMenuGUI);

        b.addSlot("Options", "Settings", IconCore.OPTION, GuiSelection.IngameMenuGUI);
        b.addSlot("Menu", "Settings", IconCore.HELP, GuiSelection.IngameMenuGUI);
        b.addSlot("Logout", "Settings", IconCore.LOGOUT, GuiSelection.IngameMenuGUI);

        b.addSlot("Tools", "Equipment", IconCore.EQUIPMENT, GuiSelection.IngameMenuGUI);
        b.addSlot("Armor", "Equipment", IconCore.ARMOR, GuiSelection.IngameMenuGUI);
        if (InventoryCore.isBaublesLoaded())
            b.addSlot("Accessory", "Equipment", IconCore.ACCESSORY, GuiSelection.IngameMenuGUI);
        b.addSlot("Consumables", "Navigation", IconCore.ITEMS, GuiSelection.IngameMenuGUI);

        // Items need adding
        // Skills need adding
        b.addSlot("Quests", "Navigation", IconCore.QUEST, GuiSelection.IngameMenuGUI);
        b.addSlot("Quests", "Navigation", IconCore.QUEST, GuiSelection.IngameMenuGUI);
        b.addSlot("Quests", "Navigation", IconCore.QUEST, GuiSelection.IngameMenuGUI);
        LogCore.logInfo("Finished Element Dispatch");
    }

}
