package com.tencao.saoui.api.elements

import com.mojang.blaze3d.matrix.MatrixStack
import com.tencao.saomclib.Client
import com.tencao.saomclib.GLCore
import com.tencao.saomclib.utils.math.BoundingBox2D
import com.tencao.saomclib.utils.math.Vec2d
import com.tencao.saomclib.utils.math.vec
import com.tencao.saoui.SAOCore
import com.tencao.saoui.config.OptionCore
import com.tencao.saoui.resources.StringNames
import com.tencao.saoui.screens.CoreGUI
import com.tencao.saoui.screens.util.toIcon
import com.tencao.saoui.util.ColorUtil
import com.tencao.saoui.util.IconCore
import com.tencao.saoui.util.PlayerStats
import net.minecraft.client.gui.screen.inventory.InventoryScreen.drawEntityOnScreen
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.ResourceLocation
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

class ProfileElement(var player: PlayerEntity, override var listed: Boolean) : IconElement(IconCore.PROFILE) {

    var w = 165
    var h = 256
    val size = 40

    override var pos: Vec2d = if (!listed) Vec2d(-w - 25.0, -(h / 2.0) - 25) else super.pos

    val left = pos.x + w / 2 - 10
    val top = pos.y + h / 2

    override var destination: Vec2d = if (!listed) Vec2d(-w - 20.0, -(h / 2.0) - 13) else super.destination

    override val boundingBox: BoundingBox2D
        get() = if (!listed) BoundingBox2D(pos, pos) else BoundingBox2D(pos, pos + vec(w, h))

    private val rl = ResourceLocation(SAOCore.MODID, "textures/menu/parts/profilebg.png")

    init {
        Client.player?.armorInventoryList?.forEachIndexed { index, itemStack ->
            val icon = IconElement(itemStack.toIcon())
            icon.pos = when (index) {
                0 -> Vec2d(left - w + 20, top + 50)
                1 -> Vec2d(left - w + 20, top + 70)
                2 -> Vec2d(left - w + 20, top + 90)
                3 -> Vec2d(left - w + 20, top + 110)
                else -> Vec2d(left + 50, top + 50)
            }
            icon.destination = icon.pos
            +icon
        }
    }

    override fun drawBackground(mouse: Vec2d, partialTicks: Float, matrixStack: MatrixStack) {
        if (canDraw) {
            GLCore.pushMatrix()
            GLCore.glBlend(true)
            GLCore.color(ColorUtil.DEFAULT_COLOR.multiplyAlpha(transparency))
            GLCore.glBindTexture(rl)

            val shadowY = size / 2 + max(min((mouse.y - pos.y), 0.0), -size / 2 + 2.0)
            GLCore.glTexturedRectV2(pos.x, pos.y, width = w.toDouble(), height = h.toDouble())

            GLCore.glBindTexture(StringNames.gui)
            GLCore.glTexturedRectV2(left - size / 2, top - shadowY / 2, width = size.toDouble(), height = shadowY, srcX = 200.0, srcY = 85.0, srcWidth = 56.0, srcHeight = 30.0)

            GLCore.glString(player.scoreboardName, pos.xi + 50 + (player.displayName.string.length / 2), pos.yi + 20, ColorUtil.DEFAULT_BOX_FONT_COLOR.rgba, matrixStack = matrixStack, shadow = false, centered = true)
            val profile = PlayerStats.instance().stats.getStatsString(player)
            val color = if (transparency < 1.0f) ColorUtil.DEFAULT_BOX_FONT_COLOR.rgba else ColorUtil.DEFAULT_FONT_COLOR.rgba
            (0 until profile.size).forEach {
                GLCore.glString(profile[it], pos.xi + (w / 2) - 10 + (-GLCore.glStringWidth(profile[it]) / 2), (pos.y + 180 + (it * GLCore.glStringHeight())).toInt(), color, matrixStack = matrixStack, centered = false)
            }
            GLCore.glBlend(false)
            GLCore.color(1f, 1f, 1f, 1f)
            GLCore.popMatrix()
        }
    }

    override fun draw(ouse: Vec2d, partialTicks: Float, matrixStack: MatrixStack) {
        if (canDraw) {
            drawCharacter(left, top)
        }
    }

    override fun drawForeground(mouse: Vec2d, partialTicks: Float, matrixStack: MatrixStack) {
    }

    private fun drawCharacter(x: Double, y: Double) {
        val tmp = player.ridingEntity as LivingEntity?

        GLCore.depth(true)
        if (player.ridingEntity != null && OptionCore.MOUNT_STAT_VIEW.isEnabled) {
            drawEntityOnScreen(x.roundToInt(), y.roundToInt(), 40, Client.minecraft.currentScreen!!.width / 3.5f, 20f, tmp!!)
        } else {
            drawEntityOnScreen(x.roundToInt(), y.roundToInt(), 40, Client.minecraft.currentScreen!!.width / 3.5f, 20f, player)
        }

        GLCore.glRescaleNormal(true)
        GLCore.glTexture2D(true)
        GLCore.glBlend(true)
    }

    override fun move(delta: Vec2d) {
        CoreGUI.animator.removeAnimationsFor(this)
    }
}
