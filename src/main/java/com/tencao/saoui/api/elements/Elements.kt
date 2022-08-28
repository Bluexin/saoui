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

package com.tencao.saoui.api.elements

import com.mojang.blaze3d.matrix.MatrixStack
import com.tencao.saomclib.utils.math.BoundingBox2D
import com.tencao.saomclib.utils.math.Vec2d
import com.tencao.saomclib.utils.math.vec
import com.tencao.saoui.SAOCore
import com.tencao.saoui.api.elements.animator.Easing
import com.tencao.saoui.screens.CoreGUI
import com.tencao.saoui.screens.MouseButton
import com.tencao.saoui.screens.unaryPlus

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
    val controllingParent: INeoParent?
        get() {
            return if (parent is CoreGUI<*>) {
                parent
            } else if (parent is CategoryButton) {
                if (parent!!.parent is CoreGUI<*>) {
                    parent!!.parent
                } else parent!!.parent?.parent
            } else parent?.parent
        }
    val controllingGUI: CoreGUI<*>?
        get() {
            return tlParent as? CoreGUI<*>
        }
    var pos: Vec2d
    var destination: Vec2d
    var scroll
        get() = 0
        set(_) = Unit
    var isOpen: Boolean
    fun move(delta: Vec2d) {
        try {
            CoreGUI.animator.removeAnimationsFor(this)
        } catch (e: ConcurrentModificationException) {
            SAOCore.LOGGER.fatal("Element caused a concurrent modification exception on animation")
            e.printStackTrace()
        }
        destination += delta
        +basicAnimation(this, "pos") {
            to = destination
            duration = 10f
            easing = Easing.easeInOutQuint
        }
    }

    fun mouseClicked(pos: Vec2d, mouseButton: MouseButton) = false
    fun keyTyped(typedChar: String, keyCode: Int) {}
    fun isFocus(): Boolean {
        return isOpen || parent?.isOpen == true || (controllingParent as? CategoryButton)?.elements?.none { it.isOpen } ?: (controllingParent as? CoreGUI<*>)?.elements?.none { it.isOpen } ?: parent?.isOpen ?: true
    }
}

interface NeoElement : INeoParent {

    val boundingBox: BoundingBox2D
    var idealBoundingBox
        get() = boundingBox

        /**
         * The set method is used to apply changes. Not ideal but idk between this and [boundingBox]
         */
        set(_) {}
    val listed
        get() = true
    val visible
        get() = true
    var valid
        get() = true
        set(_) = Unit
    var selected
        get() = false
        set(_) = Unit
    var highlighted
        get() = false
        set(_) = Unit
    val disabled
        get() = false
    var opacity
        get() = 1f
        set(_) = Unit
    val scale
        get() = Vec2d(1.0, 1.0)
    val canDraw: Boolean
        get() = opacity >= 0.03 || scale != Vec2d.ZERO

    fun drawBackground(mouse: Vec2d, partialTicks: Float, matrixStack: MatrixStack)
    fun draw(mouse: Vec2d, partialTicks: Float, matrixStack: MatrixStack)
    fun drawForeground(mouse: Vec2d, partialTicks: Float, matrixStack: MatrixStack)
    operator fun contains(pos: Vec2d) = pos in boundingBox
    fun init() {}
    fun hide() = Unit
    fun show() = Unit
    fun update() = Unit
}

interface NeoParent : NeoElement {

    override var isOpen: Boolean
    override var selected: Boolean
    open val elements: MutableList<NeoElement>
    val elementsSequence
        get() = elements.asSequence()
    val otherElementsSequence
        get() = elementsSequence.filter { !it.listed && it.visible }
    val listedElementsSequence
        get() = elementsSequence.filter(NeoElement::listed)
    val validElementsSequence
        get() = listedElementsSequence.filter(NeoElement::valid)
    val visibleElementsSequence
        get() = validElementsSequence.filter(NeoElement::visible)

    val childrenXOffset
        get() = 0
    val childrenYOffset
        get() = 0
    val childrenXSeparator
        get() = 0
    val childrenYSeparator
        get() = 0
    override var parent: INeoParent?
    val futureOperations: MutableList<NeoParent.() -> Unit>
    override fun update() {
        super.update()

        this.elementsSequence.forEach(NeoElement::update)

        this.futureOperations.forEach { it() }
        this.futureOperations.clear()
    }

    operator fun plusAssign(element: NeoElement) {
        if (elementsSequence.none { it == element }) {
            if (elements.isNotEmpty()) {
                val bb1 = elements[0].idealBoundingBox
                val bbNew = element.idealBoundingBox
                if (bb1.widthI() >= bbNew.widthI()) {
                    element.idealBoundingBox = BoundingBox2D(bbNew.min, vec(bb1.width(), bbNew.height()))
                } else {
                    elementsSequence.forEach {
                        val bb = it.idealBoundingBox
                        it.idealBoundingBox = BoundingBox2D(bb.min, vec(bbNew.width(), bb.height()))
                    }
                    element.idealBoundingBox = bbNew
                }
            } else element.idealBoundingBox = element.idealBoundingBox
            elements += element

            element.parent = this
        }
    }

    operator fun NeoElement.unaryPlus() {
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
    fun performLater(block: NeoParent.() -> Unit) {
        futureOperations += block
    }
}
