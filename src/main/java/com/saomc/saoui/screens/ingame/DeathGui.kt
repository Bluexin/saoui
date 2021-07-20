package com.saomc.saoui.screens.ingame

import com.mojang.blaze3d.matrix.MatrixStack
import com.saomc.saoui.GLCore
import com.saomc.saoui.SAOCore
import com.saomc.saoui.SAOCore.mc
import com.saomc.saoui.util.*
import net.minecraft.client.gui.AbstractGui
import net.minecraft.client.gui.screen.Screen
import net.minecraft.util.ResourceLocation
import java.awt.Color

class DeathGui : Screen("Death Screen".translate()) {

    private var isHardCore = mc.world?.worldInfo?.isHardcore?: false
    var counter = 0

    override fun tick() {
        if (counter < 40) counter++
    }

    override fun renderBackground(matrixStack: MatrixStack) {
        super.renderBackground(matrixStack)
        darkenScreen(matrixStack)

        GLCore.color(if (isHardCore) ColorUtil.HARDCORE_DEAD_COLOR else ColorUtil.DEAD_COLOR)
        GLCore.glBindTexture(rl)
        GLCore.glTexturedRectV2(matrixStack, width / 2 - (w / 2), height / 2 - (h / 2), width = w, height = h)
    }

    fun darkenScreen(matrixStack: MatrixStack){
        val alpha = counter / 40f
        AbstractGui.fill(matrixStack, 0, 0, scaledWidth, scaledHeight, ColorUtil.multiplyAlpha(Color.black.rgb, alpha))
/*
        GlStateManager.disableTexture2D()
        GLCore.glBlend(true)
        GlStateManager.disableAlpha()
        GLCore.tryBlendFuncSeparate(SourceFactor.SRC_ALPHA.factor, DestFactor.ONE_MINUS_SRC_ALPHA.factor, SourceFactor.ONE.factor, DestFactor.ZERO.factor)
        GlStateManager.shadeModel(7425)
        GLCore.begin(7, DefaultVertexFormats.POSITION_COLOR)
        GLCore.addVertex(width.toDouble(), 0.0, zLevel.toDouble(), 0f, 0f, 0f, alpha)
        GLCore.addVertex(0.0, 0.0, zLevel.toDouble(), 0f, 0f, 0f, alpha)
        GLCore.addVertex(0.0, height.toDouble(), zLevel.toDouble(), 0f, 0f, 0f, alpha)
        GLCore.addVertex(width.toDouble(), height.toDouble(), zLevel.toDouble(), 0f, 0f, 0f, alpha)
        GLCore.draw()
        GlStateManager.shadeModel(7424)
        //GlStateManager.disableBlend()
        GlStateManager.enableAlpha()
        GlStateManager.enableTexture2D()*/
    }

    override fun isPauseScreen(): Boolean {
        return true
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        confirmedClick()
        return true
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        return if (keyCode == 1) {
            confirmedClick()
            true
        }
        else super.keyPressed(keyCode, scanCode, modifiers)
    }

    fun confirmedClick(){
        if (isHardCore){
            UIUtil.closeGame()
        } else {
            mc.player?.preparePlayerToSpawn()
            mc.player?.respawnPlayer()
            // ****** Prevents GUI attempting to open Death Screen twice ******
            //mc.player.setPlayerSPHealth(mc.player.maxHealth)
            mc.displayGuiScreen(null)
        }
    }

    companion object {
        val rl = ResourceLocation(SAOCore.MODID, "textures/hud/buttons/death.png")
        const val w = 280f
        const val h = 100f
    }
}
