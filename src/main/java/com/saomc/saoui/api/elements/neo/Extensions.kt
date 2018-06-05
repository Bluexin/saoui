package com.saomc.saoui.api.elements.neo

import com.saomc.saoui.api.screens.IIcon
import com.saomc.saoui.config.OptionCore
import com.saomc.saoui.neo.screens.NeoGuiDsl
import com.saomc.saoui.neo.screens.unaryPlus
import com.saomc.saoui.util.IconCore
import com.teamwizardry.librarianlib.features.animator.Easing
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.math.BoundingBox2D
import com.teamwizardry.librarianlib.features.math.Vec2d

/**
 * Part of saoui by Bluexin, released under GNU GPLv3.
 *
 * @author Bluexin
 */
class NeoCategoryButton(private val delegate: NeoIconElement, parent: INeoParent? = null, private val init: (NeoCategoryButton.() -> Unit)? = null) : NeoIconElement(delegate.icon, delegate.pos) {

    init {
        this.parent = parent
        delegate.parent = this

        delegate.onClick {
            if (elements.isNotEmpty() && !selected) open()
            else if (selected) close()

            true
        }

        delegate.onClickOut {
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

    override fun contains(pos: Vec2d): Boolean {
        return delegate.contains(pos)
    }

    override var pos: Vec2d
        get() = delegate.pos
        set(value) {
            delegate.pos = value
        }

    override var destination: Vec2d
        get() = delegate.destination
        set(value) {
            delegate.destination = value
        }

    fun open() {
        selected = true

        openAnim = IndexedScheduledCounter(3f, maxIdx = elements.size - 1) {
            if (selected) elements[it].show()
        }
        +openAnim!!
        tlParent.move(vec(-boundingBox.width(), 0))
    }

    override val boundingBox: BoundingBox2D
        get() = delegate.boundingBox

    fun close() {
        elements.forEach {
            it.hide()
            if (it is NeoCategoryButton && it.selected) {
                it.close()
            }
        }
        selected = false
        tlParent.move(vec(boundingBox.width(), 0))
//        if (openAnim?.isInAnimator == true) {
//            openAnim?.cancel()
//            openAnim = null
//        }
    }

    private var openAnim: IndexedScheduledCounter? = null

    override val elements: MutableList<NeoElement>
        get() = delegate.elements
    override val childrenXOffset: Int
        get() = delegate.childrenXOffset
    override val childrenYOffset: Int
        get() = delegate.childrenYOffset
    override val childrenXSeparator: Int
        get() = delegate.childrenXSeparator
    override val childrenYSeparator: Int
        get() = delegate.childrenYSeparator

    override fun draw(mouse: Vec2d, partialTicks: Float) {
        delegate.draw(mouse, partialTicks)
    }

    override operator fun plusAssign(element: NeoElement) {
        delegate += element
        element.hide()
    }

    override fun click(pos: Vec2d): Boolean {
        return delegate.click(pos)
    }

    override var visible: Boolean
        get() = delegate.visible
        set(value) {
            delegate.visible = value
        }
    override var selected: Boolean
        get() = delegate.selected
        set(value) {
            delegate.selected = value
        }
    override var disabled: Boolean
        get() = delegate.disabled
        set(value) {
            delegate.selected = value
        }
    override var opacity: Float
        get() = delegate.opacity
        set(value) {
            delegate.opacity = value
        }
    override var scale: Vec2d
        get() = delegate.scale
        set(value) {
            delegate.scale = value
        }

    override fun onClick(body: (Vec2d) -> Boolean) {
        delegate.onClick(body)
    }

    override fun onClickOut(body: (Vec2d) -> Unit) {
        delegate.onClickOut(body)
    }

    override fun hide() {
        delegate.hide()
    }

    override fun show() {
        delegate.show()
    }

    @NeoGuiDsl
    fun category(icon: IIcon, label: String, body: (NeoCategoryButton.() -> Unit)? = null): NeoCategoryButton {
        val cat = NeoCategoryButton(NeoIconLabelElement(icon, label), this, body)
        +cat
        return cat
    }

    fun init() {
        this.init?.invoke(this)
    }
}

fun INeoParent.optionButton(option: OptionCore): NeoIconLabelElement {
    val but = object : NeoIconLabelElement(IconCore.OPTION, option.displayName) {
        override var selected: Boolean
            get() = option.isEnabled
            set(value) = if (value) option.enable() else option.disable()
    }
    but.onClick {
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
