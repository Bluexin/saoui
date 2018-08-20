package com.saomc.saoui.api.elements.neo

import com.saomc.saoui.neo.screens.MouseButton
import com.saomc.saoui.neo.screens.NeoGui
import com.saomc.saoui.neo.screens.unaryPlus
import com.teamwizardry.librarianlib.features.animator.Easing
import com.teamwizardry.librarianlib.features.helpers.vec
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
        set(_) = Unit

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

    var scroll
        get() = 0
        set(_) = Unit

    fun mouseClicked(pos: Vec2d, mouseButton: MouseButton) = false
}

interface NeoElement : INeoParent {
    fun draw(mouse: Vec2d, partialTicks: Float)

    val boundingBox: BoundingBox2D

    var idealBoundingBox
        get() = boundingBox
        /**
         * The set method is used to apply changes. Not ideal but idk between this and [boundingBox]
         */
        set(_) {}

    operator fun contains(pos: Vec2d) = pos in boundingBox

    val visible
        get() = true

    val valid
        get() = true

    val selected
        get() = false

    val disabled
        get() = false

    var opacity
        get() = 1f
        set(_) = Unit

    val scale
        get() = Vec2d.ONE

    fun hide() = Unit
    fun show() = Unit

    fun update() = Unit
}

abstract class NeoParent : NeoElement {

    override var scroll = -3
        set(value) {
            val c = validElementsSequence.count()
            if (c > 6) field = (value /*+ c*/) % (c)
//            SAOCore.LOGGER.info("Result: $field (tried $value)")
        }

    open val elements = mutableListOf<NeoElement>()

    open val elementsSequence by lazy { elements.asSequence() }

    open val validElementsSequence by lazy { elementsSequence.filter(NeoElement::valid) }

    open val visibleElementsSequence by lazy { validElementsSequence.filter(NeoElement::visible) }

    open operator fun plusAssign(element: NeoElement) {
        if (elements.isNotEmpty()) {
            val bb1 = elements[0].idealBoundingBox
            val bbNew = element.idealBoundingBox
            if (bb1.widthI() >= bbNew.widthI()) {
                element.idealBoundingBox = BoundingBox2D(bbNew.min, vec(bb1.width(), bbNew.height()))
            } else {
                elements.forEach {
                    val bb = it.idealBoundingBox
                    it.idealBoundingBox = BoundingBox2D(bb.min, vec(bbNew.width(), bb.height()))
                }
                element.idealBoundingBox = bbNew
            }
        }
        elements += element
        element.parent = this
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
