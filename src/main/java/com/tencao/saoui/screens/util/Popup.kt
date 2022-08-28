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

package com.tencao.saoui.screens.util

import com.mojang.blaze3d.matrix.MatrixStack
import com.tencao.saomclib.Client
import com.tencao.saomclib.GLCore
import com.tencao.saomclib.GLCore.glTexturedRectV2
import com.tencao.saomclib.party.PlayerInfo
import com.tencao.saomclib.utils.math.Vec2d
import com.tencao.saomclib.utils.math.vec
import com.tencao.saoui.SAOCore
import com.tencao.saoui.SoundCore
import com.tencao.saoui.api.elements.IconElement
import com.tencao.saoui.api.elements.IconTextElement
import com.tencao.saoui.api.elements.animator.Easing
import com.tencao.saoui.api.elements.basicAnimation
import com.tencao.saoui.api.elements.getRequirementDesc
import com.tencao.saoui.api.events.ProfileInfoEvent
import com.tencao.saoui.play
import com.tencao.saoui.screens.CoreGUI
import com.tencao.saoui.screens.unaryPlus
import com.tencao.saoui.util.ColorIntent
import com.tencao.saoui.util.ColorUtil
import com.tencao.saoui.util.IconCore
import com.tencao.saoui.util.PlayerStats
import net.minecraft.advancements.Advancement
import net.minecraft.client.Minecraft
import net.minecraft.inventory.container.Slot
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.MinecraftForge
import kotlin.math.max

open class Popup<T : Any>(open var header: String, open var text: List<String>, open var footer: String, val map: Map<IconElement, T>) : CoreGUI<T>(header, Vec2d.ZERO) {

    private val rl = ResourceLocation(SAOCore.MODID, "textures/menu/parts/alertbg.png")
    internal /*private*/ var expansion = 0f
    internal /*private*/ var currheight = h * 0.625
    internal /*private*/ var eol = 1f

    var mouseSet: Boolean = false
    override var previousMouse: Vec2d = Vec2d(0.0, 0.0)

    companion object {
        internal const val w = 220f
        internal const val h = 160f
    }

    override fun init(minecraft: Minecraft, width: Int, height: Int) {
        val animDuration = 20f

        elements.clear()

        pos = vec(width / 2.0, height / 2.0)
        destination = pos

        val childrenXSeparator = w / map.size
        val childrenXOffset = (-w / 2) + (childrenXSeparator / 2 - 9)
        val childrenYOffset = h * 0.1875 - 9 + h * 0.03125 * (text.size)

        map.asSequence().forEachIndexed { index, entry ->
            val button = entry.key
            val result = entry.value
            button.onClick { _, _ ->
                this@Popup.result = result
                closeScreen()
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
                easing = Easing.easeInQuint
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
        super.init(minecraft, width, height)
    }

    override fun render(matrixStack: MatrixStack, mouseX: Int, mouseY: Int, partialTicks: Float) {
        // TODO: these could be moved to Liblib's Sprites. Maybe.

        GLCore.pushMatrix()
        GLCore.translate(pos.x, pos.y, 0.0)
        if (expansion < 0.2f) {
            GLCore.scale(expansion * 4 + 0.2f, expansion * 4 + 0.2f, 1f)
        }
        if (eol < 1f) {
            GLCore.scale(eol, 1f, 1f)
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
        val posX = -w / 2.0
        val posY = -h / 2.0
        glTexturedRectV2(posX, posY, width = w.toDouble(), height = step1, srcX = 0.0, srcY = 0.0, srcWidth = 256.0, srcHeight = 64.0) // Title bar
        glTexturedRectV2(posX, posY + step1, width = w.toDouble(), height = shadows, srcX = 0.0, srcY = 64.0, srcWidth = 256.0, srcHeight = 32.0) // Top shadow
        glTexturedRectV2(posX, posY + step1 + shadows, width = w.toDouble(), height = step3, srcX = 0.0, srcY = 96.0, srcWidth = 256.0, srcHeight = 32.0) // Text lines
        glTexturedRectV2(posX, posY + step1 + shadows + step3, width = w.toDouble(), height = shadows, srcX = 0.0, srcY = 128.0, srcWidth = 256.0, srcHeight = 32.0) // Bottom shadow
        glTexturedRectV2(posX, posY + step1 + shadows + step3 + shadows, width = w.toDouble(), height = step5, srcX = 0.0, srcY = 160.0, srcWidth = 256.0, srcHeight = 96.0) // Button bar

        if (alpha > 0.03f) GLCore.glString(header, -GLCore.glStringWidth(header) / 2, (-h / 2.0 + step1 / 2).toInt(), ColorUtil.DEFAULT_BOX_FONT_COLOR.multiplyAlpha(alpha), matrixStack = matrixStack, centered = true)
        (text.indices).forEach {
            if (alpha > 0.56f) GLCore.glString(text[it], -GLCore.glStringWidth(text[it]) / 2, (-h / 2.0 + step1 + shadows + step3 / (text.size) * (it + 0.5)).toInt(), ColorUtil.DEFAULT_FONT_COLOR.multiplyAlpha((alpha - 0.5f) / 0.5f), matrixStack = matrixStack, centered = true)
        }
        if (alpha > 0.03f) GLCore.glString(footer, -GLCore.glStringWidth(footer) / 2, (-h / 2.0 + step1 + shadows + step3 + (step5 / 2)).toInt(), ColorUtil.DEFAULT_BOX_FONT_COLOR.multiplyAlpha(alpha), matrixStack = matrixStack, centered = true)

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

        super.render(matrixStack, mouseX, mouseY, partialTicks)
    }

    override fun mouseMoved(mouseX: Double, mouseY: Double) {
        if (!mouseSet) {
            previousMouse = Vec2d(mouseX, mouseY)
            mouseSet = true
        }
        pos = pos.add(mouseX - previousMouse.x, mouseY - previousMouse.y)
        previousMouse = Vec2d(mouseX, mouseY)
        super.mouseMoved(mouseX, mouseY)
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
        mouseSet = false
        return super.mouseReleased(mouseX, mouseY, button)
    }

    override fun onClose() {
        +basicAnimation(this, "eol") {
            to = 0f
            easing = Easing.linear
            duration = 10f
            completion = Runnable {
                super.closeScreen()
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
        super.onClose()
    }
}

class PopupYesNo(header: String, text: List<String>, footer: String) : Popup<PopupYesNo.Result>(
    header,
    text,
    footer,
    mapOf(
        IconElement(IconCore.CONFIRM, description = mutableListOf("Confirm"))
            .setBgColor(ColorIntent.NORMAL, ColorUtil.CONFIRM_COLOR)
            .setBgColor(ColorIntent.HOVERED, ColorUtil.CONFIRM_COLOR_LIGHT)
            .setFontColor(ColorIntent.NORMAL, ColorUtil.DEFAULT_COLOR)
            .setFontColor(ColorIntent.NORMAL, ColorUtil.DEFAULT_COLOR)
            to Result.YES,
        IconElement(IconCore.CANCEL, description = mutableListOf("Cancel"))
            .setBgColor(ColorIntent.NORMAL, ColorUtil.CANCEL_COLOR)
            .setBgColor(ColorIntent.HOVERED, ColorUtil.CANCEL_COLOR_LIGHT)
            .setFontColor(ColorIntent.NORMAL, ColorUtil.DEFAULT_COLOR)
            .setFontColor(ColorIntent.NORMAL, ColorUtil.DEFAULT_COLOR)
            to Result.NO
    )
) {

    constructor(header: String, text: String, footer: String) : this(header, listOf(text), footer)

    init {
        result = Result.NO
    }

    enum class Result {
        YES,
        NO
    }
}

class PopupNotice(header: String, text: List<String>, footer: String) : Popup<PopupNotice.Result>(
    header,
    text,
    footer,
    mapOf(
        IconElement(IconCore.CONFIRM, description = mutableListOf("Confirm"))
            .setBgColor(ColorIntent.NORMAL, ColorUtil.CONFIRM_COLOR)
            .setBgColor(ColorIntent.HOVERED, ColorUtil.CONFIRM_COLOR_LIGHT)
            .setFontColor(ColorIntent.NORMAL, ColorUtil.DEFAULT_COLOR)
            .setFontColor(ColorIntent.NORMAL, ColorUtil.DEFAULT_COLOR)
            to Result.YES
    )
) {

    constructor(header: String, text: String, footer: String) : this(header, listOf(text), footer)

    init {
        result = Result.YES
    }

    enum class Result {
        YES
    }
}

class PopupItem(header: String, text: List<String>, footer: String) : Popup<PopupItem.Result>(
    header,
    text,
    footer,
    mapOf(
        IconElement(IconCore.EQUIPMENT, description = mutableListOf("Equip"))
            .setBgColor(ColorIntent.NORMAL, ColorUtil.DEFAULT_COLOR)
            .setBgColor(ColorIntent.HOVERED, ColorUtil.HOVER_COLOR)
            .setFontColor(ColorIntent.NORMAL, ColorUtil.DEFAULT_COLOR)
            .setFontColor(ColorIntent.NORMAL, ColorUtil.DEFAULT_COLOR)
            to Result.EQUIP,
        IconElement(IconCore.CANCEL, description = mutableListOf("Drop"))
            .setBgColor(ColorIntent.NORMAL, ColorUtil.CANCEL_COLOR)
            .setBgColor(ColorIntent.HOVERED, ColorUtil.CANCEL_COLOR_LIGHT)
            .setFontColor(ColorIntent.NORMAL, ColorUtil.DEFAULT_COLOR)
            .setFontColor(ColorIntent.NORMAL, ColorUtil.DEFAULT_COLOR)
            to Result.DROP
    )
) {

    constructor(header: String, text: String, footer: String) : this(header, listOf(text), footer)

    init {
        result = Result.CANCEL
    }

    enum class Result {
        EQUIP,
        DROP,
        CANCEL
    }
}

class PopupSlotSelection(header: String, text: List<String>, footer: String, slots: Set<Slot>) : Popup<Int>(header, text, footer, getButtons(slots)) {

    init {
        result = -1
    }

    companion object {

        fun getButtons(slots: Set<Slot>): Map<IconElement, Int> {
            val map = mutableMapOf<IconElement, Int>()
            slots.forEach {
                map[IconElement(it.stack.toIcon())] = it.slotNumber
            }
            return map
        }
    }
}

/**
 * This is used to select a slot on the hotbar
 *
 * TODO find better solution or allow user input
 */
class PopupHotbarSelection(header: String, text: List<String>, footer: String) : Popup<PopupHotbarSelection.Result>(header, text, footer, getHotbarList()) {

    constructor(header: String, text: String, footer: String) : this(header, listOf(text), footer)

    init {
        result = Result.CANCEL
    }

    enum class Result(val slot: Int) {
        SLOT_ONE(36),
        SLOT_TWO(37),
        SLOT_THREE(38),
        SLOT_FOUR(39),
        SLOT_FIVE(40),
        SLOT_SIX(41),
        SLOT_SEVEN(42),
        SLOT_EIGHT(43),
        SLOT_NINE(44),
        CANCEL(-1)
    }

    companion object {

        fun getHotbarList(): Map<IconElement, Result> {
            val map = linkedMapOf<IconElement, Result>()
            val inventory = Client.player!!.container
            for (i in 36..44) {
                val stack = inventory.getSlot(i).stack
                map[
                    IconElement(stack.toIcon())
                        .setBgColor(ColorIntent.NORMAL, ColorUtil.DEFAULT_COLOR)
                        .setBgColor(ColorIntent.HOVERED, ColorUtil.HOVER_COLOR)
                        .setFontColor(ColorIntent.NORMAL, ColorUtil.DEFAULT_COLOR)
                        .setFontColor(ColorIntent.NORMAL, ColorUtil.DEFAULT_COLOR)
                ] = Result.values()[i - 36]
            }
            return map
        }
    }
}

class PopupPlayerInspect(player: PlayerInfo, elements: List<IconElement>) : Popup<Int>(
    player.username,
    player.player?.let {
        val playerInfo = ProfileInfoEvent(it, PlayerStats.instance().stats.getStatsString(it))
        MinecraftForge.EVENT_BUS.post(playerInfo)
        playerInfo.info
    } ?: listOf("Player data unknown"),
    "",
    elements.associateBy({ it }, { elements.indexOf(it) })
) {

    init {
        result = -1
    }
}

/*
class PopupCraft(val recipe: IRecipe): Popup<Int>(recipe.recipeOutput.displayName, recipe.recipeOutput.itemDesc(), "", getButtons()){
    private val countPerCraft = recipe.recipeOutput.count
    override var result: Int = countPerCraft
    override var footer
        get() = "Craft $result ${recipe.recipeOutput.displayName}"
        set(_) {}

    override fun init(minecraft: Minecraft, width: Int, height: Int) {
        val animDuration = 20f

        elements.clear()

        pos = vec(width / 2.0, height / 2.0)
        destination = pos

        val childrenXSeparator = w / map.size
        val childrenXOffset = (-w / 2) + (childrenXSeparator / 2 - 9)
        val childrenYOffset = h * 0.1875 - 9 + h * 0.03125 * (text.size)

        map.asSequence().forEachIndexed { index, entry ->
            val button = entry.key
            val result = entry.value
            button.onClick { _, _ ->
                if (result == 0){
                    CraftingUtil.craft(recipe, this.result / countPerCraft)
                    closeScreen()
                }
                else {
                    //Add result to current stack count
                    this.result += (countPerCraft * result)
                    //If less than one, make it one
                    this.result = max(this.result, countPerCraft)
                    //If higher than maximum craft size, reduce
                    this.result = min(this.result, CraftingUtil.getMaxStack(recipe))
                }
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
                easing = Easing.easeInQuint
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
        super.init(minecraft, width, height)
    }

    companion object{
        fun getButtons(): Map<IconElement, Int>{
            return mapOf(
                    Pair(IconTextElement("-10", mutableListOf("Decrease by 10")), -10),
                    Pair(IconTextElement("-1", mutableListOf("Decrease by 1")), -1),
                    Pair(IconElement(IconCore.CONFIRM, description = mutableListOf("Craft")), 0),
                    Pair(IconTextElement("1", mutableListOf("Increase by 1")), 1),
                    Pair(IconTextElement("10", mutableListOf("Increase by 10")), 10)
            )
        }
    }


}*/

class PopupAdvancement(advancement: Advancement) : Popup<PopupAdvancement.Result>(
    advancement.displayText.unformattedComponentText,
    advancement.getRequirementDesc(),
    "",
    mapOf(
        IconTextElement("<--", description = mutableListOf("Previous"))
            to Result.PREVIOUS,
        IconTextElement("-->", description = mutableListOf("Next"))
            to Result.NEXT
    )
) {

    init {
        result = Result.NONE
    }

    enum class Result {
        NONE,
        NEXT,
        PREVIOUS;
    }
}
