package com.saomc.saoui.api.elements.neo

import com.saomc.saoui.api.screens.IIcon
import com.saomc.saoui.config.OptionCore
import com.saomc.saoui.util.IconCore
import com.teamwizardry.librarianlib.features.animator.Animator
import com.teamwizardry.librarianlib.features.animator.Easing
import com.teamwizardry.librarianlib.features.animator.animations.BasicAnimation

/**
 * Part of saoui by Bluexin, released under GNU GPLv3.
 *
 * @author Bluexin
 */
class NeoTLCategoryButton(icon: IIcon, x: Int = 0, y: Int = 0) : NeoIconElement(icon, x, y) {

    init {
        onClick {
            elements.forEach(if (selected) NeoElement::hide else NeoElement::show)
            if (elements.isNotEmpty()) selected = !selected

            true
        }

        onClickOut {
            if (selected) elements.forEach(NeoElement::hide)
            selected = false
        }

        val appearAnim = BasicAnimation(this, "y")

        appearAnim.duration = 15f
        appearAnim.from = 0
        appearAnim.repeatCount = 1
        appearAnim.easing = Easing.easeInOutQuint
        Animator().add(appearAnim)
    }

    override fun plusAssign(element: NeoElement) {
        super.plusAssign(element)
        element.hide()
    }
}

class NeoCategoryButton(icon: IIcon, label: String, width: Int = 84, height: Int = 18, x: Int = 0, y: Int = 0) : NeoIconLabelElement(icon, label, width, height, x, y) {

    init {
        onClick {
            elements.forEach(if (selected) NeoElement::hide else NeoElement::show)
            if (elements.isNotEmpty()) selected = !selected

            true
        }

        onClickOut {
            if (selected) elements.forEach(NeoElement::hide)
            selected = false
        }

        val appearAnim = BasicAnimation(this, "y")

        appearAnim.duration = 15f
        appearAnim.from = 0
        appearAnim.repeatCount = 1
        appearAnim.easing = Easing.easeInOutQuint
        Animator().add(appearAnim)
    }

    override fun plusAssign(element: NeoElement) {
        super.plusAssign(element)
        element.hide()
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
    val cat = NeoCategoryButton(IconCore.OPTION, option.displayName)
    option.subOptions.forEach {
        cat += if (it.isCategory) optionCategory(it)
        else optionButton(it)
    }
    return cat
}

fun category(icon: IIcon, label: String, body: (NeoCategoryButton.() -> Unit)? = null): NeoCategoryButton {
    val cat = NeoCategoryButton(icon, label)
    if (body != null) cat.body()
    return cat
}
