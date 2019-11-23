package com.saomc.saoui.screens

import com.saomc.saoui.GLCore
import com.saomc.saoui.SAOCore
import com.saomc.saoui.util.ColorUtil
import com.saomc.saoui.util.UIUtil
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.util.ResourceLocation

class DeathGui : GuiScreen() {

    private var isHardCore = false

    override fun initGui() {
        isHardCore = mc.world.worldInfo.isHardcoreModeEnabled
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        val sr = ScaledResolution(mc)
        drawDefaultBackground()
        GLCore.color(if (isHardCore) ColorUtil.HARDCORE_DEAD_COLOR else ColorUtil.DEAD_COLOR)
        GLCore.glBlend(true)
        GLCore.glBindTexture(rl)
        GLCore.glTexturedRectV2(sr.scaledWidth / 2 - (w / 2), sr.scaledHeight / 2 - (h / 2), width = w, height = h, srcX = 0.0, srcY = 0.0, srcWidth = 256.0, srcHeight = 256.0)
        GLCore.glBlend(false)
    }

    override fun doesGuiPauseGame(): Boolean {
        return true
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        confirmedClick()
    }

    override fun keyTyped(typedChar: Char, keyCode: Int) {
        if (keyCode == 1) {
            confirmedClick()
        }
    }

    fun confirmedClick(){
        if (mc.world.worldInfo.isHardcoreModeEnabled){
            UIUtil.closeGame()
        } else {
            mc.player.preparePlayerToSpawn()
            mc.player.respawnPlayer()
            // ****** Prevents GUI attempting to open Death Screen twice ******
            //mc.player.setPlayerSPHealth(mc.player.maxHealth)
            mc.displayGuiScreen(null)
        }
    }

    companion object {
        val rl = ResourceLocation(SAOCore.MODID, "textures/hud/buttons/death.png")
        const val w = 280.0
        const val h = 100.0
    }
}
