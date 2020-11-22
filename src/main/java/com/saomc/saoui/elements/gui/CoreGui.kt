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

package com.saomc.saoui.elements.gui

import com.saomc.saoui.GLCore
import com.saomc.saoui.api.elements.basicAnimation
import com.saomc.saoui.api.elements.plusAssign
import com.saomc.saoui.api.screens.IIcon
import com.saomc.saoui.config.OptionCore
import com.saomc.saoui.elements.IElement
import com.saomc.saoui.elements.IconElement
import com.teamwizardry.librarianlib.features.animator.Animation
import com.teamwizardry.librarianlib.features.animator.Animator
import com.teamwizardry.librarianlib.features.animator.Easing
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.Minecraft
import com.teamwizardry.librarianlib.features.kotlin.clamp
import com.teamwizardry.librarianlib.features.kotlin.minus
import com.teamwizardry.librarianlib.features.kotlin.plus
import com.teamwizardry.librarianlib.features.math.Vec2d
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
abstract class CoreGUI<T : Any>(val parentGUI: GuiScreen? = null) : GuiScreen() {

    var viewSet: Boolean = false
    var playerView: Vec2f = Vec2f.ZERO
    open var previousMouse: Vec2d = Vec2d(0.0, 0.0)
    var pos: Vec2d = Vec2d.ZERO
    var destination: Vec2d = pos
    open var originPos = Vec2d.ZERO
    val elements: MutableList<IElement> = mutableListOf()

    var subGui: CoreGUI<*>? = null
        private set

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        GLCore.glBlend(true)
        GLCore.pushMatrix()

        if (OptionCore.UI_MOVEMENT.isEnabled) {
            if (!viewSet) {
                playerView = Vec2f(Minecraft().player.rotationYaw, Minecraft().player.rotationPitch)
                previousMouse = Vec2d(mouseX.toDouble(), mouseY.toDouble())
                viewSet = true
            } else {
                Minecraft().player.rotationYaw = playerView.x - ((width / 2 - mouseX) / 2) * 0.25F
                Minecraft().player.rotationPitch = playerView.y - ((height / 2 - mouseY) / 2) * 0.25F
                pos = pos.add((mouseX - previousMouse.x) * 0.25, (mouseY - previousMouse.y) * 0.25)
                previousMouse = Vec2d(mouseX.toDouble(), mouseY.toDouble())
            }
        } else if (viewSet) {
            Minecraft().player.rotationYaw = playerView.x
            Minecraft().player.rotationPitch = playerView.y
            pos = originPos
        }
        GLCore.translate(pos.x, pos.y, 0.0)

        val mousePos = if (subGui == null) vec(mouseX, mouseY) - pos else Vec2d.NEG_INFINITY
        elements.forEach { it.drawBackground(mousePos, partialTicks) }
        elements.forEach { it.draw(mousePos, partialTicks) }
        elements.forEach { it.drawForeground(mousePos, partialTicks) }

        //GLCore.lighting(false)

        GLCore.popMatrix()

        subGui?.drawScreen(mouseX, mouseY, partialTicks)
    }

    override fun updateScreen() {
        subGui?.updateScreen() ?: elements.forEach(IElement::update)
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

    fun mouseClicked(pos: Vec2d, mouseButton: MouseButton): Boolean {
        return subGui?.mouseClicked(pos, mouseButton)
                ?: elements.filter { it.mouseClicked(pos - this.pos, mouseButton) }.any()
    }

    override fun mouseClickMove(mouseX: Int, mouseY: Int, clickedMouseButton: Int, timeSinceLastClick: Long) {
        subGui?.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick)
    }

    override fun mouseReleased(mouseX: Int, mouseY: Int, state: Int) {
        subGui?.mouseReleased(mouseX, mouseY, state)
    }

    override fun doesGuiPauseGame() = OptionCore.GUI_PAUSE.isEnabled

    @CoreGUIDsl
    fun tlCategory(icon: IIcon, index: Int, body: (IElement.() -> Unit)? = null): IElement {
        val cat = IconElement(icon, null, vec(0, 25 * index), init = body)
        +cat
        return cat
    }

    operator fun IElement.unaryPlus() {
        this@CoreGUI.elements += this
    }

    fun <R : Any> openGui(gui: CoreGUI<R>): CoreGUI<R> {
        check(subGui == null) { "Already opened a sub gui of type ${subGui!!::class.qualifiedName}" }
        subGui = gui
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
                if (this.parentGUI == null) {
                    mc.displayGuiScreen(null)
                    if (this.mc.currentScreen == null) this.mc.setIngameFocus()
                }
            } else {
                elements.firstOrNull { it.hasOpened && it.selected }?.keyTyped(typedChar, keyCode) ?: let {
                    // If no elements are open and focuseds
                    if (keyCode == Minecraft().gameSettings.keyBindForward.keyCode || keyCode == Keyboard.KEY_UP) {
                        val selected = elements.firstOrNull { it.selected }
                        var index = elements.indexOf(selected)
                        if (index == -1) index = 0
                        else if (--index < 0) index = elements.size.minus(1)
                        selected?.selected = false
                        elements[index].selected = true
                    } else if (keyCode == Minecraft().gameSettings.keyBindBack.keyCode || keyCode == Keyboard.KEY_DOWN) {
                        val selected = elements.firstOrNull { it.selected }
                        var index = elements.indexOf(selected)
                        if (++index >= elements.size) index = 0
                        selected?.selected = false
                        elements[index].selected = true
                    } else if (keyCode == Minecraft().gameSettings.keyBindRight.keyCode || keyCode == Keyboard.KEY_RIGHT || keyCode == Minecraft().gameSettings.keyBindJump.keyCode || keyCode == Keyboard.KEY_RETURN) {
                        val selected = elements.firstOrNull { it.selected } ?: return
                        selected.onClickBody(selected.pos, MouseButton.LEFT)

                    }
                }
            }
        } else subGui?.keyTyped(typedChar, keyCode)
    }

    override fun onGuiClosed() {
        super.onGuiClosed()

        this.elements.forEach {
            it.close()
        }

        this.callbacks.forEach { it(result) }
    }

    fun move(delta: Vec2d) {
        animator.removeAnimationsFor(this)
        destination += delta
        +basicAnimation(this, "pos") {
            to = destination
            duration = 10f
            easing = Easing.easeInOutQuint
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

operator fun Animation<*>.unaryPlus() {
    CoreGUI.animator += this
}

@DslMarker
annotation class CoreGUIDsl
