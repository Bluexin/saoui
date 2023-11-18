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

import com.mojang.blaze3d.vertex.PoseStack
import com.tencao.saoui.SAOCore
import com.tencao.saoui.SoundCore
import com.tencao.saoui.Vector2d
import com.tencao.saoui.api.screens.IIcon
import com.tencao.saoui.api.scripting.catching
import com.tencao.saoui.config.OptionCategory
import com.tencao.saoui.config.OptionCore
import com.tencao.saoui.screens.CoreGUI
import com.tencao.saoui.screens.KeyButton
import com.tencao.saoui.screens.MouseButton
import com.tencao.saoui.screens.menus.IngameMenu
import com.tencao.saoui.screens.unaryPlus
import com.tencao.saoui.themes.ThemeManager
import com.tencao.saoui.themes.ThemeMetadata
import com.tencao.saoui.util.*
import com.tencao.saoui.util.math.BoundingBox2D
import com.tencao.saoui.util.math.vec
import li.cil.repack.com.naef.jnlua.LuaValueProxy
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Player
import org.joml.Vector2d
import java.lang.ref.WeakReference
import kotlin.math.max
import kotlin.math.min

/**
 * Part of saoui by Bluexin, released under GNU GPLv3.
 *
 * @author Bluexin
 */
open class CategoryButton(
    val delegate: IconElement,
    parent: INeoParent? = null,
    private val init: (CategoryButton.() -> Unit)? = null
) : IconElement(delegate.icon, delegate.pos) {

    override var pos: Vector2d by delegate::pos
    override var destination: Vector2d by delegate::destination
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
    override var scale: Vector2d by delegate::scale
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

    override fun onClick(body: (Vector2d, MouseButton) -> Boolean) = delegate.onClick(body)

    @JvmName("onClick")
    @Suppress("unused") // for lua
    fun luaOnClick(bodyProxy: LuaValueProxy) = onClick { _, _ ->
        val state = bodyProxy.luaState
        state.catching(false) {
            bodyProxy.pushValue()
            val r = if (state.isFunction(-1)) {
                state.pushJavaObject(this)
                state.call(1, 1)
                state.checkBoolean(-1).also {
                    state.pop(1)
                }
            } else false
//            state.pop(1)
            r
        }
    }

    override fun onClickOut(body: (Vector2d, MouseButton) -> Unit) = delegate.onClickOut(body)
    override fun hide() = delegate.hide()
    override fun show() = delegate.show()
    override fun drawBackground(mouse: Vector2d, partialTicks: Float, poseStack: PoseStack) = delegate.drawBackground(mouse, partialTicks, poseStack)
    override fun draw(mouse: Vector2d, partialTicks: Float, poseStack: PoseStack) = delegate.draw(mouse, partialTicks, poseStack)
    override fun drawForeground(mouse: Vector2d, partialTicks: Float, poseStack: PoseStack) = delegate.drawForeground(mouse, partialTicks, poseStack)
    override fun contains(pos: Vector2d) = delegate.contains(pos)
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
                if (it == children.count() - 1) elementsSequence.forEach(NeoElement::show)
            }
            +anim
            openAnim = WeakReference(anim)

            if (controllingParent is CoreGUI<*>) tlParent.move(Vector2d(0, 0))
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

    override fun mouseClicked(pos: Vector2d, mouseButton: MouseButton): Boolean {
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

    @JvmName("addStringListDescription")
    fun addDescription(description: MutableList<String>) {
        delegate.description.addAll(description)
    }

    @JvmName("addCompoundListDescription")
    fun addDescription(description: MutableList<Component>) {
        delegate.description.addAll(description.map { it.contents })
    }

    fun addDescription(description: String) {
        delegate.description.add(description)
    }

    fun addDescription(description: Component) {
        delegate.description.add(description.contents)
    }

    /*
    fun category(
        icon: IIcon,
        label: String,
        description: MutableList<String> = mutableListOf(),
        body: (CategoryButton.() -> Unit)? = null
    ): CategoryButton {
        val cat = CategoryButton(IconLabelElement(icon, label, description = description), this, body)
        +cat
        return cat
    }*/

    fun category(
        icon: IIcon,
        label: Component,
        description: MutableList<String> = mutableListOf(),
        body: (CategoryButton.() -> Unit)? = null
    ): CategoryButton {
        val cat = CategoryButton(IconLabelElement(icon, label, description = description), this, body)
        +cat
        return cat
    }

    @Suppress("unused") // For Lua
    @JvmOverloads
    @JvmName("category")
    fun luaCategory(
        icon: String,
        label: String,
        description: List<String> = listOf(),
        bodyProxy: LuaValueProxy? = null
    ): CategoryButton {
        val body: (CategoryButton.() -> Unit)? = bodyProxy?.let {
            {
                val state = it.luaState
                state.catching(Unit) {
                    it.pushValue()
                    if (state.isFunction(-1)) {
                        state.pushJavaObject(this)
                        state.call(1, 0)
                    }
                    state.pop(1)
                }
            }
        }

        return category(IconCore.valueOf(icon), label.translate(), description.toMutableList(), body)
    }

    fun partyMenu(): CategoryButton {
        val partyElement = PartyElement()
        if (!SAOCore.isSAOMCLibServerSide) {
            partyElement.disabled = true
            partyElement.description.add("saoui.server".localize())
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

    fun profile(body: (CategoryButton.() -> Unit)? = null): CategoryButton {
        val cat = CategoryButton(ProfileElement(), this, body)
        +cat
        return cat
    }

    fun profile(player: Player, body: (CategoryButton.() -> Unit)? = null): CategoryButton {
        val cat = CategoryButton(ProfileElement(player), this, body)
        +cat
        return cat
    }

    fun crafting(): CategoryButton {
        val cat = CategoryButton(CraftingElement(), this)
        +cat
        return cat
    }

    /*
    fun recipes(): CategoryButton {
        val cat = CategoryButton(IconLabelElement(IconCore.CRAFTING, "sao.element.recipes".localize()), this) {
            addRecipes(AdvancementUtil.getRecipes())
        }
        +cat
        return cat
    }

    fun advancementCategory(advancement: Advancement): CategoryButton {
        return if (advancement.getProgress() != null && advancement.getProgress()!!.isDone) {
            val cat = CategoryButton(AdvancementElement(advancement, true), this) {
                category(Items.WRITTEN_BOOK.toIcon(), "sao.element.quest.completed".localize()) {
                    addAdvancements(AdvancementUtil.getAdvancements(advancement, true))
                }
                category(Items.WRITABLE_BOOK.toIcon(), "sao.element.quest.inProgress".localize()) {
                    addAdvancements(AdvancementUtil.getAdvancements(advancement, false))
                }
            }
            +cat
            cat
        } else advancement(advancement)
    }

    fun addAdvancements(advancements: Sequence<Advancement>) {
        advancements.forEach {
            advancement(it)
        }
    }

    fun advancement(advancement: Advancement): CategoryButton {
        val parent = this
        val cat = CategoryButton(AdvancementElement(advancement), this) {
            onClick { vec, mouse ->
                (tlParent as CoreGUI<*>).openGui(PopupAdvancement(advancement)) += {
                    var index = parent.elements.indexOf(this)
                    when (it) {
                        PopupAdvancement.Result.NEXT -> {
                            if (++index >= parent.elements.size) {
                                index = 0
                            }
                            parent.elements[index].mouseClicked(vec, mouse)
                        }
                        PopupAdvancement.Result.PREVIOUS -> {
                            if (--index < 0) {
                                index = parent.elements.size.minus(1)
                            }
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

    fun addRecipes(advancements: List<Advancement>) {
        advancements.forEach { advancement ->
            advancement.rewards.recipes.forEach { recipe ->
                Client.minecraft.level?.recipeManager?.byKey(recipe)?.ifPresent {
                    +CategoryButton(RecipeElement(it), this)
                }
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
        return "CategoryButton(delegate=$delegate, elements=${elements.size})"
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


//TODO Seperate options to allow users to define their own
fun INeoParent.optionCategory(category: OptionCategory): CategoryButton {
    val cat = CategoryButton(IconLabelElement(IconCore.OPTION, category.unformattedName.translate(), description = category.description.toMutableList()))
    if (category == OptionCategory.THEME) {
        ThemeManager.themeList.forEach {
            cat += themeButton(it.value)
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

private fun translateIfExists(key: String, default: String) =
    if (key.check()) key.localize() else default

private val ResourceLocation.tl get() = toString().replace(':', '.')

fun INeoParent.themeButton(theme: ThemeMetadata): IconLabelElement {
    val realName = translateIfExists(theme.nameTranslationKey, theme.name)
    val but = object :
        IconLabelElement(
            IconCore.OPTION, realName,
            description = mutableListOf(
                "Change theme to $realName",
                translateIfExists(theme.descTranslationKey, "No description for $realName (${theme.id})")
            )
        ) {
        override var highlighted: Boolean
            get() = ThemeManager.currentTheme === theme && !OptionCore.VANILLA_UI.isEnabled
            set(_) = (Unit)
    }
    but.onClick { _, _ ->
        ThemeManager.load(theme.id)
        OptionCore.VANILLA_UI.disable()
        Client.minecraft.setScreen(null)
        Client.minecraft.setScreen(IngameMenu())
        true
    }
    but.parent = this
    return but
}
