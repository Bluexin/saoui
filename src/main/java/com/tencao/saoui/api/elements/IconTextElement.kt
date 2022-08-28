package com.tencao.saoui.api.elements

import com.mojang.blaze3d.matrix.MatrixStack
import com.tencao.saomclib.GLCore
import com.tencao.saomclib.utils.math.Vec2d
import com.tencao.saomclib.utils.math.vec
import com.tencao.saoui.config.OptionCore
import com.tencao.saoui.util.ColorUtil
import com.tencao.saoui.util.IconCore

class IconTextElement(var text: String, override val description: MutableList<String> = mutableListOf()) : IconElement(IconCore.NONE) {

    init {
        if (text.length > 3) text = text.substring(0, 2)
    }

    override fun draw(mouse: Vec2d, partialTicks: Float, matrixStack: MatrixStack) {
        super.draw(mouse, partialTicks, matrixStack)
        val color = ColorUtil.multiplyAlpha(getTextColor(mouse), opacity)
        GLCore.color(color)
        if (this.highlighted || mouse in this) {
            GLCore.glString(text, pos + vec(width / 2 - GLCore.glStringWidth(text) / 2, height / 2), color, matrixStack = matrixStack, shadow = OptionCore.TEXT_SHADOW.isEnabled, centered = true)
        } else {
            GLCore.glString(text, pos + vec(width / 2 - GLCore.glStringWidth(text) / 2, height / 2), color, matrixStack = matrixStack, shadow = false, centered = true)
        }
        if (mouse in this) {
            drawHoveringText(mouse)
            GLCore.lighting(false)
            GLCore.depth(false)
        }
    }
}
