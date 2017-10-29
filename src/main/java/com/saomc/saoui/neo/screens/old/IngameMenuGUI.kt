package com.saomc.saoui.neo.screens.old

import com.saomc.saoui.SAOCore
import com.saomc.saoui.SoundCore
import com.saomc.saoui.api.elements.*
import com.saomc.saoui.config.OptionCore
import com.saomc.saoui.screens.inventory.InventoryCore
import com.saomc.saoui.themes.elements.menus.CategoryData
import com.saomc.saoui.util.IconCore
import net.minecraft.client.Minecraft
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.lang.IllegalArgumentException

/**
 * Part of saoui by Bluexin.
 *
 * @author Bluexin
 */
@SideOnly(Side.CLIENT)
class IngameMenuGUI(override val name: String = "In-game menu GUI") : ScreenGUI() {

    private var flowY = 0
    private var flowX = 0
    private var playedSound = false

    override fun drawScreen(cursorX: Int, cursorY: Int, partialTicks: Float) {
        if (!playedSound) { // This trickery is used to circumvent the stupid shit in Minecraft#displayInGameMenu()
            SoundCore.play(mc, SoundCore.ORB_DROPDOWN)
            playedSound = true
        }

        super.drawScreen(cursorX, cursorY, partialTicks)
    }

    override fun mouseReleased(cursorX: Int, cursorY: Int, button: Int) {
        super.mouseReleased(cursorX, cursorY, button)

    }

    override fun initGui() {
        categories.clear()

        /*+category("Main") {
            +category("Profile") {
                categoryIcon(IconCore.PROFILE)
                +category("Equipment") {
                    categoryIconLabel(IconCore.EQUIPMENT)
                    +category("Tools") {
                        categoryIconLabel(IconCore.EQUIPMENT)
                        // TODO: Weapons, Bows, Pickaxes, Axes, Shovels
                    }
                    +category("Armor") {
                        categoryIconLabel(IconCore.ARMOR)
                    }
                    +category("Consumables") {
                        categoryIconLabel(IconCore.ITEMS)
                    }
                }
                +category("Items") {
                    categoryIconLabel(IconCore.ITEMS)
                }
                +category("Skills") {
                    categoryIconLabel(IconCore.SKILLS)
                }
            }
            +category("Social") {
                categoryIcon(IconCore.SOCIAL)
                // TODO: Guild
                +category("Party") {
                    categoryIconLabel(IconCore.PARTY)
                    // TODO: Invite, Cancel
                }
                // TODO: Friends
            }
            +category("Message") {
                categoryIcon(IconCore.MESSAGE)
            }
            +category("Navigation") {
                categoryIcon(IconCore.NAVIGATION)
                // TODO: Quest, Field Map, Dungeon Map
            }
            +category("Settings") {
                categoryIcon(IconCore.SETTINGS)
                +category("Options") {
                    categoryIconLabel(IconCore.OPTION)
                    // TODO: Vanilla_Options
                }
                // TODO: Help, Logout
            }
        }*/

        val list: MutableList<ElementData> = mutableListOf(
                ElementData("Main", null, MenuDefEnum.ICON_BUTTON, IconCore.PROFILE, "Profile", "sao.element.profile", ElementDefEnum.CATEGORY),
                ElementData("Main", null, MenuDefEnum.ICON_BUTTON, IconCore.SOCIAL, "Social", "sao.element.social", ElementDefEnum.CATEGORY),
                ElementData("Main", null, MenuDefEnum.ICON_BUTTON, IconCore.MESSAGE, "Message", "sao.element.message", ElementDefEnum.CATEGORY),
                ElementData("Main", null, MenuDefEnum.ICON_BUTTON, IconCore.NAVIGATION, "Navigation", "sao.element.navigation", ElementDefEnum.CATEGORY),
                ElementData("Main", null, MenuDefEnum.ICON_BUTTON, IconCore.SETTINGS, "Settings", "sao.element.settings", ElementDefEnum.CATEGORY),
                ElementData("Profile", "Main", MenuDefEnum.ICON_LABEL_BUTTON, IconCore.EQUIPMENT, "Equipment", "sao.element.equipment", ElementDefEnum.CATEGORY),
                ElementData("Profile", "Main", MenuDefEnum.ICON_LABEL_BUTTON, IconCore.ITEMS, "Items", "sao.element.items", ElementDefEnum.CATEGORY),
                ElementData("Profile", "Main", MenuDefEnum.ICON_LABEL_BUTTON, IconCore.SKILLS, "Skills", "sao.element.skills", ElementDefEnum.CATEGORY),
                ElementData("Equipment", "Profile", MenuDefEnum.ICON_LABEL_BUTTON, IconCore.EQUIPMENT, "Tools", "sao.element.tools", ElementDefEnum.CATEGORY),
                ElementData("Equipment", "Profile", MenuDefEnum.ICON_LABEL_BUTTON, IconCore.ARMOR, "Armor", "sao.element.armor", ElementDefEnum.CATEGORY),
                ElementData("Equipment", "Profile", MenuDefEnum.ICON_LABEL_BUTTON, IconCore.ITEMS, "Consumables", "sao.element.consumables", ElementDefEnum.CATEGORY),
                ElementData("Tools", "Equipment", MenuDefEnum.ICON_LABEL_BUTTON, IconCore.EQUIPMENT, "Weapons", "sao.element.weapons", ElementDefEnum.CATEGORY),
                ElementData("Tools", "Equipment", MenuDefEnum.ICON_LABEL_BUTTON, IconCore.EQUIPMENT, "Bows", "sao.element.bows", ElementDefEnum.CATEGORY),
                ElementData("Tools", "Equipment", MenuDefEnum.ICON_LABEL_BUTTON, IconCore.EQUIPMENT, "Pickaxe", "sao.element.pickaxe", ElementDefEnum.CATEGORY),
                ElementData("Tools", "Equipment", MenuDefEnum.ICON_LABEL_BUTTON, IconCore.EQUIPMENT, "Axe", "sao.element.axe", ElementDefEnum.CATEGORY),
                ElementData("Tools", "Equipment", MenuDefEnum.ICON_LABEL_BUTTON, IconCore.EQUIPMENT, "Shovel", "sao.element.shovel", ElementDefEnum.CATEGORY),
                ElementData("Social", "Main", MenuDefEnum.ICON_LABEL_BUTTON, IconCore.GUILD, "Guild", "sao.element.guild", ElementDefEnum.CATEGORY),
                ElementData("Social", "Main", MenuDefEnum.ICON_LABEL_BUTTON, IconCore.PARTY, "Party", "sao.element.party", ElementDefEnum.CATEGORY),
                ElementData("Social", "Main", MenuDefEnum.ICON_LABEL_BUTTON, IconCore.FRIEND, "Friends", "sao.element.friends", ElementDefEnum.CATEGORY),
                ElementData("Party", "Social", MenuDefEnum.ICON_LABEL_BUTTON, IconCore.INVITE, "Invite", "sao.element.invite", ElementDefEnum.CATEGORY),
                ElementData("Party", "Social", MenuDefEnum.ICON_LABEL_BUTTON, IconCore.CANCEL, "Dissolve", "sao.element.dissolve", ElementDefEnum.BUTTON),
                ElementData("Message", "Main", MenuDefEnum.ICON_LABEL_BUTTON, IconCore.MESSAGE, "Message Box", "sao.element.message_box", ElementDefEnum.CATEGORY),
                ElementData("Navigation", "Main", MenuDefEnum.ICON_LABEL_BUTTON, IconCore.QUEST, "Quests", "sao.element.quests", ElementDefEnum.CATEGORY),
                ElementData("Navigation", "Main", MenuDefEnum.ICON_LABEL_BUTTON, IconCore.FIELD_MAP, "Field Map", "sao.element.field_map", ElementDefEnum.CATEGORY),
                ElementData("Navigation", "Main", MenuDefEnum.ICON_LABEL_BUTTON, IconCore.DUNGEON_MAP, "Dungeon Map", "sao.element.dungeon_map", ElementDefEnum.CATEGORY),
                ElementData("Settings", "Main", MenuDefEnum.ICON_LABEL_BUTTON, IconCore.OPTION, "Options", "sao.element.options", ElementDefEnum.CATEGORY),
                ElementData("Settings", "Main", MenuDefEnum.ICON_LABEL_BUTTON, IconCore.HELP, "Menu", "sao.element.menu", ElementDefEnum.BUTTON),
                ElementData("Settings", "Main", MenuDefEnum.ICON_LABEL_BUTTON, IconCore.LOGOUT, "Logout", "sao.element.logout", ElementDefEnum.BUTTON),
                ElementData("Options", "Settings", MenuDefEnum.ICON_LABEL_BUTTON, IconCore.OPTION, "Vanilla_Options", "guiOptions", ElementDefEnum.BUTTON)
        )

        if (InventoryCore.isBaublesLoaded()) list.add(ElementData(
                "Equipment",
                "Profile",
                MenuDefEnum.ICON_LABEL_BUTTON,
                IconCore.ACCESSORY,
                "Accessory", "sao.element.accessory",
                ElementDefEnum.CATEGORY)
        )

        list.addAll(OptionCore.values().map {
            if (it.category != null) ElementData(
                    it.getCategoryName(),
                    it.category?.getCategoryName() ?: "Options",
                    MenuDefEnum.ICON_LABEL_BUTTON,
                    IconCore.OPTION,
                    it.name, it.displayName,
                    if (it.isCategory) ElementDefEnum.CATEGORY else ElementDefEnum.OPTION
            ) else ElementData(
                    "Options",
                    "Settings",
                    MenuDefEnum.ICON_LABEL_BUTTON,
                    IconCore.OPTION,
                    it.name, it.displayName,
                    if (it.isCategory) ElementDefEnum.CATEGORY else ElementDefEnum.OPTION
            )
        })

        Minecraft.getMinecraft().world.playerEntities.filter { it != mc.player }.forEach {
            list.add(ElementData("Invite", "Party", MenuDefEnum.ICON_LABEL_BUTTON, IconCore.INVITE, it.name, it.displayNameString, ElementDefEnum.PLAYER))
        }

        list.addAll(Minecraft.getMinecraft().world.playerEntities.filter { it != mc.player }.map {
            ElementData(
                    "Invite",
                    "Party",
                    MenuDefEnum.ICON_LABEL_BUTTON,
                    IconCore.INVITE,
                    it.name, it.displayNameString,
                    ElementDefEnum.PLAYER
            )
        })

        //FMLCommonHandler.instance().minecraftServerInstance.playerList.players.filter { it != StaticPlayerHelper.thePlayer() }.forEach { player ->  run{list.add(ElementData("Invite", "Party", MenuDefEnum.ICON_LABEL_BUTTON, IconCore.INVITE, player.name, player.displayNameString, ElementDefEnum.PLAYER))}}

        list.forEach { (cat, parentCat, type, icon, name, displayName, elementType) ->
            var category: CategoryEnum

            category = try {
                CategoryEnum.valueOf(cat.toUpperCase())
            } catch (e: IllegalArgumentException) {
                try {
                    CategoryHelper.addCategory(cat.toUpperCase(), CategoryEnum.valueOf(parentCat!!.toUpperCase()))!!
                } catch (e: NullPointerException) {
                    SAOCore.LOGGER.fatal("Failed to make category for $name with category: $cat and parent category: $parentCat")
                    return@forEach
                }
            }

            if ((category == CategoryEnum.MAIN && parentCat != null) || (category != CategoryEnum.MAIN && !category.parent?.name.equals(parentCat?.toUpperCase())))
                try {
                    category = CategoryHelper.addCategory(cat.toUpperCase(), CategoryEnum.valueOf(parentCat!!.toUpperCase()))!!
                } catch (e: NullPointerException) {
                    SAOCore.LOGGER.fatal("Failed to make category for $name with category: $cat and parent category: $parentCat")
                    return@forEach
                }

            var categoryData: CategoryData? = categories.firstOrNull { it.category == category }

            //If category exists, add to existing
            if (categoryData != null) {
                categoryData.addElement(type, icon, name, displayName, elementType)
            }
            //Else, create new category and add to that
            else {
                categoryData = CategoryData(category, categories.firstOrNull { it.category == category.parent })
                categoryData.init(this)
                categoryData.addElement(type, icon, name, displayName, elementType)
                categories.add(categoryData)
            }
        }

        /*
        list.stream().forEachOrdered { (category, type, icon, name) -> kotlin.run{
            var categoryData: CategoryData? = categories.firstOrNull { it.name.equals(category, true) }

            //If category exists, add to existing
            if (categoryData != null)
                categoryData.addElement(type, icon, name)
            //Else, create new category and add to that
            else {
                categoryData = CategoryData(category, categories.find { it.parentOf(category) })
                categoryData.init(this)
                categoryData.addElement(type, icon, name) }
                categories.add(categoryData)
            }
        }*/

//        flowY = -height

//        SoundCore.play(mc, SoundCore.ORB_DROPDOWN)

        super.initGui()
    }

    override val parentX: Int
        get() = super.parentX + width * 2 / 5// + flowX / 2

    override val parentY: Int
        get() = super.parentY + flowY

    override fun updateScreen() {
        super.updateScreen()

        if (flowY < height / 2) flowY = (flowY + height / 2 - 32) / 2

        flowX /= 2
    }

    override fun close() {
        super.close()
        SoundCore.play(mc, SoundCore.DIALOG_CLOSE)
    }
}
