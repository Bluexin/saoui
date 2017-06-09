package com.saomc.saoui.neo.screens

import com.saomc.saoui.SAOCore
import com.saomc.saoui.SoundCore
import com.saomc.saoui.api.elements.ElementData
import com.saomc.saoui.api.screens.Actions
import com.saomc.saoui.themes.elements.menus.CategoryData
import com.saomc.saoui.themes.elements.menus.MenuDefEnum
import com.saomc.saoui.util.IconCore
import net.minecraft.client.gui.GuiOptions
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

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
                ElementData("menu", MenuDefEnum.ICON_BUTTON, IconCore.PROFILE, "profile"),
                ElementData("menu", MenuDefEnum.ICON_BUTTON, IconCore.SOCIAL, "social"),
                ElementData("menu", MenuDefEnum.ICON_BUTTON, IconCore.MESSAGE, "message"),
                ElementData("menu", MenuDefEnum.ICON_BUTTON, IconCore.NAVIGATION, "navigation"),
                ElementData("menu", MenuDefEnum.ICON_BUTTON, IconCore.SETTINGS, "settings"),
                ElementData("profile", MenuDefEnum.ICON_LABEL_BUTTON, IconCore.EQUIPMENT, "equipment"),
                ElementData("profile", MenuDefEnum.ICON_LABEL_BUTTON, IconCore.ITEMS, "items"),
                ElementData("profile", MenuDefEnum.ICON_LABEL_BUTTON, IconCore.SKILLS, "skills")
        )
        list.stream().forEachOrdered { first -> kotlin.run{
            var category: CategoryData? = categories.firstOrNull { it.name.equals(first.category, true) }

            //If category exists, add to existing
            if (category != null)
                category.run { addElement(first.type, first.icon, first.name) }
            //Else, create new category and add to that
            else {
                category = CategoryData(first.category, categories.find { it.parentOf(first.category) })
                category.init(this)
                category.addElement(first.type, first.icon, first.name) }
                categories.add(category)
            }
        }

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
