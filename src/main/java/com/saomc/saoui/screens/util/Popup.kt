/*
 * Copyright (C) 2016-2019 Arnaud 'Bluexin' Solé
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.saomc.saoui.screens.util

import com.saomc.saoui.GLCore
import com.saomc.saoui.GLCore.glTexturedRectV2
import com.saomc.saoui.SAOCore
import com.saomc.saoui.SoundCore
import com.saomc.saoui.api.elements.IconElement
import com.saomc.saoui.api.elements.basicAnimation
import com.saomc.saoui.play
import com.saomc.saoui.screens.CoreGUI
import com.saomc.saoui.screens.ItemIcon
import com.saomc.saoui.screens.unaryPlus
import com.saomc.saoui.util.ColorIntent
import com.saomc.saoui.util.ColorUtil
import com.saomc.saoui.util.IconCore
import com.teamwizardry.librarianlib.features.animator.Easing
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.math.Vec2d
import net.minecraft.client.Minecraft
import net.minecraft.util.ResourceLocation
import kotlin.math.max

open class Popup<T : Any>(var title: String, var text: List<String>, var footer: String, private val buttons: Map<IconElement, T>) : CoreGUI<T>(Vec2d.ZERO) {

    private val rl = ResourceLocation(SAOCore.MODID, "textures/menu/parts/alertbg.png")
    internal /*private*/ var expansion = 0f
    internal /*private*/ var currheight = h * 0.625
    internal /*private*/ var eol = 1f

    companion object {
        private const val w = 220.0
        private const val h = 160.0
    }

    override fun initGui() {
        val animDuration = 20f

        elements.clear()

        pos = vec(width / 2.0, height / 2.0)
        destination = pos

        val childrenXSeparator = w / buttons.size
        val childrenXOffset = (-w / 2) + (childrenXSeparator / 2 - 9)
        val childrenYOffset = h * 0.1875 - 9 + h * 0.03125 * (text.size)

        buttons.asSequence().forEachIndexed { index, entry ->
            val button = entry.key
            val result = entry.value
            button.onClick { _, _ ->
                this@Popup.result = result
                onGuiClosed()
                true
            }
            button.pos = vec(childrenXOffset + childrenXSeparator * index, childrenYOffset)
            button.destination = button.pos
            +basicAnimation(button, "opacity") {
                duration = animDuration
                from = 0f
                easing = Easing.easeInQuint
            }
            +basicAnimation(button, "pos") {
                duration = animDuration
                from = vec(button.pos.x, h * 0.125 - 9)
                easing = Easing.easeInQuint
            }
            +basicAnimation(button, "scale") {
                duration = animDuration
                from = Vec2d.ZERO
                easing = object : Easing() {
                    override fun invoke(progress: Float): Float {
                        val t = easeInQuint(progress)
                        return if (t < 0.2f) t * 4 + 0.2f
                        else 1f
                    }

                }
            }
            +button

        }
        +basicAnimation(this, "expansion") {
            to = 1f
            duration = animDuration
            easing = Easing.easeInQuint
        }
        +basicAnimation(this, "currheight") {
            to = h
            duration = animDuration
            easing = Easing.easeInQuint
        }
        SoundCore.MESSAGE.play()
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        // TODO: these could be moved to Liblib's Sprites. Maybe.

        GLCore.pushMatrix()
        GLCore.translate(pos.x, pos.y, 0.0)
        if (expansion < 0.2f) {
            GLCore.glScalef(expansion * 4 + 0.2f, expansion * 4 + 0.2f, 1f)
        }
        if (eol < 1f) {
            GLCore.glScalef(eol, 1f, 1f)
        }

        val shadows = if (expansion > 0.66f) {
            h * 0.125
        } else {
            h * 0.1875 * expansion
        }

        val step1 = h * 0.250
        val step3 = max(h * 0.125 - h * 0.375 * (1 - expansion), 0.0) * text.size / 2f // TODO: handle multiline text
        val step5 = h * 0.375

//        if (shadows < 20.0) SAOCore.LOGGER.info("h=$h; shadows=$shadows; step3=$step3; expansion=$expansion")

        val h = h - h * 0.250 * (1 - expansion) + h * 0.0625 * (text.size + 2)

        val alpha = if (expansion < 1f) expansion else eol

        GLCore.glBindTexture(rl)
        GLCore.color(ColorUtil.DEFAULT_COLOR.multiplyAlpha(alpha))
        glTexturedRectV2(-w / 2.0, -h / 2.0, width = w, height = step1, srcX = 0.0, srcY = 0.0, srcWidth = 256.0, srcHeight = 64.0) // Title bar
        glTexturedRectV2(-w / 2.0, -h / 2.0 + step1, width = w, height = shadows, srcX = 0.0, srcY = 64.0, srcWidth = 256.0, srcHeight = 32.0) // Top shadow
        glTexturedRectV2(-w / 2.0, -h / 2.0 + step1 + shadows, width = w, height = step3, srcX = 0.0, srcY = 96.0, srcWidth = 256.0, srcHeight = 32.0) // Text lines
        glTexturedRectV2(-w / 2.0, -h / 2.0 + step1 + shadows + step3, width = w, height = shadows, srcX = 0.0, srcY = 128.0, srcWidth = 256.0, srcHeight = 32.0) // Bottom shadow
        glTexturedRectV2(-w / 2.0, -h / 2.0 + step1 + shadows + step3 + shadows, width = w, height = step5, srcX = 0.0, srcY = 160.0, srcWidth = 256.0, srcHeight = 96.0) // Button bar

        if (alpha > 0.03f) GLCore.glString(title, -GLCore.glStringWidth(title) / 2, (-h / 2.0 + step1 / 2).toInt(), ColorUtil.DEFAULT_BOX_FONT_COLOR.multiplyAlpha(alpha), centered = true)
        (text.indices).forEach {
            if (alpha > 0.56f) GLCore.glString(text[it], -GLCore.glStringWidth(text[it]) / 2, (-h / 2.0 + step1 + shadows + step3 / (text.size) * (it + 0.5)).toInt(), ColorUtil.DEFAULT_FONT_COLOR.multiplyAlpha((alpha - 0.5f) / 0.5f), centered = true)
        }
        if (alpha > 0.03f) GLCore.glString(footer, -GLCore.glStringWidth(footer) / 2, (-h / 2.0 + step1 + shadows + step3 + (step5 / 2)).toInt(), ColorUtil.DEFAULT_BOX_FONT_COLOR.multiplyAlpha(alpha), centered = true)

        // Guides
        /*GLCore.glBindTexture(StringNames.gui)
        GLCore.color(ColorUtil.DEAD_COLOR.multiplyAlpha(0.5f))
        for (i in 1..15) {
            GLCore.glTexturedRect(-w / 2.0 + i * w / 16, -h / 2.0, 1.0, h, 5.0, 120.0, 2.0, 2.0)
            GLCore.glTexturedRect(-w / 2.0, -h / 2.0 + i * h / 16.0, w, 1.0, 5.0, 120.0, 2.0, 2.0)
        }
        GLCore.color(0f, 1f, 0f, 0.5f)
        GLCore.glTexturedRect(-w / 2.0, h * 0.3125, w, 1.0, 5.0, 120.0, 2.0, 2.0)

        val childrenXSeparator = w / buttons.size
        val childrenXOffset = -childrenXSeparator / buttons.size

        GLCore.glTexturedRect(childrenXOffset, -h / 2, 1.0, h, 5.0, 120.0, 2.0, 2.0)
        GLCore.glTexturedRect(childrenXOffset + childrenXSeparator, -h / 2, 1.0, h, 5.0, 120.0, 2.0, 2.0)*/

        GLCore.popMatrix()

        super.drawScreen(mouseX, mouseY, partialTicks)
    }

    override fun onGuiClosed() {
        +basicAnimation(this, "eol") {
            to = 0f
            easing = Easing.linear
            duration = 10f
            completion = Runnable {
                super.onGuiClosed()
            }
        }
        elements.forEach { button ->
            +basicAnimation(button, "opacity") {
                duration = 10f
                to = 0f
                easing = Easing.linear
            }
            +basicAnimation(button, "scale") {
                duration = 10f
                to = vec(0.0, 1.0)
                easing = Easing.linear
            }
        }

        SoundCore.DIALOG_CLOSE.play()
    }
}

class PopupYesNo(title: String, text: List<String>, footer: String) : Popup<PopupYesNo.Result>(title, text, footer, mapOf(
        IconElement(IconCore.CONFIRM)
                .setBgColor(ColorIntent.NORMAL, ColorUtil.CONFIRM_COLOR)
                .setBgColor(ColorIntent.HOVERED, ColorUtil.CONFIRM_COLOR_LIGHT)
                .setFontColor(ColorIntent.NORMAL, ColorUtil.DEFAULT_COLOR)
                .setFontColor(ColorIntent.NORMAL, ColorUtil.DEFAULT_COLOR)
                to Result.YES,
        IconElement(IconCore.CANCEL)
                .setBgColor(ColorIntent.NORMAL, ColorUtil.CANCEL_COLOR)
                .setBgColor(ColorIntent.HOVERED, ColorUtil.CANCEL_COLOR_LIGHT)
                .setFontColor(ColorIntent.NORMAL, ColorUtil.DEFAULT_COLOR)
                .setFontColor(ColorIntent.NORMAL, ColorUtil.DEFAULT_COLOR)
                to Result.NO
)) {

    constructor(title: String, text: String, footer: String) : this(title, listOf(text), footer)

    init {
        result = Result.NO
    }

    enum class Result {
        YES,
        NO
    }
}

class PopupItem(title: String, text: List<String>, footer: String) : Popup<PopupItem.Result>(title, text, footer, mapOf(
        IconElement(IconCore.EQUIPMENT)
                .setBgColor(ColorIntent.NORMAL, ColorUtil.DEFAULT_COLOR)
                .setBgColor(ColorIntent.HOVERED, ColorUtil.HOVER_COLOR)
                .setFontColor(ColorIntent.NORMAL, ColorUtil.DEFAULT_COLOR)
                .setFontColor(ColorIntent.NORMAL, ColorUtil.DEFAULT_COLOR)
                to Result.EQUIP,
        IconElement(IconCore.CRAFTING)
                .setBgColor(ColorIntent.NORMAL, ColorUtil.DEFAULT_COLOR)
                .setBgColor(ColorIntent.HOVERED, ColorUtil.HOVER_COLOR)
                .setFontColor(ColorIntent.NORMAL, ColorUtil.DEFAULT_COLOR)
                .setFontColor(ColorIntent.NORMAL, ColorUtil.DEFAULT_COLOR)
                to Result.USE,
        IconElement(IconCore.CANCEL)
                .setBgColor(ColorIntent.NORMAL, ColorUtil.CANCEL_COLOR)
                .setBgColor(ColorIntent.HOVERED, ColorUtil.CANCEL_COLOR_LIGHT)
                .setFontColor(ColorIntent.NORMAL, ColorUtil.DEFAULT_COLOR)
                .setFontColor(ColorIntent.NORMAL, ColorUtil.DEFAULT_COLOR)
                to Result.DROP,
        IconElement(IconCore.PARTY)
                .setBgColor(ColorIntent.NORMAL, ColorUtil.DEFAULT_COLOR)
                .setBgColor(ColorIntent.HOVERED, ColorUtil.HOVER_COLOR)
                .setFontColor(ColorIntent.NORMAL, ColorUtil.DEFAULT_COLOR)
                .setFontColor(ColorIntent.NORMAL, ColorUtil.DEFAULT_COLOR)
                to Result.TRADE
)) {

    constructor(title: String, text: String, footer: String) : this(title, listOf(text), footer)

    init {
        result = Result.CANCEL
    }

    enum class Result {
        EQUIP,
        USE,
        DROP,
        TRADE,
        CANCEL
    }
}

/**
 * This is used to select a slot on the hotbar
 *
 * TODO find better solution or allow user input
 */
class PopupHotbarSelection(title: String, text: List<String>, footer: String) : Popup<PopupHotbarSelection.Result>(title, text, footer, getHotbarList()) {


    constructor(title: String, text: String, footer: String) : this(title, listOf(text), footer)

    init {
        result = Result.CANCEL
    }

    enum class Result(val slot: Int) {
        ONE(36),
        TWO(37),
        THREE(38),
        FOUR(39),
        FIVE(40),
        SIX(41),
        SEVEN(42),
        EIGHT(43),
        NEIN(44),
        CANCEL(-1)
    }

    companion object {

        fun getHotbarList():Map<IconElement, Result>{
            val map = linkedMapOf<IconElement, Result>()
            val inventory = Minecraft.getMinecraft().player.inventoryContainer
            for (i in 36..44){
                val stack = inventory.getSlot(i).stack
                map[IconElement(icon = ItemIcon { stack })
                        .setBgColor(ColorIntent.NORMAL, ColorUtil.DEFAULT_COLOR)
                        .setBgColor(ColorIntent.HOVERED, ColorUtil.HOVER_COLOR)
                        .setFontColor(ColorIntent.NORMAL, ColorUtil.DEFAULT_COLOR)
                        .setFontColor(ColorIntent.NORMAL, ColorUtil.DEFAULT_COLOR)] = Result.values()[i - 36]
            }
            return map
        }
    }
}