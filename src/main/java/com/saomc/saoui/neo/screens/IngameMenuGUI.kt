package com.saomc.saoui.neo.screens

import com.saomc.saoui.SoundCore
import com.saomc.saoui.themes.ThemeLoader
import com.saomc.saoui.themes.elements.menus.PlaceholderElement
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
        if (!playedSound) {
            SoundCore.play(mc, SoundCore.ORB_DROPDOWN)
            playedSound = true
        }

        super.drawScreen(mouseX, mouseY, partialTicks)
    }

    override fun mouseReleased(cursorX: Int, cursorY: Int, button: Int) {
        super.mouseReleased(cursorX, cursorY, button)

        when (button) { // 0 = left click, 1 = right click, 2 = middle click, 3 = back
            2 -> ThemeLoader.load()
            1 -> this.mc.displayGuiScreen(GuiOptions(this, this.mc.gameSettings))
        }
    }

    override fun initGui() {
        elements.addAll(listOf(
                PlaceholderElement(10, 10, IconCore.ACCESSORY),
                PlaceholderElement(10, 40, IconCore.ARMOR)
        ))
        flowY = -height

        super.initGui()
    }

    override fun getX() = super.getX() + width * 2 / 5 + flowX / 2

    override fun getY() = super.getY() + flowY

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
