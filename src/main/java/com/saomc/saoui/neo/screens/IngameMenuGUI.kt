package com.saomc.saoui.neo.screens

import com.saomc.saoui.SAOCore
import com.saomc.saoui.SoundCore
import com.saomc.saoui.api.elements.ElementData
import com.saomc.saoui.api.screens.Actions
import com.saomc.saoui.config.OptionCore
import com.saomc.saoui.screens.inventory.InventoryCore
import com.saomc.saoui.themes.elements.menus.CategoryData
import com.saomc.saoui.themes.elements.menus.MenuDefEnum
import com.saomc.saoui.util.IconCore
import net.minecraft.client.gui.GuiOptions
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.stream.Stream

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
                ElementData("Menu", MenuDefEnum.ICON_BUTTON, IconCore.PROFILE, "Profile"),
                ElementData("Menu", MenuDefEnum.ICON_BUTTON, IconCore.SOCIAL, "Social"),
                ElementData("Menu", MenuDefEnum.ICON_BUTTON, IconCore.MESSAGE, "Message"),
                ElementData("Menu", MenuDefEnum.ICON_BUTTON, IconCore.NAVIGATION, "Navigation"),
                ElementData("Menu", MenuDefEnum.ICON_BUTTON, IconCore.SETTINGS, "Settings"),
                ElementData("Profile", MenuDefEnum.ICON_LABEL_BUTTON, IconCore.EQUIPMENT, "Equipment"),
                ElementData("Profile", MenuDefEnum.ICON_LABEL_BUTTON, IconCore.ITEMS, "Items"),
                ElementData("Profile", MenuDefEnum.ICON_LABEL_BUTTON, IconCore.SKILLS, "Skills"),
                ElementData("Equipment", MenuDefEnum.ICON_LABEL_BUTTON, IconCore.EQUIPMENT, "Tools"),
                ElementData("Equipment", MenuDefEnum.ICON_LABEL_BUTTON, IconCore.ARMOR, "Armor"),
                ElementData("Equipment", MenuDefEnum.ICON_LABEL_BUTTON, IconCore.ITEMS, "Consumables"),
                ElementData("Tools", MenuDefEnum.ICON_LABEL_BUTTON, IconCore.EQUIPMENT, "Weapons"),
                ElementData("Tools", MenuDefEnum.ICON_LABEL_BUTTON, IconCore.EQUIPMENT, "Bows"),
                ElementData("Tools", MenuDefEnum.ICON_LABEL_BUTTON, IconCore.EQUIPMENT, "Pickaxe"),
                ElementData("Tools", MenuDefEnum.ICON_LABEL_BUTTON, IconCore.EQUIPMENT, "Axe"),
                ElementData("Tools", MenuDefEnum.ICON_LABEL_BUTTON, IconCore.EQUIPMENT, "Shovel"),
                ElementData("Social", MenuDefEnum.ICON_LABEL_BUTTON, IconCore.GUILD, "Guild"),
                ElementData("Social", MenuDefEnum.ICON_LABEL_BUTTON, IconCore.PARTY, "Party"),
                ElementData("Social", MenuDefEnum.ICON_LABEL_BUTTON, IconCore.FRIEND, "Friends"),
                ElementData("Party", MenuDefEnum.ICON_LABEL_BUTTON, IconCore.INVITE, "Invite"),
                ElementData("Party", MenuDefEnum.ICON_LABEL_BUTTON, IconCore.CANCEL, "Dissolve"),
                ElementData("Message", MenuDefEnum.ICON_LABEL_BUTTON, IconCore.MESSAGE, "Message Box"),
                ElementData("Navigation", MenuDefEnum.ICON_LABEL_BUTTON, IconCore.QUEST, "Quests"),
                ElementData("Navigation", MenuDefEnum.ICON_LABEL_BUTTON, IconCore.FIELD_MAP, "Field Map"),
                ElementData("Navigation", MenuDefEnum.ICON_LABEL_BUTTON, IconCore.DUNGEON_MAP, "Dungeon Map"),
                ElementData("Settings", MenuDefEnum.ICON_LABEL_BUTTON, IconCore.OPTION, "Options"),
                ElementData("Settings", MenuDefEnum.ICON_LABEL_BUTTON, IconCore.HELP, "Menu"),
                ElementData("Settings", MenuDefEnum.ICON_LABEL_BUTTON, IconCore.LOGOUT, "Logout")
        )

        if (InventoryCore.isBaublesLoaded())
            list.plusElement(ElementData("Equipment", MenuDefEnum.ICON_LABEL_BUTTON, IconCore.ACCESSORY, "Accessory"))

        OptionCore.values().forEachIndexed{ _, optionCore -> list.plusElement(ElementData(optionCore.getCategoryName(), MenuDefEnum.ICON_LABEL_BUTTON, IconCore.OPTION, optionCore.name)) }

        list.forEach { (category, type, icon, name) ->
            var categoryData: CategoryData? = categories.firstOrNull { it.name.equals(category, true) }

            //If category exists, add to existing
            if (categoryData != null) {
                categoryData.addElement(type, icon, name)
            }
            //Else, create new category and add to that
            else {
                categoryData = CategoryData(category, categories.find { it.parentOf(category) })
                categoryData.init(this)
                categoryData.addElement(type, icon, name)
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
