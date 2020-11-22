package com.saomc.saoui.elements.custom

import be.bluexin.saomclib.party.PlayerInfo
import com.saomc.saoui.GLCore
import com.saomc.saoui.SAOCore
import com.saomc.saoui.config.OptionCore
import com.saomc.saoui.elements.IElement
import com.saomc.saoui.elements.IconElement
import com.saomc.saoui.elements.IconLabelElement
import com.saomc.saoui.resources.StringNames
import com.saomc.saoui.screens.CoreGUI
import com.saomc.saoui.screens.util.toIcon
import com.saomc.saoui.util.ColorUtil
import com.saomc.saoui.util.IconCore
import com.saomc.saoui.util.PlayerStats
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.Minecraft
import com.teamwizardry.librarianlib.features.kotlin.plus
import com.teamwizardry.librarianlib.features.math.BoundingBox2D
import com.teamwizardry.librarianlib.features.math.Vec2d
import net.minecraft.client.gui.inventory.GuiInventory
import net.minecraft.entity.EntityLivingBase
import net.minecraft.util.ResourceLocation
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt


class ProfileElement(var playerInfo: PlayerInfo, val isMain: Boolean = true, override var parent: IElement?) : IconLabelElement(IconCore.PROFILE) {

    override val listed: Boolean
        get() = !isMain

    override var valid: Boolean = false

    var w = 165
    var h = 256
    val size = 40

    override var pos: Vec2d = if (isMain) Vec2d(-w - 25.0, -(h / 2.0) - 25) else super.pos

    val left = pos.x + w / 2 - 10
    val top = pos.y + h / 2

    override var destination: Vec2d = if (isMain) Vec2d(-w - 20.0, -(h / 2.0) - 13) else super.destination

    override var boundingBox: BoundingBox2D = BoundingBox2D(pos, pos)
        get() = if (isMain) BoundingBox2D(pos, pos) else BoundingBox2D(pos, pos + vec(w, h))

    override val childrenYOffset: Int
        get() = 0
    override val childrenYSeparator: Int
        get() = 0


    private val rl = ResourceLocation(SAOCore.MODID, "textures/menu/parts/profilebg.png")

    override fun open(reInit: Boolean) {
        super.open(reInit)
        Minecraft().player.armorInventoryList.forEachIndexed { index, itemStack ->
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

    override fun drawBackground(mouse: Vec2d, partialTicks: Float) {
        if (opacity < 0.03 || scale == Vec2d.ZERO) return
        GLCore.pushMatrix()
        GLCore.glBlend(true)
        GLCore.color(ColorUtil.DEFAULT_COLOR)
        GLCore.glBindTexture(rl)

        val shadowY = size / 2 + max(min((mouse.y - pos.y), 0.0), -size / 2 + 2.0)
        GLCore.glTexturedRectV2(pos.x, pos.y, width = w.toDouble(), height = h.toDouble())


        GLCore.glBindTexture(StringNames.gui)
        GLCore.glTexturedRectV2(left - size / 2, top - shadowY / 2, width = size.toDouble(), height = shadowY, srcX = 200.0, srcY = 85.0, srcWidth = 56.0, srcHeight = 30.0)

        GLCore.glString(playerInfo.username, pos.xi + 50 + (playerInfo.username.length / 2), pos.yi + 20, ColorUtil.DEFAULT_BOX_FONT_COLOR.rgba, shadow = false, centered = true)
        val profile = PlayerStats.instance().stats.getStatsString(playerInfo.player)
        (0 until profile.size).forEach {
            GLCore.glString(profile[it], pos.xi + (w / 2) - 10 + (-GLCore.glStringWidth(profile[it]) / 2), (pos.y + 180 + (it * GLCore.glStringHeight())).toInt(), ColorUtil.DEFAULT_FONT_COLOR.rgba, centered = false)
        }
        GLCore.glBlend(false)
        GLCore.popMatrix()
    }

    override fun draw(mouse: Vec2d, partialTicks: Float) {
        if (canDraw)
            drawCharacter(left, top)
    }

    private fun drawCharacter(x: Double, y: Double) {
        if (playerInfo.player == null) return
        val tmp = playerInfo.player!!.ridingEntity as EntityLivingBase?

        GLCore.depth(true)
        if (playerInfo.player!!.isRiding && OptionCore.MOUNT_STAT_VIEW.isEnabled)
            GuiInventory.drawEntityOnScreen(x.roundToInt(), y.roundToInt(), 40, SAOCore.mc.currentScreen!!.width / 3.5f, 20f, tmp!!)
        else
            GuiInventory.drawEntityOnScreen(x.roundToInt(), y.roundToInt(), 40, SAOCore.mc.currentScreen!!.width / 3.5f, 20f, playerInfo.player!!)

        GLCore.glRescaleNormal(true)
        GLCore.glTexture2D(true)
        GLCore.glBlend(true)
    }

    override fun move(delta: Vec2d) {
        CoreGUI.animator.removeAnimationsFor(this)
    }

}

