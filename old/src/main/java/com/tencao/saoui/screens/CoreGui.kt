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

import com.tencao.saomclib.Client
import com.tencao.saomclib.utils.math.Vec2d
import com.tencao.saomclib.utils.math.clamp
import com.tencao.saomclib.utils.math.vec
import com.tencao.saoui.GLCore
import com.tencao.saoui.api.elements.*
import com.tencao.saoui.api.elements.animator.Animation
import com.tencao.saoui.api.elements.animator.Animator
import com.tencao.saoui.api.screens.IIcon
import com.tencao.saoui.config.OptionCore
import com.tencao.saoui.screens.util.Popup
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.settings.KeyBinding
import net.minecraft.util.math.Vec2f
import org.lwjgl.input.Keyboard
import org.lwjgl.input.Mouse

/**
 * Part of saoui by Bluexin, released under GNU GPLv3.
 *
 * @author Bluexin
 */
abstract class CoreGUI<T : Any>(final override var pos: Vec2d, override var destination: Vec2d = pos, val elements: MutableList<NeoElement> = mutableListOf()) : GuiScreen(), INeoParent {

    var viewSet: Boolean = false
    var playerView: Vec2f = Vec2f.ZERO
    open var previousMouse: Vec2d = Vec2d(0.0, 0.0)

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

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        GLCore.glBlend(true)
        GLCore.pushMatrix()

        if (OptionCore.UI_MOVEMENT.isEnabled) {
            if (!viewSet) {
                playerView = Vec2f(Client.minecraft.player.rotationYaw, Client.minecraft.player.rotationPitch)
                previousMouse = Vec2d(mouseX.toDouble(), mouseY.toDouble())
                viewSet = true
            } else {
                Client.minecraft.player.rotationYaw = playerView.x - ((width / 2 - mouseX) / 2) * 0.25F
                Client.minecraft.player.rotationPitch = playerView.y - ((height / 2 - mouseY) / 2) * 0.25F
                pos = pos.add((mouseX - previousMouse.x) * 0.25, (mouseY - previousMouse.y) * 0.25)
                previousMouse = Vec2d(mouseX.toDouble(), mouseY.toDouble())
            }
        } else if (viewSet) {
            Client.minecraft.player.rotationYaw = playerView.x
            Client.minecraft.player.rotationPitch = playerView.y
        }
        GLCore.translate(pos.x, pos.y, 0.0)

        val mousePos = if (subGui == null) vec(mouseX, mouseY) - pos else Vec2d.NEG_INFINITY
        elements.forEach { it.drawBackground(mousePos, partialTicks) }
        elements.forEach { it.draw(mousePos, partialTicks) }
        elements.forEach { it.drawForeground(mousePos, partialTicks) }

        // GLCore.lighting(false)

        GLCore.popMatrix()

        subGui?.drawScreen(mouseX, mouseY, partialTicks)
    }

    override fun updateScreen() {
        subGui?.updateScreen() ?: elements.forEach(NeoElement::update)
    }

    override fun handleMouseInput() {
        super.handleMouseInput()

        val i = Mouse.getEventDWheel()

        val x = Mouse.getEventX() * this.width / this.mc.displayWidth
        val y = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1

        if (i > 0) this.mouseClicked(vec(x, y), MouseButton.SCROLL_UP)
        if (i < 0) this.mouseClicked(vec(x, y), MouseButton.SCROLL_DOWN)
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        this.mouseClicked(vec(mouseX, mouseY), MouseButton.fromInt(mouseButton))
    }

    override fun mouseClicked(pos: Vec2d, mouseButton: MouseButton): Boolean {
        return subGui?.mouseClicked(pos, mouseButton) ?: elements.fold(false) { acc, it ->
            it.mouseClicked(pos - this.pos, mouseButton) || acc
        }
    }

    override fun mouseClickMove(mouseX: Int, mouseY: Int, clickedMouseButton: Int, timeSinceLastClick: Long) {
        subGui?.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick)
    }

    override fun mouseReleased(mouseX: Int, mouseY: Int, state: Int) {
        subGui?.mouseReleased(mouseX, mouseY, state)
    }

    override fun doesGuiPauseGame() = OptionCore.GUI_PAUSE.isEnabled

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
        KeyBinding.unPressAllKeys()

        while (Mouse.next());
        while (Keyboard.next());

        val sr = ScaledResolution(mc)
        gui.setWorldAndResolution(mc, sr.scaledWidth, sr.scaledHeight)

        gui += { subGui = null }

        return gui
    }

    override fun setWorldAndResolution(mc: Minecraft, width: Int, height: Int) {
        super.setWorldAndResolution(mc, width, height)
        subGui?.setWorldAndResolution(mc, width, height)
    }

    override fun keyTyped(typedChar: Char, keyCode: Int) { // TODO: keyboard nav (should also support controller)
        if (subGui == null) {
            if (keyCode == Keyboard.KEY_ESCAPE) {
                this.onGuiClosed()
                if (this.parent == null) {
                    mc.displayGuiScreen(null)
                    if (this.mc.currentScreen == null) this.mc.setIngameFocus()
                }
            } else {
                elements.firstOrNull { it.isOpen && it.selected }?.keyTyped(typedChar, keyCode) ?: let {
                    // If no elements are open and focuseds
                    if (keyCode == Client.minecraft.gameSettings.keyBindForward.keyCode || keyCode == Keyboard.KEY_UP) {
                        val selected = elements.firstOrNull { it.selected }
                        var index = elements.indexOf(selected)
                        if (index == -1) index = 0
                        else if (--index < 0) index = elements.size.minus(1)
                        selected?.selected = false
                        elements[index].selected = true
                    } else if (keyCode == Client.minecraft.gameSettings.keyBindBack.keyCode || keyCode == Keyboard.KEY_DOWN) {
                        val selected = elements.firstOrNull { it.selected }
                        var index = elements.indexOf(selected)
                        if (++index >= elements.size) index = 0
                        selected?.selected = false
                        elements[index].selected = true
                    } else if (keyCode == Client.minecraft.gameSettings.keyBindRight.keyCode || keyCode == Keyboard.KEY_RIGHT || keyCode == Client.minecraft.gameSettings.keyBindJump.keyCode || keyCode == Keyboard.KEY_RETURN) {
                        val selected = elements.firstOrNull { it.selected } ?: return
                        if (selected is CategoryButton) {
                            selected.open()
                        } else if (selected is IconElement) {
                            selected.onClickBody(selected.pos, MouseButton.LEFT)
                        }
                    }
                }
            }
        } else subGui?.keyTyped(typedChar, keyCode)
    }

    override fun onGuiClosed() {
        super.onGuiClosed()

        this.elements.forEach {
            (it as? CategoryButton)?.close()
        }

        this.callbacks.forEach { it(result) }
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

operator fun Animation<*>.unaryPlus() {
    CoreGUI.animator += this
}

@DslMarker
annotation class CoreGUIDsl
