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
}

abstract class NeoParent : NeoElement {
    val elements = mutableListOf<NeoElement>()

    operator fun plusAssign(element: NeoElement) {
        elements += element
    }

    operator fun NeoElement.unaryPlus() {
        this@NeoParent += this
    }

    override operator fun contains(pos: Vec2d) = super.contains(pos) || with(pos + vec(childrenXOffset, childrenYOffset)) { elements.any { this in it } }

    open val childrenXOffset = 0
    open val childrenYOffset = 0
}

