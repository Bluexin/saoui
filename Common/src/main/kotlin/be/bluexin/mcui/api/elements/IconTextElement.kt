package be.bluexin.mcui.api.elements

import org.joml.Vector2d
import com.tencao.saomclib.utils.math.vec
import be.bluexin.mcui.GLCore
import be.bluexin.mcui.config.OptionCore
import be.bluexin.mcui.util.ColorUtil
import be.bluexin.mcui.util.IconCore

class IconTextElement(var text: String, override val description: MutableList<String> = mutableListOf()) : IconElement(IconCore.NONE) {

    init {
        if (text.length > 3) text = text.substring(0, 2)
    }

    override fun draw(mouse: Vector2d, partialTicks: Float) {
        super.draw(mouse, partialTicks)
        val color = ColorUtil.multiplyAlpha(getTextColor(mouse), opacity)
        GLCore.color(color)
        if (this.highlighted || mouse in this) {
            GLCore.glString(text, pos + vec(width / 2 - GLCore.glStringWidth(text) / 2, height / 2), color, shadow = OptionCore.TEXT_SHADOW.isEnabled, centered = true)
        } else {
            GLCore.glString(text, pos + vec(width / 2 - GLCore.glStringWidth(text) / 2, height / 2), color, shadow = false, centered = true)
        }
        if (mouse in this) {
            drawHoveringText(mouse)
            GLCore.lighting(false)
            GLCore.depth(false)
        }
    }
}
