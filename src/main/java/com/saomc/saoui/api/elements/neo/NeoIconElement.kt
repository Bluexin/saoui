package com.saomc.saoui.api.elements.neo

import com.saomc.saoui.GLCore
import com.saomc.saoui.api.screens.IIcon
import com.saomc.saoui.resources.StringNames
import com.saomc.saoui.util.ColorUtil
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.minus
import com.teamwizardry.librarianlib.features.math.BoundingBox2D
import com.teamwizardry.librarianlib.features.math.Vec2d
import net.minecraft.client.renderer.GlStateManager

/**
 * Part of saoui by Bluexin, released under GNU GPLv3.
 *
 * @author Bluexin
 */
class NeoIconElement(private val icon: IIcon, private val x: Int, private val y: Int) : NeoParent() {

    private var onClickBody: (Vec2d) -> Boolean = { true }

    override val boundingBox = BoundingBox2D(vec(x, y), vec(20 + x, 20 + y))

    override fun draw(mouse: Vec2d, partialTicks: Float) {
        GlStateManager.pushMatrix()
        GLCore.glColorRGBA(if (mouse in this) ColorUtil.HOVER_COLOR else ColorUtil.DEFAULT_COLOR)
        GLCore.glBindTexture(StringNames.gui)
        GLCore.glTexturedRect(x.toDouble(), y.toDouble(), 0.0, 25.0, 20.0, 20.0)
        GLCore.glColorRGBA(if (mouse in this) ColorUtil.HOVER_FONT_COLOR else ColorUtil.DEFAULT_FONT_COLOR)
        if (icon.rl != null) GLCore.glBindTexture(icon.rl!!)
        icon.glDrawUnsafe(x + 2, y + 2)

        GlStateManager.translate(x.toDouble() + childrenXOffset, y.toDouble() + childrenYOffset, 0.0)
        val nmouse = mouse - vec(x + childrenXOffset, y + childrenYOffset)
        elements.forEach { it.draw(nmouse, partialTicks) }
        GlStateManager.popMatrix()
    }

    override fun click(pos: Vec2d): Boolean {
        return if (pos in boundingBox) {
            onClickBody(pos)
        } else {
            val npos = pos - vec(x + childrenXOffset, y + childrenYOffset)
            var ok = false
            elements.forEach { ok = it.click(npos) || ok }
            if (ok) true
            else {
                elements.clear()
                false
            }
        }
    }

    override val childrenXOffset = 20

    fun onClick(body: (Vec2d) -> Boolean) {
        onClickBody = body
    }
}
