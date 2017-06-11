package com.saomc.saoui.neo.screens

import com.saomc.saoui.SAOCore
import com.saomc.saoui.SoundCore
import com.saomc.saoui.api.elements.ElementData
import com.saomc.saoui.api.elements.CategoryEnum
import com.saomc.saoui.api.elements.CategoryHelper
import com.saomc.saoui.api.screens.Actions
import com.saomc.saoui.screens.inventory.InventoryCore
import com.saomc.saoui.themes.elements.menus.CategoryData
import com.saomc.saoui.api.elements.MenuDefEnum
import com.saomc.saoui.util.IconCore
import net.minecraft.client.gui.GuiOptions
import net.minecraftforge.common.util.EnumHelper
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

    override fun backgroundClicked(cursorX: Int, cursorY: Int, button: Actions) {
        when (button) { // 0 = left click, 1 = right click, 2 = middle click, 3 = back
            Actions.RIGHT_PRESSED -> this.mc.displayGuiScreen(GuiOptions(this, this.mc.gameSettings))
            else -> return
        }
        SAOCore.LOGGER.debug("Background Clicked")
    }

    override fun initGui() {
        categories.clear()

        val list = listOf(
                ElementData("Main", null, MenuDefEnum.ICON_BUTTON, IconCore.PROFILE, "Profile", "sao.element.profile", true),
                ElementData("Main", null, MenuDefEnum.ICON_BUTTON, IconCore.SOCIAL, "Social", "sao.element.social", true),
                ElementData("Main", null, MenuDefEnum.ICON_BUTTON, IconCore.MESSAGE, "Message", "sao.element.message", true),
                ElementData("Main", null, MenuDefEnum.ICON_BUTTON, IconCore.NAVIGATION, "Navigation", "sao.element.navigation", true),
                ElementData("Main", null, MenuDefEnum.ICON_BUTTON, IconCore.SETTINGS, "Settings", "sao.element.settings", true),
                ElementData("Profile", "Main", MenuDefEnum.ICON_LABEL_BUTTON, IconCore.EQUIPMENT, "Equipment", "sao.element.equipment", true),
                ElementData("Profile", "Main", MenuDefEnum.ICON_LABEL_BUTTON, IconCore.ITEMS, "Items", "sao.element.items", true),
                ElementData("Profile", "Main", MenuDefEnum.ICON_LABEL_BUTTON, IconCore.SKILLS, "Skills", "sao.element.skills", true),
                ElementData("Equipment", "Profile", MenuDefEnum.ICON_LABEL_BUTTON, IconCore.EQUIPMENT, "Tools", "sao.element.tools", true),
                ElementData("Equipment", "Profile", MenuDefEnum.ICON_LABEL_BUTTON, IconCore.ARMOR, "Armor", "sao.element.armor", true),
                ElementData("Equipment", "Profile", MenuDefEnum.ICON_LABEL_BUTTON, IconCore.ITEMS, "Consumables", "sao.element.consumables", true),
                ElementData("Tools", "Equipment", MenuDefEnum.ICON_LABEL_BUTTON, IconCore.EQUIPMENT, "Weapons", "sao.element.weapons", true),
                ElementData("Tools", "Equipment", MenuDefEnum.ICON_LABEL_BUTTON, IconCore.EQUIPMENT, "Bows", "sao.element.bows", true),
                ElementData("Tools", "Equipment", MenuDefEnum.ICON_LABEL_BUTTON, IconCore.EQUIPMENT, "Pickaxe", "sao.element.pickaxe", true),
                ElementData("Tools", "Equipment", MenuDefEnum.ICON_LABEL_BUTTON, IconCore.EQUIPMENT, "Axe", "sao.element.axe", true),
                ElementData("Tools", "Equipment", MenuDefEnum.ICON_LABEL_BUTTON, IconCore.EQUIPMENT, "Shovel", "sao.element.shovel", true),
                ElementData("Social", "Main", MenuDefEnum.ICON_LABEL_BUTTON, IconCore.GUILD, "Guild", "sao.element.guild", true),
                ElementData("Social", "Main", MenuDefEnum.ICON_LABEL_BUTTON, IconCore.PARTY, "Party", "sao.element.party", true),
                ElementData("Social", "Main", MenuDefEnum.ICON_LABEL_BUTTON, IconCore.FRIEND, "Friends", "sao.element.friends", true),
                ElementData("Party", "Social", MenuDefEnum.ICON_LABEL_BUTTON, IconCore.INVITE, "Invite", "sao.element.invite", true),
                ElementData("Party", "Social", MenuDefEnum.ICON_LABEL_BUTTON, IconCore.CANCEL, "Dissolve", "sao.element.dissolve", false),
                ElementData("Message", "Main", MenuDefEnum.ICON_LABEL_BUTTON, IconCore.MESSAGE, "Message Box", "sao.element.message_box", true),
                ElementData("Navigation", "Main", MenuDefEnum.ICON_LABEL_BUTTON, IconCore.QUEST, "Quests", "sao.element.quests", true),
                ElementData("Navigation", "Main", MenuDefEnum.ICON_LABEL_BUTTON, IconCore.FIELD_MAP, "Field Map", "sao.element.field_map", true),
                ElementData("Navigation", "Main", MenuDefEnum.ICON_LABEL_BUTTON, IconCore.DUNGEON_MAP, "Dungeon Map", "sao.element.dungeon_map", true),
                ElementData("Settings", "Main", MenuDefEnum.ICON_LABEL_BUTTON, IconCore.OPTION, "Options", "sao.element.options", true),
                ElementData("Settings", "Main", MenuDefEnum.ICON_LABEL_BUTTON, IconCore.HELP, "Menu", "sao.element.menu", false),
                ElementData("Settings", "Main", MenuDefEnum.ICON_LABEL_BUTTON, IconCore.LOGOUT, "Logout", "sao.element.logout", false)
        )

        if (InventoryCore.isBaublesLoaded())
            list.plusElement(ElementData("Equipment", "Profile", MenuDefEnum.ICON_LABEL_BUTTON, IconCore.ACCESSORY, "Accessory", "sao.element.accessory", true))

        //OptionCore.values().forEachIndexed{ _, optionCore -> list.plusElement(ElementData(optionCore.getCategoryName(), MenuDefEnum.ICON_LABEL_BUTTON, IconCore.OPTION, optionCore.name)) }

        list.forEach { (cat, parentCat, type, icon, name, displayName, isCategory) ->
            var category: CategoryEnum

            try {
                category = CategoryEnum.valueOf(cat.toUpperCase())
            } catch (e: IllegalArgumentException){
                try {
                    category = CategoryHelper.addCategory(cat.toUpperCase(), CategoryEnum.valueOf(parentCat!!.toUpperCase()))!!
                } catch (e: NullPointerException){
                    SAOCore.LOGGER.fatal("Failed to make category for " + name + " with category: " + cat + " and parent category: " + parentCat)
                    return@forEach
                }
            }

            if ((category == CategoryEnum.MAIN && parentCat != null) || (category != CategoryEnum.MAIN && !category.parent?.name.equals(parentCat?.toUpperCase())))
                try {
                    category = CategoryHelper.addCategory(cat.toUpperCase(), CategoryEnum.valueOf(parentCat!!.toUpperCase()))!!
                } catch (e: NullPointerException){
                    SAOCore.LOGGER.fatal("Failed to make category for " + name + " with category: " + cat + " and parent category: " + parentCat)
                    return@forEach
                }

            var categoryData: CategoryData? = categories.firstOrNull { it.category == category }

            //If category exists, add to existing
            if (categoryData != null) {
                categoryData.addElement(type, icon, name, displayName, isCategory)
            }
            //Else, create new category and add to that
            else {
                categoryData = CategoryData(category, categories.firstOrNull { it.category == category.parent })
                categoryData.init(this)
                categoryData.addElement(type, icon, name, displayName, isCategory)
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
