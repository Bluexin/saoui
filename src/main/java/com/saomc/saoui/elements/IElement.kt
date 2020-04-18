package com.saomc.saoui.elements

import com.saomc.saoui.api.elements.basicAnimation
import com.saomc.saoui.elements.controllers.Controller
import com.saomc.saoui.elements.controllers.IController
import com.saomc.saoui.screens.CoreGUI
import com.saomc.saoui.screens.MouseButton
import com.saomc.saoui.screens.unaryPlus
import com.teamwizardry.librarianlib.features.animator.Easing
import com.teamwizardry.librarianlib.features.kotlin.plus
import com.teamwizardry.librarianlib.features.math.BoundingBox2D
import com.teamwizardry.librarianlib.features.math.Vec2d

/**
 * The core part of the element
 */
interface IElement {

    var function: () -> Unit
        get() = {}
        set(_) = Unit

    /**
     * The area defining the element. Used for both spacing and
     * mouse checks.
     */
    var boundingBox: BoundingBox2D
        get() = BoundingBox2D(Vec2d.ZERO, Vec2d.ZERO)
        set(_) = Unit

    var scroll: Int
        get() = 0
        set(_) = Unit

    operator fun contains(pos: Vec2d) = pos in boundingBox

    /**
     * Draws the background layer of this element (behind the items).
     */
    fun drawBackground(mouse: Vec2d, partialTicks: Float)

    /**
     * Draw calls every render tick, used mostly for icons and general function renders.
     */
    fun draw(mouse: Vec2d, partialTicks: Float)

    /**
     * Draw the foreground layer for this element (everything in front of the items)
     */
    fun drawForeground(mouse: Vec2d, partialTicks: Float)

    fun move(delta: Vec2d) {
        CoreGUI.animator.removeAnimationsFor(this)
        destination += delta
        +basicAnimation(this, "pos") {
            to = destination
            duration = 10f
            easing = Easing.easeInOutQuint
        }
    }

    /**
     * Tells the element to hide itself from rendering
     */
    fun hide(){
        visible = false
    }

    /**
     * Tells the element to show itself from rendering
     */
    fun show(){
        visible = false
    }

    /**
     * The position of the element.
     */
    var pos: Vec2d
        get() = Vec2d.ZERO
        set(_) = Unit

    /**
     * The destination for animations.
     */
    var destination: Vec2d
        get() = pos
        set(_) = Unit

    /**
     * Check if mouse has clicked this element, returns true if yes.
     */
    fun mouseClicked(pos: Vec2d, mouseButton: MouseButton) = pos in this

    /**
     * Used to capture keyboard inputs.
     */
    fun keyTyped(typedChar: Char, keyCode: Int){}

    /**
     * This will fire the main function of the element.
     */
    fun open(){
        function.invoke()
    }

    /**
     * This will close the element.
     */
    fun close() {
        controllingParent.close()
    }

    /**
     * This is the controller for this element.
     */
    var controllingParent: IController

    /**
     * If this element should be listed with other elements.
     */
    val listed
        get() = true

    /**
     * If this element should be rendered or hidden.
     */
    var visible: Boolean
        get() = true
        set(_) = Unit

    /**
     * Check if the element is valid and ready to be called.
     */
    var valid
        get() = true
        set(_) = Unit

    /**
     * Check if the cursor or keyboard has selected this element, or
     * if the element is opened.
     */
    var selected
        get() = false
        set(_) = Unit

    /**
     * Check if cursor is over the selected element or if the function is
     * enabled. (Down to implementation)
     */
    var highlighted
        get() = false
        set(_) = Unit

    /**
     * If this element should be disabled.
     *
     * *NOTE* This will not prevent rendering of the element, just
     * the functions.
     */
    val disabled
        get() = false

    /**
     * Set the render transparency.
     */
    var opacity
        get() = 1f
        set(_) = Unit

    /**
     * Set the size of the element.
     */
    val scale
        get() = Vec2d.ONE

    open fun onOpen(body: () -> Unit): IElement {
        function = body
        return this
    }

    /**
     * Update calls every tick.
     */
    fun update() = Unit

    fun isFocus() = selected || !controllingParent.elementsSequence.any { it != this && it is Controller && it.hasOpened}
}