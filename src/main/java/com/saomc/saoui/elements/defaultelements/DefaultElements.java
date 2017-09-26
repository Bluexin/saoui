package com.saomc.saoui.elements.defaultelements;

import com.saomc.saoui.SAOCore;
import com.saomc.saoui.api.screens.GuiSelection;
import com.saomc.saoui.api.screens.IElementBuilder;
import com.saomc.saoui.elements.ElementProvider;
import com.saomc.saoui.screens.inventory.InventoryCore;
import com.saomc.saoui.util.IconCore;

/**
 * These are all the default elements used within the UI
 * <p>
 * Created by Tencao on 31/07/2016.
 */
public class DefaultElements {

    public static void dispatch() {
        SAOCore.INSTANCE.getLOGGER().debug("Starting Element Dispatch");
        ingameMenu();
    }

    public static void dispatchItemFilter() {
        IElementBuilder b = ElementProvider.Companion.instance().getBuilder();

        b.addInventory("Weapons", GuiSelection.IngameMenuGUI, InventoryCore.WEAPONS);
        b.addInventory("Bows", GuiSelection.IngameMenuGUI, InventoryCore.BOWS);
        b.addInventory("Pickaxe", GuiSelection.IngameMenuGUI, InventoryCore.PICKAXE);
        b.addInventory("Axe", GuiSelection.IngameMenuGUI, InventoryCore.AXE);
        b.addInventory("Shovel", GuiSelection.IngameMenuGUI, InventoryCore.SHOVEL);
    }

    private static void ingameMenu() {
        IElementBuilder b = ElementProvider.Companion.instance().getBuilder();

        b.addMenu("Profile", IconCore.PROFILE, GuiSelection.IngameMenuGUI);
        b.addMenu("Social", IconCore.SOCIAL, GuiSelection.IngameMenuGUI);
        b.addMenu("Message", IconCore.MESSAGE, GuiSelection.IngameMenuGUI);
        b.addMenu("Navigation", IconCore.NAVIGATION, GuiSelection.IngameMenuGUI);
        b.addMenu("Settings", IconCore.SETTINGS, GuiSelection.IngameMenuGUI);

        //Profile
        b.addSlot("Equipment", "Profile", IconCore.EQUIPMENT, GuiSelection.IngameMenuGUI);
        b.addSlot("Items", "Profile", IconCore.ITEMS, GuiSelection.IngameMenuGUI);
        b.addSlot("Skills", "Profile", IconCore.SKILLS, GuiSelection.IngameMenuGUI);

        //Equipment
        b.addSlot("Tools", "Equipment", IconCore.EQUIPMENT, GuiSelection.IngameMenuGUI);
        b.addSlot("Armor", "Equipment", IconCore.ARMOR, GuiSelection.IngameMenuGUI);
        if (InventoryCore.isBaublesLoaded())
            b.addSlot("Accessory", "Equipment", IconCore.ACCESSORY, GuiSelection.IngameMenuGUI);
        b.addSlot("Consumables", "Equipment", IconCore.ITEMS, GuiSelection.IngameMenuGUI);

        //Tools
        b.addSlot("Weapons", "Tools", IconCore.EQUIPMENT, GuiSelection.IngameMenuGUI);
        b.addSlot("Bows", "Tools", IconCore.EQUIPMENT, GuiSelection.IngameMenuGUI);
        b.addSlot("Pickaxe", "Tools", IconCore.EQUIPMENT, GuiSelection.IngameMenuGUI);
        b.addSlot("Axe", "Tools", IconCore.EQUIPMENT, GuiSelection.IngameMenuGUI);
        b.addSlot("Shovel", "Tools", IconCore.EQUIPMENT, GuiSelection.IngameMenuGUI);

        //Social
        b.addSlot("Guild", "Social", IconCore.GUILD, GuiSelection.IngameMenuGUI);
        b.addSlot("Party", "Social", IconCore.PARTY, GuiSelection.IngameMenuGUI);
        b.addSlot("Friends", "Social", IconCore.FRIEND, GuiSelection.IngameMenuGUI);

        //Party
        b.addSlot("Invite", "Party", IconCore.INVITE, GuiSelection.IngameMenuGUI);
        b.addSlot("Dissolve", "Party", IconCore.CANCEL, GuiSelection.IngameMenuGUI);

        //Message
        b.addSlot("Message Box", "Message", IconCore.MESSAGE, GuiSelection.IngameMenuGUI);

        //Navigation
        b.addSlot("Quests", "Navigation", IconCore.QUEST, GuiSelection.IngameMenuGUI);
        b.addSlot("Field Map", "Navigation", IconCore.FIELD_MAP, GuiSelection.IngameMenuGUI);
        b.addSlot("Dungeon Map", "Navigation", IconCore.DUNGEON_MAP, GuiSelection.IngameMenuGUI);

        //Settings
        b.addSlot("Options", "Settings", IconCore.OPTION, GuiSelection.IngameMenuGUI);
        b.addSlot("Menu", "Settings", IconCore.HELP, GuiSelection.IngameMenuGUI);
        b.addSlot("Logout", "Settings", IconCore.LOGOUT, GuiSelection.IngameMenuGUI);


        // Items need adding
        // Skills need adding

        //Testing purposes only
        b.addSlot("Test", "Profile", IconCore.OPTION, GuiSelection.IngameMenuGUI);
        b.addSlot("Prompt", "Profile", IconCore.OPTION, GuiSelection.IngameMenuGUI);
        b.addSlot("Slot 1", "Test", IconCore.QUEST, GuiSelection.IngameMenuGUI);
        b.addSlot("Slot 2", "Test", IconCore.QUEST, GuiSelection.IngameMenuGUI);
        b.addSlot("Slot 3", "Test", IconCore.QUEST, GuiSelection.IngameMenuGUI);
        b.addSlot("Slot 4", "Test", IconCore.QUEST, GuiSelection.IngameMenuGUI);
        b.addSlot("Slot 5", "Test", IconCore.QUEST, GuiSelection.IngameMenuGUI);
        b.addSlot("Slot 6", "Test", IconCore.QUEST, GuiSelection.IngameMenuGUI);
        b.addSlot("Slot 7", "Test", IconCore.QUEST, GuiSelection.IngameMenuGUI);
        b.addSlot("Slot 8", "Test", IconCore.QUEST, GuiSelection.IngameMenuGUI);
        b.addSlot("Slot 9", "Test", IconCore.QUEST, GuiSelection.IngameMenuGUI);
        b.addSlot("Slot 10", "Test", IconCore.QUEST, GuiSelection.IngameMenuGUI);
        b.addSlot("Slot 11", "Test", IconCore.QUEST, GuiSelection.IngameMenuGUI);
        b.addSlot("Slot 12", "Test", IconCore.QUEST, GuiSelection.IngameMenuGUI);

        //Stream.of(OptionCore.values()).filter(option -> option.isCategory() && option.getCategory() == null).forEachOrdered(option -> b.addSlot(option.getName(), "Options", IconCore.OPTION, GuiSelection.IngameMenuGUI));
        //Stream.of(OptionCore.values()).filter(option -> option.getCategory() != null).forEachOrdered(option -> ElementBuilder.Companion.getInstance().addOption(option.getName(), option.getCategoryName(), IconCore.OPTION, GuiSelection.IngameMenuGUI, option));
        SAOCore.INSTANCE.getLOGGER().debug("Finished Element Dispatch");
    }

}
