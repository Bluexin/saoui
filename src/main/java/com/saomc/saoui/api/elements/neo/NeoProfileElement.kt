package com.saomc.saoui.api.elements.neo

import com.saomc.saoui.GLCore
import com.saomc.saoui.SAOCore
import com.saomc.saoui.config.OptionCore
import com.saomc.saoui.resources.StringNames
import com.saomc.saoui.util.ColorUtil
import com.saomc.saoui.util.IconCore
import com.saomc.saoui.util.PlayerStats
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.plus
import com.teamwizardry.librarianlib.features.math.BoundingBox2D
import com.teamwizardry.librarianlib.features.math.Vec2d
import net.minecraft.client.gui.inventory.GuiInventory
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.ResourceLocation
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt


open class NeoProfileElement(var player: EntityPlayer, override var pos: Vec2d = vec(-189, -74), override var destination: Vec2d = pos) : NeoIconElement(IconCore.PROFILE) {

    val width = 170
    val height = 240

    override val boundingBox: BoundingBox2D
        get() = BoundingBox2D(pos , pos)

    val actualBoundingBox: BoundingBox2D
        get() = BoundingBox2D(pos , pos + vec(width, height))

    private val rl = ResourceLocation(SAOCore.MODID, "textures/menu/parts/profilebg.png")

    init {
        /* TODO Add items to screen
        val inventory = Minecraft.getMinecraft().player.inventoryContainer

        (0 until 5).forEach {
            var slot = it
            if (it <= 3){
                slot += 5
            } else if (it == 4) {
                slot += 41
            }
            else slot = Minecraft.getMinecraft().player.inventory.currentItem + 31

            val stack = inventory.getSlot(slot).stack
            val icon = NeoIconElement(icon = ItemIcon{stack})
            icon.pos = when (slot){
                5 -> Vec2d(pos.x + width * 0.25, pos.y + 50)
                6 -> Vec2d(pos.x + width * 0.75, pos.y + 50)
                7 -> Vec2d(pos.x + width * 0.75, pos.y + 150)
                8 -> Vec2d(pos.x + width * 0.75, pos.y + 150)
                45 -> Vec2d(pos.x + width * 0.75, pos.y + 100)
                else -> Vec2d(pos.x + width * 0.25, pos.y + 100)
            }
            icon.destination = icon.pos
            +icon
        }*/
    }

    private fun drawCharacter(x: Double, y: Double, size: Int, cursorX: Int, cursorY: Int) {
        val mouseX = x.toFloat() - cursorX
        val mouseY = y.toFloat() - size * 1.67f - cursorY.toFloat()
        val tmp = player.ridingEntity as EntityLivingBase?

        if (player.isRiding && OptionCore.MOUNT_STAT_VIEW.isEnabled)
            GuiInventory.drawEntityOnScreen(x.roundToInt(), y.roundToInt(), size, mouseX, mouseY, tmp!!)
        else
            GuiInventory.drawEntityOnScreen(x.roundToInt(), y.roundToInt(), size, mouseX, mouseY, player)

        GLCore.glRescaleNormal(true)
        GLCore.glTexture2D(true)
        GLCore.glBlend(true)

        GLCore.tryBlendFuncSeparate(770, 771, 1, 0)
    }

    override fun draw(mouse: Vec2d, partialTicks: Float) {
        if (opacity < 0.03 || scale == Vec2d.ZERO) return
        GLCore.pushMatrix()
        GLCore.color(ColorUtil.DEFAULT_COLOR.multiplyAlpha(opacity))
        GLCore.glBindTexture(rl)
        val left = pos.x + width / 2 - 10
        val top = pos.y + height / 2
        val size = 40

        val shadowY = size / 2 + max(min((mouse.y - pos.y), 0.0), -size / 2 + 2.0)
        GLCore.glTexturedRectV2(pos.x, pos.y, width = width.toDouble(), height = height.toDouble())


        GLCore.glBindTexture(StringNames.gui)
        GLCore.glTexturedRectV2( left - size / 2, top - shadowY / 2, width = size.toDouble(), height = shadowY, srcX = 200.0, srcY = 85.0, srcWidth = 56.0, srcHeight = 30.0)
        drawCharacter(left, top, size, mouse.x.roundToInt(), mouse.y.roundToInt())

        GLCore.glString(player.displayNameString, pos.xi + 50 + (player.displayNameString.length / 2), pos.yi + 20, ColorUtil.DEFAULT_BOX_FONT_COLOR.rgba, shadow = false, centered = true)
        val profile = PlayerStats.instance().stats.getStatsString(player)
        (0 until profile.size).forEach {
            GLCore.glString(profile[it], pos.xi + (width / 2) - 10 + (-GLCore.glStringWidth(profile[it]) / 2), (pos.y + 180 + (it * GLCore.glStringHeight())).toInt(), ColorUtil.DEFAULT_FONT_COLOR.rgba, centered = false)
        }

        GLCore.popMatrix()


    }

}

