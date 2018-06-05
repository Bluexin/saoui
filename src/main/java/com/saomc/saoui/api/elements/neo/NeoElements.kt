package com.saomc.saoui.api.elements.neo

import com.saomc.saoui.neo.screens.NeoGui
import com.saomc.saoui.neo.screens.unaryPlus
import com.teamwizardry.librarianlib.features.animator.Easing
import com.teamwizardry.librarianlib.features.kotlin.plus
import com.teamwizardry.librarianlib.features.math.BoundingBox2D
import com.teamwizardry.librarianlib.features.math.Vec2d
import java.util.*

/**
 * Part of saoui by Bluexin, released under GNU GPLv3.
 *
 * @author Bluexin
 */
interface INeoParent {
    var parent: INeoParent?
        get() = null
        set(value) {}

    val tlParent: INeoParent
        get() = parent?.tlParent ?: this

    fun move(delta: Vec2d) {
        NeoGui.animator.removeAnimationsFor(this)
        destination += delta
        +basicAnimation(this, "pos") {
            to = destination
            duration = 10f
            easing = Easing.easeInOutQuint
        }
    }

    var pos: Vec2d

    var destination: Vec2d
}

interface NeoElement : INeoParent {
    fun draw(mouse: Vec2d, partialTicks: Float)

    val boundingBox: BoundingBox2D

    operator fun contains(pos: Vec2d) = pos in boundingBox

    fun click(pos: Vec2d) = false

    val visible
        get() = true

    val selected
        get() = false

    val disabled
        get() = false

    val opacity
        get() = 1f

    val scale
        get() = Vec2d.ONE

    fun hide() = Unit
    fun show() = Unit

    fun update() = Unit
}

abstract class NeoParent : NeoElement {
    open val elements = mutableListOf<NeoElement>()

    open val elementsSequence by lazy { elements.asSequence() }

    open operator fun plusAssign(element: NeoElement) {
        elements += element
    }

    open operator fun NeoElement.unaryPlus() {
        this@NeoParent += this
    }

    override operator fun contains(pos: Vec2d) = super.contains(pos) /*||
            with(pos + vec(childrenXOffset, childrenYOffset)) {
                var npos = this
                elementsSequence.filter(NeoElement::visible).any {
                    val r = npos in it
                    npos += vec(childrenXSeparator, childrenYSeparator)
                    r
                }
            }*/

    open val childrenXOffset = 0
    open val childrenYOffset = 0
    open val childrenXSeparator = 0
    open val childrenYSeparator = 0

    override var parent: INeoParent? = null

    private val futureOperations: MutableList<NeoParent.() -> Unit> = LinkedList()

    fun performLater(block: NeoParent.() -> Unit) {
        futureOperations += block
    }
}
