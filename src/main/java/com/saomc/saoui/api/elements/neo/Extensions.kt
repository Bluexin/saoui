package com.saomc.saoui.api.elements.neo

import com.saomc.saoui.api.screens.IIcon
import com.saomc.saoui.config.OptionCore
import com.saomc.saoui.neo.screens.NeoGui
import com.saomc.saoui.util.IconCore
import com.teamwizardry.librarianlib.features.animator.Easing
import com.teamwizardry.librarianlib.features.animator.animations.BasicAnimation
import com.teamwizardry.librarianlib.features.math.Vec2d

/**
 * Part of saoui by Bluexin, released under GNU GPLv3.
 *
 * @author Bluexin
 */
@Deprecated("Use NeoCategoryButton instead")
class NeoTLCategoryButtonOld(icon: IIcon, x: Int = 0, y: Int = 0) : NeoIconElement(icon, x, y) {

    init {
        onClick {
            if (elements.isNotEmpty() && !selected) open()
            else close()

            true
        }

        onClickOut {
            if (selected) close()
        }

        val appearAnim = BasicAnimation(this, "y")

        appearAnim.duration = 15f
        appearAnim.from = 0
        appearAnim.repeatCount = 1
        appearAnim.easing = Easing.easeInOutQuint
        NeoGui.animator.add(appearAnim)
    }

    fun open() {
        selected = true

        openAnim = IndexedScheduledCounter(3f, maxIdx = elements.size - 1) {
            if (selected) elements[it].show()
        }
        NeoGui.animator.add(openAnim!!)
    }

    fun close() { // TODO: fancy animation
        elements.forEach {
            it.hide()
            (it as? NeoCategoryButtonOld)?.close() ?: (it as? NeoTLCategoryButtonOld)?.close()
        }
        selected = false
        if (openAnim?.isInAnimator == true) {
            openAnim?.cancel()
            openAnim = null
        }
    }

    override fun plusAssign(element: NeoElement) {
        super.plusAssign(element)
        element.hide()
    }

    private var openAnim: IndexedScheduledCounter? = null
}

@Deprecated("Use NeoCategoryButton instead")
class NeoCategoryButtonOld(icon: IIcon, label: String, width: Int = 84, height: Int = 18, x: Int = 0, y: Int = 0) : NeoIconLabelElement(icon, label, width, height, x, y) {

    init {
        onClick {
            if (elements.isNotEmpty() && !selected) open()
            else close()

            true
        }

        onClickOut {
            if (selected) close()
        }
    }

    fun open() {
        selected = true

        openAnim = IndexedScheduledCounter(3f, maxIdx = elements.size - 1) {
            if (selected) elements[it].show()
        }
        NeoGui.animator.add(openAnim!!)
    }

    fun close() { // TODO: fancy animation
        elements.forEach {
            it.hide()
            (it as? NeoCategoryButtonOld)?.close() ?: (it as? NeoTLCategoryButtonOld)?.close()
        }
        selected = false
        if (openAnim?.isInAnimator == true) {
            openAnim?.cancel()
            openAnim = null
        }
    }

    private var openAnim: IndexedScheduledCounter? = null

    override fun plusAssign(element: NeoElement) {
        super.plusAssign(element)
        element.hide()
    }
}

class NeoCategoryButton(private val delegate: NeoIconElement) : NeoIconElement(delegate.icon, delegate.x, delegate.y) {

    init {
        delegate.onClick {
            if (elements.isNotEmpty() && !selected) open()
            else close()

            true
        }

        delegate.onClickOut {
            if (selected) close()
        }

        if (delegate !is NeoIconLabelElement) { // TL Category
            val appearAnim = BasicAnimation(delegate, "y")

            appearAnim.duration = 15f
            appearAnim.from = 0
            appearAnim.repeatCount = 1
            appearAnim.easing = Easing.easeInOutQuint
            NeoGui.animator.add(appearAnim)
        }
    }

    fun open() {
        selected = true

        openAnim = IndexedScheduledCounter(3f, maxIdx = elements.size - 1) {
            if (selected) elements[it].show()
        }
        NeoGui.animator.add(openAnim!!)
    }

    fun close() { // TODO: fancy animation
        elements.forEach {
            it.hide()
            (it as? NeoCategoryButton)?.close()
        }
        selected = false
        if (openAnim?.isInAnimator == true) {
//            openAnim?.cancel()
//            openAnim = null
        }
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
}

fun optionButton(option: OptionCore): NeoIconLabelElement {
    val but = object : NeoIconLabelElement(IconCore.OPTION, option.displayName) {
        override var selected: Boolean
            get() = option.isEnabled
            set(value) = if (value) option.enable() else option.disable()
    }
    but.onClick {
        option.flip()
        true
    }
    return but
}

fun optionCategory(option: OptionCore): NeoCategoryButton {
    val cat = NeoCategoryButton(NeoIconLabelElement(IconCore.OPTION, option.displayName))
    option.subOptions.forEach {
        cat += if (it.isCategory) optionCategory(it)
        else optionButton(it)
    }
    return cat
}

fun category(icon: IIcon, label: String, body: (NeoCategoryButton.() -> Unit)? = null): NeoCategoryButton {
    val cat = NeoCategoryButton(NeoIconLabelElement(icon, label))
    if (body != null) cat.body()
    return cat
}
