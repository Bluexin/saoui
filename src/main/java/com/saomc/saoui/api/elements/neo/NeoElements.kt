package com.saomc.saoui.api.elements.neo

import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.plus
import com.teamwizardry.librarianlib.features.math.BoundingBox2D
import com.teamwizardry.librarianlib.features.math.Vec2d

/**
 * Part of saoui by Bluexin, released under GNU GPLv3.
 *
 * @author Bluexin
 */
interface NeoElement {
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

    fun hide() = Unit
    fun show() = Unit
}

abstract class NeoParent : NeoElement {
    val elements = mutableListOf<NeoElement>()

    open operator fun plusAssign(element: NeoElement) {
        elements += element
    }

    open operator fun NeoElement.unaryPlus() {
        this@NeoParent += this
    }

    override operator fun contains(pos: Vec2d) = super.contains(pos) ||
            with(pos + vec(childrenXOffset, childrenYOffset)) {
                var npos = this
                elements.filter(NeoElement::visible).any {
                    val r = npos in it
                    npos += vec(childrenXSeparator, childrenYSeparator)
                    r
                }
            }

    open val childrenXOffset = 0
    open val childrenYOffset = 0
    open val childrenXSeparator = 0
    open val childrenYSeparator = 0
}

