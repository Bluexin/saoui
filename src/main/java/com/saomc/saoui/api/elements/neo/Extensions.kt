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

package com.saomc.saoui.api.elements.neo

import com.saomc.saoui.api.screens.IIcon
import com.saomc.saoui.config.OptionCore
import com.saomc.saoui.neo.screens.MouseButton
import com.saomc.saoui.neo.screens.NeoGuiDsl
import com.saomc.saoui.neo.screens.unaryPlus
import com.saomc.saoui.util.IconCore
import com.teamwizardry.librarianlib.features.animator.Easing
import com.teamwizardry.librarianlib.features.gui.component.supporting.delegate
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.math.Vec2d
import java.lang.ref.WeakReference
import kotlin.math.max
import kotlin.math.min

/**
 * Part of saoui by Bluexin, released under GNU GPLv3.
 *
 * @author Bluexin
 */
class NeoCategoryButton(private val delegate: NeoIconElement, parent: INeoParent? = null, private val init: (NeoCategoryButton.() -> Unit)? = null) : NeoIconElement(delegate.icon, delegate.pos) {

    override var pos by delegate::pos.delegate
    override var destination by delegate::destination.delegate
    override val boundingBox by delegate::boundingBox.delegate
    override var idealBoundingBox by delegate::idealBoundingBox.delegate
    override val elements by delegate::elements.delegate
    override val childrenXOffset by delegate::childrenXOffset.delegate
    override val childrenYOffset by delegate::childrenYOffset.delegate
    override val childrenXSeparator by delegate::childrenXSeparator.delegate
    override val childrenYSeparator by delegate::childrenYSeparator.delegate
    override var visible by delegate::visible.delegate
    override var selected by delegate::selected.delegate
    override var disabled by delegate::disabled.delegate
    override var opacity by delegate::opacity.delegate
    override var scale by delegate::scale.delegate
    override fun onClick(body: (Vec2d, MouseButton) -> Boolean) = delegate.onClick(body)
    override fun onClickOut(body: (Vec2d, MouseButton) -> Unit) = delegate.onClickOut(body)
    override fun hide() = delegate.hide()
    override fun show() = delegate.show()
    override fun draw(mouse: Vec2d, partialTicks: Float) = delegate.draw(mouse, partialTicks)
    override fun contains(pos: Vec2d) = delegate.contains(pos)
    override fun update() {
        super.update()
        delegate.update()
    }

    init {
        this.parent = parent
        delegate.parent = this

        delegate.onClick { _, _ ->
            if (elements.isNotEmpty() && !selected) open()
            else if (selected) close()

            true
        }

        delegate.onClickOut { _, _ ->
            if (selected) close()
        }

        if (delegate !is NeoIconLabelElement) { // TL Category TODO: move to their actual creation
            +basicAnimation(this, "pos") {
                duration = 10f
                from = Vec2d.ZERO
                easing = Easing.easeInOutQuint
            }
        }

        init?.invoke(this)
    }

    fun open(reInit: Boolean = false) {
        selected = true

        if (reInit) elementsSequence.forEach(NeoElement::show) else {
            val children = childrenOrderedForAppearing().toList()
            val anim = IndexedScheduledCounter(3f, maxIdx = children.count() - 1) {
                children.elementAt(it).show()
                @Suppress("NestedLambdaShadowedImplicitParameter")
                if (it == children.count() - 1) elementsSequence.forEach(NeoElement::show)
            }
            +anim
            openAnim = WeakReference(anim)
            tlParent.move(vec(-boundingBox.width(), 0))
        }
    }

    fun close(reInit: Boolean = false) {
        delegate.scroll = -3
        elements.forEach {
            it.hide()
            if (it is NeoCategoryButton && it.selected) {
                it.close()
            }
        }
        selected = false
        if (!reInit) tlParent.move(vec(boundingBox.width(), 0))
        openAnim?.get()?.terminated = true
        openAnim = null
    }

    private var openAnim: WeakReference<IndexedScheduledCounter>? = null

    override operator fun plusAssign(element: NeoElement) {
        delegate += element
        element.hide()
    }

    override fun mouseClicked(pos: Vec2d, mouseButton: MouseButton): Boolean {
        return if ((mouseButton == MouseButton.SCROLL_DOWN || mouseButton == MouseButton.SCROLL_UP) && openAnim?.get()?.finished == false) true else delegate.mouseClicked(pos, mouseButton)
    }

    protected fun childrenOrderedForAppearing(): Sequence<NeoElement> {
        val count = validElementsSequence.count()
        return if (count == 0) emptySequence()
        else {
            val selectedIdx = if (validElementsSequence.any { it is NeoCategoryButton }) validElementsSequence.indexOfFirst { it.selected } else -1
            when {
                selectedIdx >= 0 -> {
                    val skipFront = (selectedIdx - (count / 2 - (count + 1) % 2) + count) % count
                    validElementsSequence.drop(skipFront) + validElementsSequence.take(skipFront)
                }
                count >= 7 -> {
                    val s = validElementsSequence + validElementsSequence
                    s.drop(min(max((scroll + count) % count, 0), count)).take(7)
                }
                else -> validElementsSequence
            }
        }
    }

    @NeoGuiDsl
    fun category(icon: IIcon, label: String, body: (NeoCategoryButton.() -> Unit)? = null): NeoCategoryButton {
        val cat = NeoCategoryButton(NeoIconLabelElement(icon, label), this, body)
        +cat
        return cat
    }

    fun reInit() {
        val wasOpen = this.selected
        if (wasOpen) this.close(true)
        this.elements.clear()
        this.delegate.elements.clear()
        this.init?.invoke(this)
        if (wasOpen) this.open(true)
    }

    override fun toString(): String {
        return "NeoCategoryButton(delegate=$delegate)"
    }
}

fun INeoParent.optionButton(option: OptionCore): NeoIconLabelElement {
    val but = object : NeoIconLabelElement(IconCore.OPTION, option.displayName) {
        override var selected: Boolean
            get() = option.isEnabled
            set(value) = if (value) option.enable() else option.disable()
    }
    but.onClick { _, _ ->
        option.flip()
        true
    }
    but.parent = this
    return but
}

fun INeoParent.optionCategory(option: OptionCore): NeoCategoryButton {
    val cat = NeoCategoryButton(NeoIconLabelElement(IconCore.OPTION, option.displayName))
    option.subOptions.forEach {
        cat += if (it.isCategory) optionCategory(it)
        else optionButton(it)
    }
    cat.parent = this
    return cat
}
