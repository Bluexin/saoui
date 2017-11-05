package com.saomc.saoui.api.elements.neo

import com.saomc.saoui.GLCore
import com.saomc.saoui.api.screens.IIcon
import com.saomc.saoui.neo.screens.NeoGui
import com.saomc.saoui.resources.StringNames
import com.saomc.saoui.util.ColorUtil
import com.teamwizardry.librarianlib.features.animator.animations.BasicAnimation
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.math.BoundingBox2D
import com.teamwizardry.librarianlib.features.math.Vec2d
import net.minecraft.client.renderer.GlStateManager

/**
 * Part of saoui by Bluexin, released under GNU GPLv3.
 *
 * @author Bluexin
 */
open class NeoIconLabelElement(icon: IIcon, private val label: String, var width: Int = 84, var height: Int = 18, x: Int = 0, y: Int = 0) : NeoIconElement(icon, x, y) {

    override val boundingBox = BoundingBox2D(vec(x, y), vec(width + x, height + y))

    override fun draw(mouse: Vec2d, partialTicks: Float) { // TODO: scrolling if too many elements
        GlStateManager.pushMatrix()
        GLCore.glColorRGBA(ColorUtil.multiplyAlpha(getColor(mouse), opacity))
        GLCore.glBindTexture(StringNames.slot)
        GLCore.glTexturedRect(x.toDouble(), y.toDouble(), width.toDouble(), height.toDouble(), 0.0, 40.0, 84.0, 18.0)
        val color = ColorUtil.multiplyAlpha(getTextColor(mouse), opacity)
        GLCore.glColorRGBA(color)
        if (icon.rl != null) GLCore.glBindTexture(icon.rl!!)
        icon.glDrawUnsafe(x + 1, y + 1)
        GLCore.glString(label, x + 22, y + height / 2, color, centered = true)

        drawChildren(mouse, partialTicks)
        GlStateManager.popMatrix()
    }

    override val childrenXOffset = width + 5

    override var visible: Boolean
        get() = super.visible
        set(value) {
            super.visible = value
            if (value) {
                opacity = 0f
                val appear = BasicAnimation(this, "opacity")
                appear.to = 1f
                appear.duration = 4f
                NeoGui.animator.add(appear)
            }
        }
}
