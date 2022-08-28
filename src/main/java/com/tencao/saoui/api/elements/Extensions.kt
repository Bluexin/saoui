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
import com.tencao.saomclib.Client
import com.tencao.saomclib.utils.math.BoundingBox2D
import com.tencao.saomclib.utils.math.Vec2d
import com.tencao.saomclib.utils.math.vec
import com.tencao.saoui.SAOCore
import com.tencao.saoui.SoundCore
import com.tencao.saoui.api.screens.IIcon
import com.tencao.saoui.config.OptionCategory
import com.tencao.saoui.config.OptionCore
import com.tencao.saoui.play
import com.tencao.saoui.screens.CoreGUI
import com.tencao.saoui.screens.KeyButton
import com.tencao.saoui.screens.MouseButton
import com.tencao.saoui.screens.menus.IngameMenu
import com.tencao.saoui.screens.unaryPlus
import com.tencao.saoui.themes.ThemeLoader
import com.tencao.saoui.util.IconCore
import com.tencao.saoui.util.UIUtil
import com.tencao.saoui.util.translate
import net.minecraft.client.entity.player.ClientPlayerEntity
import net.minecraft.client.resources.I18n.format
import java.lang.ref.WeakReference
import kotlin.math.max
import kotlin.math.min

/**
 * Part of saoui by Bluexin, released under GNU GPLv3.
 *
 * @author Bluexin
 */
class CategoryButton(val delegate: IconElement, parent: INeoParent? = null, private val init: (CategoryButton.() -> Unit)? = null) : IconElement(delegate.icon, delegate.pos) {

    override var pos: Vec2d by delegate::pos
    override var destination: Vec2d by delegate::destination
    override val boundingBox: BoundingBox2D by delegate::boundingBox
    override var idealBoundingBox: BoundingBox2D by delegate::idealBoundingBox
    override val elements by delegate::elements
    override val childrenXOffset by delegate::childrenXOffset
    override val childrenYOffset by delegate::childrenYOffset
    override val childrenXSeparator by delegate::childrenXSeparator
    override val childrenYSeparator by delegate::childrenYSeparator
    override val listed by delegate::listed
    override var visible: Boolean by delegate::visible
    override var highlighted: Boolean by delegate::highlighted
    override var selected: Boolean by delegate::selected
    override var disabled: Boolean by delegate::disabled
    override var opacity: Float by delegate::opacity
    override var scale: Vec2d by delegate::scale
    private var openAnim: WeakReference<IndexedScheduledCounter>? = null

    init {
        this.parent = parent
        delegate.parent = this

        delegate.onClick { _, _ ->
            if (elements.isNotEmpty() && !selected && !disabled) {
                open()
                SoundCore.MENU_POPUP.play()
            } else if (selected) {
                close()
                SoundCore.DIALOG_CLOSE.play()
            }

            true
        }

        delegate.onClickOut { _, _ ->
            if (selected) close()
        }

        delegate.init()

        init?.invoke(this)
    }

    override fun onClick(body: (Vec2d, MouseButton) -> Boolean) = delegate.onClick(body)
    override fun onClickOut(body: (Vec2d, MouseButton) -> Unit) = delegate.onClickOut(body)
    override fun hide() = delegate.hide()
    override fun show() = delegate.show()
    override fun drawBackground(mouse: Vec2d, partialTicks: Float, matrixStack: MatrixStack) = delegate.drawBackground(mouse, partialTicks, matrixStack)
    override fun draw(mouse: Vec2d, partialTicks: Float, matrixStack: MatrixStack) = delegate.draw(mouse, partialTicks, matrixStack)
    override fun drawForeground(mouse: Vec2d, partialTicks: Float, matrixStack: MatrixStack) = delegate.drawForeground(mouse, partialTicks, matrixStack)
    override fun contains(pos: Vec2d) = delegate.contains(pos)
    override fun update() {
        super.update()
        delegate.update()
    }

    override fun keyTyped(typedChar: String, keyCode: Int) {
        val button = KeyButton.getButton(keyCode)
        elementsSequence.firstOrNull { it.isOpen && it.selected }?.keyTyped(typedChar, keyCode) ?: let {
            if (button == KeyButton.FORWARD) {
                val selected = elementsSequence.firstOrNull { it.selected }
                var index = elements.indexOf(selected)
                if (index == -1) index = 0
                else if (--index < 0) index = elements.size.minus(1)
                selected?.selected = false
                elements[index].selected = true
            } else if (button == KeyButton.BACK) {
                val selected = elementsSequence.firstOrNull { it.selected }
                var index = elements.indexOf(selected)
                if (++index >= elements.size) index = 0
                selected?.selected = false
                elements[index].selected = true
            } else if (button == KeyButton.RIGHT || button == KeyButton.SPACE || button == KeyButton.ENTER) {
                val selected = elementsSequence.firstOrNull { it.selected } ?: return
                if (selected is CategoryButton) {
                    selected.open()
                } else if (selected is IconElement) {
                    selected.onClickBody(selected.pos, MouseButton.LEFT)
                }
            } else if (button == KeyButton.LEFT) {
                close(false)
            }
        }
    }

    fun open(reInit: Boolean = false) {
        highlighted = true
        selected = true
        isOpen = true

        if (reInit) elementsSequence.forEach(NeoElement::show)
        else {
            val children = childrenOrderedForAppearing().toList()

            val anim = IndexedScheduledCounter(3f, maxIdx = children.count() - 1) {
                children.elementAt(it).show()
                @Suppress("NestedLambdaShadowedImplicitParameter")
                if (it == children.count() - 1) elementsSequence.forEach(NeoElement::show)
            }
            +anim
            openAnim = WeakReference(anim)

            if (controllingParent is CoreGUI<*>) tlParent.move(Vec2d.ZERO)
            else tlParent.move(vec(-boundingBox.width(), 0))
        }
    }

    fun close(reInit: Boolean = false) {
        delegate.scroll = -3
        elementsSequence.forEach {
            it.hide()
            if (it is CategoryButton && it.highlighted) {
                it.close()
            }
        }
        highlighted = false
        selected = false
        isOpen = false
        if (!reInit) {
            if (tlParent != parent) {
                tlParent.move(vec(boundingBox.width(), 0))
            }
        }
        openAnim?.get()?.terminated = true
        openAnim = null
        if (parent is CoreGUI<*>) {
            UIUtil.resetMouse()
            (parent as CoreGUI<*>).move(Vec2d.ZERO)
        }
    }

    override operator fun plusAssign(element: NeoElement) {
        delegate += element
        element.hide()
    }

    override fun mouseClicked(pos: Vec2d, mouseButton: MouseButton): Boolean {
        return if ((mouseButton == MouseButton.SCROLL_DOWN || mouseButton == MouseButton.SCROLL_UP) && openAnim?.get()?.finished == false) true else delegate.mouseClicked(pos, mouseButton)
    }

    private fun childrenOrderedForAppearing(): Sequence<NeoElement> {
        val count = validElementsSequence.count()
        return if (count == 0) emptySequence()
        else {
            val selectedIdx = if (validElementsSequence.any { it is CategoryButton }) validElementsSequence.indexOfFirst { it.highlighted } else -1
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

    fun addDescription(description: MutableList<String>) {
        delegate.description.addAll(description)
    }

    fun addDescription(description: String) {
        delegate.description.add(description)
    }

    fun category(icon: IIcon, label: String, description: MutableList<String> = mutableListOf(), body: (CategoryButton.() -> Unit)? = null): CategoryButton {
        val cat = CategoryButton(IconLabelElement(icon, label, description = description), this, body)
        +cat
        return cat
    }

    fun partyMenu(): CategoryButton {
        val partyElement = PartyElement()
        if (!SAOCore.isSAOMCLibServerSide) {
            partyElement.disabled = true
            partyElement.description.add(format("saoui.server"))
        }
        val cat = CategoryButton(partyElement, this)
        +cat
        return cat
    }

    fun friendMenu(): CategoryButton {
        val friendElement = FriendElement(this)
        val cat = CategoryButton(friendElement, this)
        +cat
        return cat
    }

    fun profile(player: ClientPlayerEntity = Client.player!!, body: (CategoryButton.() -> Unit)? = null): CategoryButton {
        val cat = CategoryButton(ProfileElement(player, player != Client.player), this, body)
        +cat
        return cat
    }

    /*
    fun crafting(): CategoryButton {
        val cat = CategoryButton(CraftingElement(), this)
        +cat
        return cat
    }*/

    /*
    fun recipes(): CategoryButton {
        val cat = CategoryButton(IconLabelElement(IconCore.CRAFTING, format("sao.element.recipes")), this){
            addRecipes(AdvancementUtil.getRecipes())
        }
        +cat
        return cat
    }*/

    /*
    fun advancementCategory(advancement: Advancement): CategoryButton{
        return if (advancement.getProgress() != null && advancement.getProgress()!!.isDone) {
            val cat = CategoryButton(AdvancementElement(advancement, true), this) {
                category(Items.WRITTEN_BOOK.toIcon(), format("sao.element.quest.completed")) {
                    addAdvancements(AdvancementUtil.getAdvancements(advancement, true))
                }
                category(Items.WRITABLE_BOOK.toIcon(), format("sao.element.quest.inProgress")) {
                    addAdvancements(AdvancementUtil.getAdvancements(advancement, false))
                }
            }
            +cat
            cat
        }
        else advancement(advancement)
    }

    fun addAdvancements(advancements: Sequence<Advancement>){
        advancements.forEach {
            advancement(it)
        }
    }

    fun advancement(advancement: Advancement): CategoryButton{
        val parent = this
        val cat = CategoryButton(AdvancementElement(advancement), this) {
            onClick { vec, mouse ->
                (tlParent as CoreGUI<*>).openGui(PopupAdvancement(advancement)) += {
                    var index = parent.elements.indexOf(this)
                    when (it){
                        PopupAdvancement.Result.NEXT -> {
                            if (++index >= parent.elements.size)
                                index = 0
                            parent.elements[index].mouseClicked(vec, mouse)
                        }
                        PopupAdvancement.Result.PREVIOUS ->  {
                            if (--index < 0)
                                index = parent.elements.size.minus(1)
                            parent.elements[index].mouseClicked(vec, mouse)
                        }
                        else -> {}
                    }
                }
                true
            }
        }
        +cat
        return cat

    }*/

    /*
    fun addRecipes(advancements: List<Advancement>){
        advancements.forEach { advancement ->
            advancement.rewards.getRecipes()?.forEach { recipe ->
                +CategoryButton(RecipeElement(advancement, ForgeRegistries.RECIPE_SERIALIZERS.getValue(recipe)!!.), this)
            }
        }
    }*/

    fun reInit() {
        val wasOpen = this.highlighted
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

fun INeoParent.optionButton(option: OptionCore): IconLabelElement {
    val but = object : IconLabelElement(IconCore.OPTION, option.displayName, description = option.description.toMutableList()) {
        override var highlighted: Boolean
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

fun INeoParent.optionCategory(category: OptionCategory): CategoryButton {
    val cat = CategoryButton(IconLabelElement(IconCore.OPTION, category.unformattedName.translate(), description = category.description.toMutableList()))
    if (category == OptionCategory.THEME) {
        ThemeLoader.themeList.forEach {
            cat += themeButton(it)
        }
    }
    category.getSubCategories().forEach {
        cat += optionCategory(it)
    }
    category.getOptions().forEach {
        cat += optionButton(it)
    }
    cat.parent = this
    return cat
}

fun INeoParent.themeButton(theme: String): IconLabelElement {
    val but = object : IconLabelElement(IconCore.OPTION, theme, description = mutableListOf("Change theme to $theme")) {
        override var highlighted: Boolean
            get() = ThemeLoader.currentTheme == label.string && !OptionCore.VANILLA_UI.isEnabled
            set(_) = (Unit)
    }
    but.onClick { _, _ ->
        ThemeLoader.load(but.label.string)
        OptionCore.VANILLA_UI.disable()
        Client.minecraft.displayGuiScreen(null)
        Client.minecraft.displayGuiScreen(IngameMenu())
        true
    }
    but.parent = this
    return but
}
