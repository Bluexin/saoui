package com.saomc.saoui.elements.controllers

import com.saomc.saoui.api.elements.IconElement
import com.saomc.saoui.elements.DrawType
import com.saomc.saoui.elements.IElement
import com.saomc.saoui.screens.MouseButton
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.Minecraft
import com.teamwizardry.librarianlib.features.kotlin.minus
import com.teamwizardry.librarianlib.features.math.Vec2d
import org.lwjgl.input.Keyboard
import java.util.*

open class Controller(
        /**
         * This holds the draw, bounding box and all other details
         */
        override val delegate: IElement,
        override var controllingParent: IController,
        final override var init: IController.() -> Unit = {}): IElement by delegate, IController{


    init {
        init.invoke(this)
    }

    /**
     * @see IController.hasOpened
     */
    override var visible: Boolean
        get() = controllingParent.hasOpened
        set(_) = Unit


    /**
     * Queue operations for the controller
     */
    private val futureOperations: MutableList<Controller.() -> Unit>
        get() = LinkedList()

    override fun drawBackground(mouse: Vec2d, partialTicks: Float) {
        delegate.drawBackground(mouse, partialTicks)
        drawChildren(mouse, partialTicks, DrawType.BACKGROUND)
    }

    override fun draw(mouse: Vec2d, partialTicks: Float) {
        delegate.draw(mouse, partialTicks)
        drawChildren(mouse, partialTicks, DrawType.DRAW)
    }

    override fun drawForeground(mouse: Vec2d, partialTicks: Float) {
        delegate.drawForeground(mouse, partialTicks)
        drawChildren(mouse, partialTicks, DrawType.FOREGROUND)
    }

    /**
     * Updates all elements within the list
     * @see IElement.update
     */
    override fun update() {
        delegate.update()
        this.elements.forEach(IElement::update)

        this.futureOperations.forEach { it() }
        this.futureOperations.clear()
    }


    override fun keyTyped(typedChar: Char, keyCode: Int) {
        if (hasOpened)
            elements.firstOrNull { it.selected }?.keyTyped(typedChar, keyCode)
        else {
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
                if (selected is Controller) {
                    selected.open()
                } else if (selected is IconElement) {
                    selected.onClickBody(selected.pos, MouseButton.LEFT)
                }
            } else if (keyCode == Minecraft().gameSettings.keyBindLeft.keyCode || keyCode == Keyboard.KEY_LEFT || keyCode == Minecraft().gameSettings.keyBindAttack.keyCode) {
                close()
            }
        }

    }

    override fun mouseClicked(pos: Vec2d, mouseButton: MouseButton): Boolean {
        if (!hasOpened) {
            @Suppress("NON_EXHAUSTIVE_WHEN")
            when (mouseButton) {
                MouseButton.SCROLL_UP -> {
                    --scroll
                    return true
                }
                MouseButton.SCROLL_DOWN -> {
                    ++scroll
                    return true
                }
            }
        }
        return if (mouseButton == MouseButton.LEFT && pos in this) {
            super<IController>.open()
            true
        }
        else {
            val children = childrenOrderedForRendering() // childrenOrderedForAppearing
            val c = validElementsSequence .take(7).count()
            var npos = pos - this.pos - vec(childrenXOffset, childrenYOffset - (c + c % 2 - 2) * childrenYSeparator / 2.0)
            var ok = false
            children.forEach {
                ok = it.mouseClicked(npos, mouseButton) || ok
                npos -= vec(childrenXSeparator, childrenYSeparator)
            }
            if (ok) true
            else {
                //onClickOutBody(pos, mouseButton)
                false
            }
        }
    }

    /**
     * Adds a task to the controller
     */
    fun performLater(block: Controller.() -> Unit) {
        futureOperations += block
    }
}