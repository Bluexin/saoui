package com.tencao.saoui.screens.ingame

import com.mojang.blaze3d.matrix.MatrixStack
import com.tencao.saomclib.Client
import com.tencao.saomclib.GLCore
import com.tencao.saoui.SAOCore
import com.tencao.saoui.util.*
import net.minecraft.client.gui.AbstractGui
import net.minecraft.client.gui.screen.Screen
import net.minecraft.util.ResourceLocation
import java.awt.Color

class DeathGui : Screen("Death Screen".toTextComponent()) {

    private var isHardCore = Client.minecraft.world?.worldInfo?.isHardcore ?: false
    var counter = 0

    override fun tick() {
        if (counter < 40) counter++
    }

    override fun renderBackground(matrixStack: MatrixStack) {
        super.renderBackground(matrixStack)
    }

    override fun render(matrixStack: MatrixStack, mouseX: Int, mouseY: Int, partialTicks: Float) {
        super.render(matrixStack, mouseX, mouseY, partialTicks)

        darkenScreen(matrixStack)
        GLCore.pushMatrix()
        GLCore.color(if (isHardCore) ColorUtil.HARDCORE_DEAD_COLOR.rgba else ColorUtil.DEAD_COLOR.rgba)
        GLCore.glBindTexture(rl)
        GLCore.glAlphaTest(true)
        GLCore.glTexturedRectV2(width / 2 - (w.toDouble() / 2), height / 2 - (h.toDouble() / 2), width = w.toDouble(), height = h.toDouble(), srcX = 0.0, srcY = 0.0, srcWidth = 256.0, srcHeight = 256.0)
        GLCore.glBlend(false)
        GLCore.popMatrix()
    }

    fun darkenScreen(matrixStack: MatrixStack) {
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
        } else super.keyPressed(keyCode, scanCode, modifiers)
    }

    fun confirmedClick() {
        counter = 0
        if (isHardCore) {
            UIUtil.closeGame()
        } else {
            Client.player?.preparePlayerToSpawn()
            Client.player?.respawnPlayer()
            // ****** Prevents GUI attempting to open Death Screen twice ******
            // mc.player.setPlayerSPHealth(mc.player.maxHealth)
            Client.displayGuiScreen(null)
        }
    }

    companion object {
        val rl = ResourceLocation(SAOCore.MODID, "textures/hud/buttons/death.png")
        const val w = 280f
        const val h = 100f
    }
}
