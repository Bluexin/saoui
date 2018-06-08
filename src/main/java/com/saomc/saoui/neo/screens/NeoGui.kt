package com.saomc.saoui.neo.screens

import com.saomc.saoui.api.elements.neo.*
import com.saomc.saoui.api.screens.IIcon
import com.saomc.saoui.config.OptionCore
import com.teamwizardry.librarianlib.features.animator.Animation
import com.teamwizardry.librarianlib.features.animator.Animator
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.minus
import com.teamwizardry.librarianlib.features.math.Vec2d
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.settings.KeyBinding
import org.lwjgl.input.Keyboard
import org.lwjgl.input.Mouse

/**
 * Part of saoui by Bluexin, released under GNU GPLv3.
 *
 * @author Bluexin
 */
abstract class NeoGui<T : Any>(override var pos: Vec2d, override var destination: Vec2d = pos) : GuiScreen(), INeoParent {
    protected val elements = mutableListOf<NeoElement>()

    protected var subGui: NeoGui<*>? = null

    override var parent: INeoParent? = null

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        GlStateManager.pushMatrix()
        GlStateManager.translate(pos.x, pos.y, 0.0)

        val mousePos = vec(mouseX, mouseY) - pos
        elements.forEach { it.draw(mousePos, partialTicks) }

        GlStateManager.popMatrix()

        subGui?.drawScreen(mouseX, mouseY, partialTicks)
    }

    override fun updateScreen() {
        subGui?.updateScreen() ?: elements.forEach(NeoElement::update)
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        val mouse = vec(mouseX, mouseY) - pos
        subGui?.mouseClicked(mouseX, mouseY, mouseButton)
                ?: elements.forEach { it.click(mouse, MouseButton.fromInt(mouseButton)) }
    }

    override fun doesGuiPauseGame() = OptionCore.GUI_PAUSE.isEnabled

    @NeoGuiDsl
    fun tlCategory(icon: IIcon, body: (NeoCategoryButton.() -> Unit)? = null) {
        this.elements += NeoCategoryButton(NeoIconElement(icon, vec(0, 25 * elements.size)), this, body)
    }

    operator fun NeoElement.unaryPlus() {
        this@NeoGui.elements += this
    }

    fun <R : Any> openGui(gui: NeoGui<R>): NeoGui<R> {
        if (subGui != null) throw IllegalStateException("Already opened a sub gui of type ${subGui!!::class.qualifiedName}")
        subGui = gui
        gui.parent = this
        KeyBinding.unPressAllKeys()

        while (Mouse.next()) {
        }

        while (Keyboard.next()) {
        }

        val sr = ScaledResolution(mc)
        gui.setWorldAndResolution(mc, sr.scaledWidth, sr.scaledHeight)

        gui += { subGui = null }

        return gui
    }

    override fun setWorldAndResolution(mc: Minecraft, width: Int, height: Int) {
        super.setWorldAndResolution(mc, width, height)
        subGui?.setWorldAndResolution(mc, width, height)
    }

    override fun keyTyped(typedChar: Char, keyCode: Int) {
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
    INVALID;

    companion object {
        fun fromInt(button: Int): MouseButton = values().getOrNull(button) ?: INVALID
    }
}

operator fun Animation<*>.unaryPlus() {
    NeoGui.animator += this
}

@DslMarker
annotation class NeoGuiDsl
