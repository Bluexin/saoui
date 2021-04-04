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

package com.saomc.saoui.api.elements

import com.saomc.saoui.SAOCore
import com.saomc.saoui.SoundCore
import com.saomc.saoui.api.screens.IIcon
import com.saomc.saoui.config.OptionCore
import com.saomc.saoui.play
import com.saomc.saoui.screens.CoreGUI
import com.saomc.saoui.screens.MouseButton
import com.saomc.saoui.screens.menus.IngameMenu
import com.saomc.saoui.screens.unaryPlus
import com.saomc.saoui.screens.util.PopupAdvancement
import com.saomc.saoui.screens.util.toIcon
import com.saomc.saoui.themes.ThemeLoader
import com.saomc.saoui.util.AdvancementUtil
import com.saomc.saoui.util.IconCore
import com.saomc.saoui.util.getProgress
import com.saomc.saoui.util.getRecipes
import com.teamwizardry.librarianlib.features.gui.component.supporting.delegate
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.Client
import com.teamwizardry.librarianlib.features.math.Vec2d
import net.minecraft.advancements.Advancement
import net.minecraft.client.resources.I18n.format
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Items
import net.minecraftforge.fml.common.registry.ForgeRegistries
import org.lwjgl.input.Keyboard
import java.lang.ref.WeakReference
import kotlin.math.max
import kotlin.math.min

/**
 * Part of saoui by Bluexin, released under GNU GPLv3.
 *
 * @author Bluexin
 */
class CategoryButton(val delegate: IconElement, parent: INeoParent? = null, private val init: (CategoryButton.() -> Unit)? = null) : IconElement(delegate.icon, delegate.pos) {

    override var pos by delegate::pos.delegate
    override var destination by delegate::destination.delegate
    override val boundingBox by delegate::boundingBox.delegate
    override var idealBoundingBox by delegate::idealBoundingBox.delegate
    override val elements by delegate::elements.delegate
    override val childrenXOffset by delegate::childrenXOffset.delegate
    override val childrenYOffset by delegate::childrenYOffset.delegate
    override val childrenXSeparator by delegate::childrenXSeparator.delegate
    override val childrenYSeparator by delegate::childrenYSeparator.delegate
    override val listed by delegate::listed.delegate
    override var visible by delegate::visible.delegate
    override var highlighted by delegate::highlighted.delegate
    override var selected by delegate::selected.delegate
    override var disabled by delegate::disabled.delegate
    override var opacity by delegate::opacity.delegate
    override var scale by delegate::scale.delegate
    private var openAnim: WeakReference<IndexedScheduledCounter>? = null

    init {
        this.parent = parent
        delegate.parent = this

        delegate.onClick { _, _ ->
            if (elements.isNotEmpty() && !selected && !disabled) {
                open()
                SoundCore.MENU_POPUP.play()
            }
            else if (selected) {
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
    override fun drawBackground(mouse: Vec2d, partialTicks: Float) = delegate.drawBackground(mouse, partialTicks)
    override fun draw(mouse: Vec2d, partialTicks: Float) = delegate.draw(mouse, partialTicks)
    override fun drawForeground(mouse: Vec2d, partialTicks: Float) = delegate.drawForeground(mouse, partialTicks)
    override fun contains(pos: Vec2d) = delegate.contains(pos)
    override fun update() {
        super.update()
        delegate.update()
    }

    override fun keyTyped(typedChar: Char, keyCode: Int) {
        elementsSequence.firstOrNull { it.isOpen && it.selected }?.keyTyped(typedChar, keyCode) ?: let {
            if (keyCode == Client.minecraft.gameSettings.keyBindForward.keyCode || keyCode == Keyboard.KEY_UP) {
                val selected = elementsSequence.firstOrNull { it.selected }
                var index = elements.indexOf(selected)
                if (index == -1) index = 0
                else if (--index < 0) index = elements.size.minus(1)
                selected?.selected = false
                elements[index].selected = true
            } else if (keyCode == Client.minecraft.gameSettings.keyBindBack.keyCode || keyCode == Keyboard.KEY_DOWN) {
                val selected = elementsSequence.firstOrNull { it.selected }
                var index = elements.indexOf(selected)
                if (++index >= elements.size) index = 0
                selected?.selected = false
                elements[index].selected = true
            } else if (keyCode == Client.minecraft.gameSettings.keyBindRight.keyCode || keyCode == Keyboard.KEY_RIGHT || keyCode == Client.minecraft.gameSettings.keyBindJump.keyCode || keyCode == Keyboard.KEY_RETURN){
                val selected = elementsSequence.firstOrNull { it.selected } ?: return
                if (selected is CategoryButton) {
                    selected.open()
                }
                else if (selected is IconElement) {
                    selected.onClickBody(selected.pos, MouseButton.LEFT)
                }
            }
            else if (keyCode == Client.minecraft.gameSettings.keyBindLeft.keyCode || keyCode == Keyboard.KEY_LEFT || keyCode == Client.minecraft.gameSettings.keyBindAttack.keyCode){
                close(false)
            }
        }
    }

    fun open(reInit: Boolean = false) {
        highlighted = true
        selected = true
        isOpen = true

        if (reInit) elementsSequence.forEach(NeoElement::show) else {
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

    fun addDescription(description: MutableList<String>){
        delegate.description.addAll(description)
    }

    fun addDescription(description: String){
        delegate.description.add(description)
    }

    fun category(icon: IIcon, label: String, description: MutableList<String> = mutableListOf(), body: (CategoryButton.() -> Unit)? = null): CategoryButton {
        val cat = CategoryButton(IconLabelElement(icon, label, description = description), this, body)
        +cat
        return cat
    }

    fun partyMenu(): CategoryButton {
        val partyElement = PartyElement()
        if (!SAOCore.isSAOMCLibServerSide){
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

    fun profile(player: EntityPlayer, body: (CategoryButton.() -> Unit)? = null): CategoryButton {
        val cat = CategoryButton(ProfileElement(player, player != Client.minecraft.player), this, body)
        +cat
        return cat
    }

    fun crafting(): CategoryButton {
        val cat = CategoryButton(CraftingElement(), this)
        +cat
        return cat
    }

    fun recipes(): CategoryButton {
        val cat = CategoryButton(IconLabelElement(IconCore.CRAFTING, format("sao.element.recipes")), this){
            addRecipes(AdvancementUtil.getRecipes())
        }
        +cat
        return cat
    }

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

    }

    fun addRecipes(advancements: List<Advancement>){
        advancements.forEach { advancement ->
            advancement.rewards.getRecipes()?.forEach { recipe ->
                +CategoryButton(RecipeElement(advancement, ForgeRegistries.RECIPES.getValue(recipe)!!), this)
            }
        }
    }

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


fun INeoParent.optionCategory(option: OptionCore): CategoryButton {
    val cat = CategoryButton(IconLabelElement(IconCore.OPTION, option.displayName, description = option.description.toMutableList()))
    if (option == OptionCore.THEME){
        ThemeLoader.themeFolder.list()?.forEach {
            cat += themeButton(it)
        }
    }
    option.subOptions.forEach {
        cat += if (it.isCategory) {
            optionCategory(it)
        }
        else optionButton(it)
    }
    cat.parent = this
    return cat
}


fun INeoParent.themeButton(theme: String): IconLabelElement {
    val but = object : IconLabelElement(IconCore.OPTION, theme, description = mutableListOf("Change theme to $theme")) {
        override var highlighted: Boolean
            get() = ThemeLoader.currentTheme == label && !OptionCore.VANILLA_UI.isEnabled
            set(_) = (Unit)

    }
    but.onClick { _, _ ->
        ThemeLoader.load(but.label)
        OptionCore.VANILLA_UI.disable()
        Client.minecraft.displayGuiScreen(null)
        Client.minecraft.displayGuiScreen(IngameMenu())
        true
    }
    but.parent = this
    return but
}

