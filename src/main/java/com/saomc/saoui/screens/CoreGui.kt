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

package com.saomc.saoui.screens

import com.saomc.saoui.GLCore
import com.saomc.saoui.api.elements.neo.*
import com.saomc.saoui.api.screens.IIcon
import com.saomc.saoui.config.OptionCore
import com.teamwizardry.librarianlib.features.animator.Animation
import com.teamwizardry.librarianlib.features.animator.Animator
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.clamp
import com.teamwizardry.librarianlib.features.kotlin.minus
import com.teamwizardry.librarianlib.features.math.Vec2d
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.settings.KeyBinding
import org.lwjgl.input.Keyboard
import org.lwjgl.input.Mouse

/**
 * Part of saoui by Bluexin, released under GNU GPLv3.
 *
 * @author Bluexin
 */
abstract class CoreGUI<T : Any>(override var pos: Vec2d, override var destination: Vec2d = pos) : GuiScreen(), INeoParent {
    protected val elements = mutableListOf<NeoElement>()

    protected var subGui: CoreGUI<*>? = null

    override var parent: INeoParent? = null

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        GLCore.glBlend(true)
        GLCore.pushMatrix()
        GLCore.translate(pos.x, pos.y, 0.0)

        val mousePos = if (subGui == null) vec(mouseX, mouseY) - pos else Vec2d.NEG_INFINITY
        elements.forEach { it.draw(mousePos, partialTicks) }

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
        return subGui?.mouseClicked(pos, mouseButton) ?: elements.filter { it.mouseClicked(pos - this.pos, mouseButton) }.any()
    }

    override fun doesGuiPauseGame() = OptionCore.GUI_PAUSE.isEnabled

    @CoreGUIDsl
    fun tlCategory(icon: IIcon, body: (NeoCategoryButton.() -> Unit)? = null) {
        this.elements += NeoCategoryButton(NeoIconElement(icon, vec(0, 25 * elements.size)), this, body)
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
            if (keyCode == 1) {
                this.onGuiClosed()
                if (this.parent == null) {
                    mc.displayGuiScreen(null)
                    if (this.mc.currentScreen == null) this.mc.setIngameFocus()
                }
            }
        } else subGui?.keyTyped(typedChar, keyCode)
    }

    override fun onGuiClosed() {
        super.onGuiClosed()

        this.elements.forEach {
            (it as? NeoCategoryButton)?.close()
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
