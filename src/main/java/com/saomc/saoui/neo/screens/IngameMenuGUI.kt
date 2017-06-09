package com.saomc.saoui.neo.screens

import com.saomc.saoui.SoundCore
import com.saomc.saoui.themes.elements.menus.CategoryData
import com.saomc.saoui.themes.elements.menus.MenuDefEnum
import com.saomc.saoui.themes.elements.menus.ElementData
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


    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        if (!playedSound) { // This trickery is used to circumvent the stupid shit in Minecraft#displayInGameMenu()
            SoundCore.play(mc, SoundCore.ORB_DROPDOWN)
            playedSound = true
        }

        super.drawScreen(mouseX, mouseY, partialTicks)
    }

    override fun mouseReleased(cursorX: Int, cursorY: Int, button: Int) {
        super.mouseReleased(cursorX, cursorY, button)

        when (button) { // 0 = left click, 1 = right click, 2 = middle click, 3 = back
            1 -> this.mc.displayGuiScreen(GuiOptions(this, this.mc.gameSettings))
        }
    }

    override fun initGui() {
        categories.clear()
        val list = listOf(
                Pair("menu", ElementData(MenuDefEnum.ICON_BUTTON, IconCore.PROFILE, "profile")),
                Pair("menu", ElementData(MenuDefEnum.ICON_BUTTON, IconCore.SOCIAL, "social")),
                Pair("menu", ElementData(MenuDefEnum.ICON_BUTTON, IconCore.MESSAGE, "message")),
                Pair("menu", ElementData(MenuDefEnum.ICON_BUTTON, IconCore.NAVIGATION, "navigation")),
                Pair("menu", ElementData(MenuDefEnum.ICON_BUTTON, IconCore.SETTINGS, "settings")),
                Pair("profile", ElementData(MenuDefEnum.ICON_LABEL_BUTTON, IconCore.EQUIPMENT, "equipment")),
                Pair("profile", ElementData(MenuDefEnum.ICON_LABEL_BUTTON, IconCore.ITEMS, "items")),
                Pair("profile", ElementData(MenuDefEnum.ICON_LABEL_BUTTON, IconCore.SKILLS, "skills"))
        )
        list.stream().forEachOrdered { element -> kotlin.run{
            var category: CategoryData? = categories.firstOrNull() { it.name.equals(element.first, true) }

            if (category != null)
                category.run { addElement(element.second) }
            else {
                category = CategoryData(element.first, categories.find { it.parentOf(element.first) })
                category.init(this)
                category.addElement(element.second)
                categories.add(category)
            }
        } }

//        flowY = -height

//        SoundCore.play(mc, SoundCore.ORB_DROPDOWN)

        super.initGui()
    }

    override fun closeCategory(name: String) {
        super.closeCategory(name)
    }

    override fun openCategory(name: String) {
        super.openCategory(name)
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
