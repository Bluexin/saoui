package com.saomc.saoui.elements

import com.saomc.saoui.GLCore
import com.saomc.saoui.api.screens.IIcon
import com.saomc.saoui.config.OptionCore
import com.saomc.saoui.elements.controllers.IController
import com.saomc.saoui.resources.StringNames
import com.saomc.saoui.util.ColorUtil
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.plus
import com.teamwizardry.librarianlib.features.math.Vec2d

open class IconLabelElement(icon: IIcon, open val label: String = "", controller: IController, pos: Vec2d = Vec2d.ZERO, destination: Vec2d = pos, description: MutableList<String> = mutableListOf(), function: () -> Unit = {}) : Element(icon = icon, controllingParent = controller, pos = pos, destination = destination, width = 84, height = 18, description = description, function = function) {

    /**
     * @see IElement.drawBackground
     */
    override fun drawBackground(mouse: Vec2d, partialTicks: Float) {
        if (!canDraw) return
        GLCore.pushMatrix()
        if (scale != Vec2d.ONE) GLCore.glScalef(scale.xf, scale.yf, 1f)
        GLCore.glBlend(true)
        GLCore.depth(true)
        GLCore.color(ColorUtil.multiplyAlpha(getColor(mouse), transparency))
        GLCore.glBindTexture(StringNames.slot)
        GLCore.glTexturedRectV2(pos.x, pos.y, width = width.toDouble(), height = height.toDouble(), srcX = 0.0, srcY = 40.0, srcWidth = 84.0, srcHeight = 18.0)
        if ((mouse in this || selected) && OptionCore.MOUSE_OVER_EFFECT.isEnabled)
            mouseOverEffect()
        GLCore.glBlend(false)
        GLCore.depth(false)
        GLCore.popMatrix()
    }

    /**
     * @see IElement.draw
     */
    override fun draw(mouse: Vec2d, partialTicks: Float){
        if (!canDraw) return
        GLCore.pushMatrix()
        GLCore.glBlend(true)
        val color = ColorUtil.multiplyAlpha(getTextColor(mouse), transparency)
        GLCore.color(color)
        if (icon.rl != null) GLCore.glBindTexture(icon.rl!!)
        icon.glDrawUnsafe(pos + vec(1, 1))
        if (this.highlighted || (mouse in this || selected))
            GLCore.glString(label, pos + vec(22, height / 2), color, shadow = OptionCore.TEXT_SHADOW.isEnabled, centered = true)
        else
            GLCore.glString(label, pos + vec(22, height / 2), color, shadow = false, centered = true)
        GLCore.glBlend(true)
        GLCore.popMatrix()
    }

    /**
     * @see IElement.drawForeground
     */
    override fun drawForeground(mouse: Vec2d, partialTicks: Float) {
        if (!canDraw) return
        if (mouse in this) {
            drawHoveringText(mouse)
            GLCore.lighting(false)
            GLCore.depth(false)
        }
    }

}