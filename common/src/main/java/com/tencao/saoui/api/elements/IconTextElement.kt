package com.tencao.saoui.api.elements

import com.mojang.blaze3d.vertex.PoseStack
import com.tencao.saoui.GLCore
import com.tencao.saoui.config.OptionCore
import com.tencao.saoui.util.ColorUtil
import com.tencao.saoui.util.IconCore
import com.tencao.saoui.util.math.plus
import com.tencao.saoui.util.math.vec
import org.joml.Vector2d

class IconTextElement(var text: String, override val description: MutableList<String> = mutableListOf()) : IconElement(
    IconCore.NONE) {

    init {
        if (text.length > 3) text = text.substring(0, 2)
    }

    override fun draw(mouse: Vector2d, partialTicks: Float, poseStack: PoseStack) {
        super.draw(mouse, partialTicks, poseStack)
        val color = ColorUtil.multiplyAlpha(getTextColor(mouse), opacity)
        GLCore.color(color)
        if (this.highlighted || mouse in this) {
            GLCore.glString(text, pos + vec(width / 2 - GLCore.glStringWidth(text) / 2, height / 2), color, poseStack = poseStack, shadow = OptionCore.TEXT_SHADOW.isEnabled, centered = true)
        } else {
            GLCore.glString(text, pos + vec(width / 2 - GLCore.glStringWidth(text) / 2, height / 2), color, poseStack = poseStack, shadow = false, centered = true)
        }
        if (mouse in this) {
            drawHoveringText(poseStack, mouse)
            GLCore.lighting(false)
            GLCore.depth(false)
        }
    }
}
