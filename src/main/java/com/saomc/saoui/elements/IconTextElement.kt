package com.saomc.saoui.elements

import com.saomc.saoui.GLCore
import com.saomc.saoui.config.OptionCore
import com.saomc.saoui.elements.controllers.IController
import com.saomc.saoui.util.ColorUtil
import com.saomc.saoui.util.IconCore
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.plus
import com.teamwizardry.librarianlib.features.math.Vec2d

class IconTextElement(val text: String, controller: IController, override val description: MutableList<String> = mutableListOf(), override var function: () -> Unit = {}): Element(icon = IconCore.NONE, controllingParent = controller) {

    override fun draw(mouse: Vec2d, partialTicks: Float) {
        val color = ColorUtil.multiplyAlpha(getTextColor(mouse), opacity)
        GLCore.color(color)
        if (this.highlighted || mouse in this)
            GLCore.glString(text, pos + vec(width / 2 - GLCore.glStringWidth(text) / 2, height / 2), color, shadow = OptionCore.TEXT_SHADOW.isEnabled, centered = true)
        else
            GLCore.glString(text, pos + vec(width / 2 - GLCore.glStringWidth(text) / 2, height / 2), color, shadow = false, centered = true)
    }
}