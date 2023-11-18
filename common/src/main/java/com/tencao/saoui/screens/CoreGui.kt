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

package com.tencao.saoui.screens

import com.mojang.blaze3d.platform.InputConstants
import com.mojang.blaze3d.vertex.PoseStack
import com.tencao.saoui.GLCore
import com.tencao.saoui.Vector2d
import com.tencao.saoui.api.elements.*
import com.tencao.saoui.api.elements.animator.Animation
import com.tencao.saoui.api.elements.animator.Animator
import com.tencao.saoui.api.screens.IIcon
import com.tencao.saoui.config.OptionCore
import com.tencao.saoui.screens.util.Popup
import com.tencao.saoui.util.Client
import com.tencao.saoui.util.math.clamp
import com.tencao.saoui.util.math.minus
import com.tencao.saoui.util.math.vec
import com.tencao.saoui.util.scaledHeight
import com.tencao.saoui.util.scaledWidth
import com.tencao.saoui.util.translate
import net.minecraft.client.KeyMapping
import net.minecraft.client.Minecraft
import net.minecraft.client.Options
import net.minecraft.client.gui.screens.Screen
import org.joml.Vector2d
import org.joml.Vector2f

/**
 * Part of saoui by Bluexin, released under GNU GPLv3.
 *
 * @author Bluexin
 */
abstract class CoreGUI<T : Any>(name: String, final override var pos: Vector2d, override var destination: Vector2d = pos, val elements: MutableList<NeoElement> = mutableListOf()) :
    Screen(
        name.translate()
    ),
    INeoParent {

    var viewSet: Boolean = false
    var isPlayerPresent = false
    var playerView: Vector2f = Vector2f(Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY)
    open var previousMouse: Vector2d = Vector2d(0.0, 0.0)

    var subGui: CoreGUI<*>? = null
        private set

    override var parent: INeoParent? = null

    override var isOpen: Boolean = true

    val getPopup: Popup<*>?
        get() {
            return if (this is Popup<*>) {
                this
            } else subGui?.getPopup
        }

    override fun init(minecraft: Minecraft, width: Int, height: Int) {
        super.init(minecraft, width, height)
        if (Client.player != null) {
            isPlayerPresent = true
        }
    }

    override fun render(poseStack: PoseStack, mouseX: Int, mouseY: Int, partialTicks: Float) {
        GLCore.glBlend(true)
        GLCore.pushMatrix()

        if (Client.player != null) {
            val player = Client.player!!
            if (OptionCore.UI_MOVEMENT.isEnabled) {
                if (!viewSet) {
                    playerView = Vector2f(
                        player.xRot,
                        player.yRot
                    )
                    previousMouse = Vector2d(mouseX, mouseY)
                    viewSet = true
                } else {
                    player.xRot = playerView.x - ((width / 2 - mouseX) / 2) * 0.25F
                    player.yRot = playerView.y - ((height / 2 - mouseY) / 2) * 0.25F
                    pos = pos.add((mouseX - previousMouse.x) * 0.25, (mouseY - previousMouse.y) * 0.25)
                    previousMouse = Vector2d(mouseX, mouseY)
                }
            } else if (viewSet) {
                player.xRot = playerView.x
                player.yRot = playerView.y
            }
        }
        GLCore.translate(pos.x, pos.y, 0.0)

        val mousePos = if (subGui == null) vec(mouseX, mouseY) - pos else Vector2d(0, 0)
        elements.forEach { it.drawBackground(mousePos, partialTicks, poseStack) }
        elements.forEach { it.draw(mousePos, partialTicks, poseStack) }
        elements.forEach { it.drawForeground(mousePos, partialTicks, poseStack) }

        // GLCore.lighting(false)

        GLCore.popMatrix()

        subGui?.render(poseStack, mouseX, mouseY, partialTicks)
    }

    override fun tick() {
        subGui?.tick() ?: elements.forEach(NeoElement::update)
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, delta: Double): Boolean {
        val x = mouseX * this.width
        val y = this.height - mouseY * this.height

        return if (delta > 0.0) this.mouseClicked(vec(x, y), MouseButton.SCROLL_UP)
        else if (delta < 0.0) this.mouseClicked(vec(x, y), MouseButton.SCROLL_DOWN)
        else super.mouseScrolled(mouseX, mouseY, delta)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, mouseButton: Int): Boolean {
        return this.mouseClicked(vec(mouseX, mouseY), MouseButton.fromInt(mouseButton))
    }

    override fun mouseClicked(pos: Vector2d, mouseButton: MouseButton): Boolean {
        if (subGui != null) return subGui!!.mouseClicked(pos, mouseButton)
        return elements.any { it.mouseClicked(pos - this.pos, mouseButton) }
    }

    override fun isPauseScreen(): Boolean {
        return OptionCore.GUI_PAUSE.isEnabled
    }

    fun tlCategory(icon: IIcon, body: (CategoryButton.() -> Unit)? = null) {
        this.elements += CategoryButton(IconElement(icon, vec(0, 25 * elements.size)), this, body)
    }

    fun tlCategory(icon: IIcon, index: Int, body: (CategoryButton.() -> Unit)? = null): CategoryButton {
        return CategoryButton(IconElement(icon, vec(0, 25 * index)), this, body)
    }

    operator fun NeoElement.unaryPlus() {
        this@CoreGUI.elements += this
    }

    fun <R : Any> openGui(gui: CoreGUI<R>): CoreGUI<R> {
        check(subGui == null) { "Already opened a sub gui of type ${subGui!!::class.qualifiedName}" }
        subGui = gui
        gui.parent = this
        KeyMapping.releaseAll()

        gui.resize(Client.minecraft, scaledWidth, scaledHeight)

        gui += { subGui = null }

        return gui
    }

    override fun resize(minecraft: Minecraft, width: Int, height: Int) {
        super.resize(minecraft, width, height)
        subGui?.resize(minecraft, width, height)
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean { // TODO: keyboard nav (should also support controller)
        val key = InputConstants.getKey(keyCode, scanCode)
        val button = KeyButton.getButton(keyCode)

        return if (subGui == null) {
            if (button == KeyButton.ESCAPE) {
                this.onClose()
                if (this.parent == null) {
                    Client.displayGuiScreen(null)
                    if (Client.minecraft.screen == null) Client.minecraft.isWindowActive = true
                }
                true
            } else {
                elements.firstOrNull { it.isOpen && it.selected }?.keyTyped(key.name, key.value) ?: let {
                    // If no elements are open and focuseds
                    when (button) {
                        KeyButton.FORWARD -> {
                            val selected = elements.firstOrNull { it.selected }
                            var index = elements.indexOf(selected)
                            if (index == -1) index = 0
                            else if (--index < 0) index = elements.size.minus(1)
                            selected?.selected = false
                            elements[index].selected = true
                            true
                        }
                        KeyButton.BACK -> {
                            val selected = elements.firstOrNull { it.selected }
                            var index = elements.indexOf(selected)
                            if (++index >= elements.size) index = 0
                            selected?.selected = false
                            elements[index].selected = true
                            true
                        }
                        KeyButton.RIGHT, KeyButton.SPACE, KeyButton.ENTER -> {
                            val selected = elements.firstOrNull { it.selected } ?: return false
                            if (selected is CategoryButton) {
                                selected.open()
                                return true
                            } else if (selected is IconElement) {
                                selected.onClickBody(selected.pos, MouseButton.LEFT)
                                return true
                            }
                            false
                        }
                        else -> false
                    }
                }
                false
            }
        } else subGui!!.keyPressed(keyCode, scanCode, modifiers)
    }

    override fun onClose() {
        this.elements.forEach {
            (it as? CategoryButton)?.close()
        }

        this.callbacks.forEach { it(result) }

        if (this.parent == null) {
            super.onClose()
        }
    }

    open lateinit var result: T

    private var callbacks: List<(T) -> Unit> = mutableListOf()

    operator fun plusAssign(callback: (T) -> Unit) {
        this.callbacks += callback
    }

    companion object {
        val animator = Animator()
    }
}

enum class MouseButton {
    LEFT,
    RIGHT,
    MIDDLE,
    BACK,
    FORWARD,
    INVALID,
    SCROLL_UP,
    SCROLL_DOWN;

    companion object {
        fun fromInt(button: Int): MouseButton = values().getOrNull(button.clamp(0, INVALID.ordinal)) ?: INVALID
    }
}

/**
 * Converts keycode to easy to understand controls and
 * groups together similar buttons.
 * @see InputMappings for full keycode list.
 */
enum class KeyButton(val keyCode: Array<Int>) {
    LEFT(arrayOf(263, Client.minecraft.options.keyLeft.key.value)),
    RIGHT(arrayOf(262, Client.minecraft.options.keyRight.key.value)),
    FORWARD(arrayOf(265, Client.minecraft.options.keyUp.key.value)),
    BACK(arrayOf(264, Client.minecraft.options.keyDown.key.value)),
    ESCAPE(arrayOf(256)),
    ENTER(arrayOf(257, 335)),
    ALT(arrayOf(342, 346)),
    CTRL(arrayOf(341, 345)),
    SHIFT(arrayOf(340, 344)),
    SPACE(arrayOf(32)),
    UNKNOWN(arrayOf(-1));

    companion object {
        fun getKeybinds(): Options {
            return Client.minecraft.options
        }

        fun getButton(key: Int): KeyButton {
            return values().firstOrNull { it.keyCode.contains(key) } ?: UNKNOWN
        }
    }
}

operator fun Animation<*>.unaryPlus() {
    CoreGUI.animator += this
}

@DslMarker
annotation class CoreGUIDsl
