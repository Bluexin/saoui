package com.saomc.saoui.api.elements

import com.mojang.blaze3d.matrix.MatrixStack
import com.saomc.saoui.GLCore
import com.saomc.saoui.config.OptionCore
import com.saomc.saoui.util.ColorUtil
import com.saomc.saoui.util.IconCore
import com.teamwizardry.librarianlib.core.util.vec
import com.teamwizardry.librarianlib.math.Vec2d

class IconTextElement(var text: String, override val description: MutableList<String> = mutableListOf()): IconElement(IconCore.NONE) {

    init {
        if (text.length > 3) text = text.substring(0, 2)
    }

    override fun draw(matrix: MatrixStack, mouse: Vec2d, partialTicks: Float) {
        super.draw(matrix, mouse, partialTicks)
        val color = ColorUtil.multiplyAlpha(getTextColor(mouse), opacity)
        GLCore.color(color)
        if (this.highlighted || mouse in this)
            GLCore.glString(matrix, text, pos + vec(width / 2 - GLCore.glStringWidth(text) / 2, height / 2), color, shadow = OptionCore.TEXT_SHADOW.isEnabled, centered = true)
        else
            GLCore.glString(matrix, text, pos + vec(width / 2 - GLCore.glStringWidth(text) / 2, height / 2), color, shadow = false, centered = true)
        if (mouse in this) {
            drawHoveringText(mouse)
            GLCore.lighting(false)
            GLCore.depth(false)
        }
    }

}