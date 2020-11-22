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

package com.saomc.saoui.elements

import be.bluexin.saomclib.party.PlayerInfo
import com.saomc.saoui.GLCore
import com.saomc.saoui.SAOCore
import com.saomc.saoui.api.elements.IndexedScheduledCounter
import com.saomc.saoui.api.screens.IIcon
import com.saomc.saoui.config.OptionCore
import com.saomc.saoui.elements.custom.*
import com.saomc.saoui.elements.gui.*
import com.saomc.saoui.screens.util.toIcon
import com.saomc.saoui.util.AdvancementUtil
import com.saomc.saoui.util.IconCore
import com.saomc.saoui.util.getProgress
import com.teamwizardry.librarianlib.features.animator.AnimatableProperty
import com.teamwizardry.librarianlib.features.animator.Animation
import com.teamwizardry.librarianlib.features.animator.Animator
import com.teamwizardry.librarianlib.features.animator.Easing
import com.teamwizardry.librarianlib.features.animator.animations.BasicAnimation
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.Minecraft
import com.teamwizardry.librarianlib.features.kotlin.plus
import com.teamwizardry.librarianlib.features.math.BoundingBox2D
import com.teamwizardry.librarianlib.features.math.Vec2d
import net.minecraft.advancements.Advancement
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.resources.I18n
import net.minecraft.init.Items
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.common.registry.ForgeRegistries
import kotlin.math.max
import kotlin.math.min

/**
 * Part of saoui, released under GNU GPLv3.
 */
interface IElement {

    /**
     * Gets the element above this
     */
    var parent: IElement?

    /**
     * Gets the highest level element on this tree
     */
    val tlParent: IElement
        get() = parent?.tlParent ?: this

    /**
     * Gets the controlling GUI for these elements
     * TODO Ensure this points to the correct sub GUI
     */
    val controllingGUI: CoreGUI<*>?
        get() {
            return Minecraft().currentScreen as? CoreGUI<*>
        }

    /**
     * Used to launch a function on click, the
     * return will determine if the element will
     * open sub elements or not.
     *
     * @return false - Sub-elements will open
     * @return true - Sub-elements will not open
     */
    var onClickBody: (Vec2d, MouseButton) -> Boolean

    val init: (IElement.() -> Unit)?

    fun move(delta: Vec2d) {
        CoreGUI.animator.removeAnimationsFor(this)
        destination += delta
        +basicAnimation(this, "pos") {
            to = destination
            duration = 10f
            easing = Easing.easeInOutQuint
        }
    }

    /**
     * The original pos of the element
     */
    val originPos: Vec2d

    /**
     * The current pos of the element
     */
    var pos: Vec2d

    /**
     * The destination pos of the element
     */
    var destination: Vec2d

    var width: Int

    var height: Int

    val transparency: Float
        get() {
            var transparency = if (isFocus())
                opacity
            else
                opacity / 2
            if (transparency < 0f)
                transparency = 0f
            return transparency
        }

    var scroll
        get() = 0
        set(_) = Unit


    fun mouseClicked(pos: Vec2d, mouseButton: MouseButton) = false

    fun keyTyped(typedChar: Char, keyCode: Int) {}

    /**
     * Used to check if child elements should draw
     */
    var hasOpened: Boolean

    fun isFocus(): Boolean {
        return hasOpened && elementsSequence.none { it.hasOpened } && parent?.elementsSequence?.none { !isFocus() }?: true
    }

    fun drawBackground(mouse: Vec2d, partialTicks: Float)
    fun draw(mouse: Vec2d, partialTicks: Float)
    fun drawForeground(mouse: Vec2d, partialTicks: Float)

    var boundingBox: BoundingBox2D

    operator fun contains(pos: Vec2d) = pos in boundingBox

    fun init() {}


    val listed
        get() = true

    var visible
        get() = true
        set(_) = Unit

    var valid
        get() = true
        set(_) = Unit

    var selected
        get() = false
        set(_) = Unit

    var highlighted
        get() = false
        set(_) = Unit

    var disabled
        get() = false
        set(_) = Unit

    var opacity
        get() = 1f
        set(_) = Unit

    val scale
        get() = Vec2d.ONE

    fun hide() = Unit
    fun show() = Unit

    fun onClick(body: (Vec2d, MouseButton) -> Boolean): IElement {
        onClickBody = body
        return this
    }

    /**
     * Called when the element is opened
     */

    fun open(reInit: Boolean = false) {
        parent?.highlighted = true
        parent?.selected = true
        hasOpened = true

        if (reInit) elementsSequence.forEach(IElement::show) else {
            val children = childrenOrderedForAppearing().toList()
            val anim = IndexedScheduledCounter(3f, maxIdx = children.count() - 1) {
                children.elementAt(it).show()
                @Suppress("NestedLambdaShadowedImplicitParameter")
                if (it == children.count() - 1) elementsSequence.forEach(IElement::show)
            }
            +anim

            if (parent == null) tlParent.move(Vec2d.ZERO)
            else tlParent.move(vec(-boundingBox.width(), 0))
        }

    }

    /**
     * Called when the element is closed
     */
    fun close(reInit: Boolean = false) {
        scroll = -3
        elements.forEach {
            it.hide()
            it.close()
        }
        highlighted = false
        selected = false
        hasOpened = false
        pos = originPos
        destination = originPos
        if (!reInit) tlParent.move(vec(boundingBox.width(), 0))
    }


    fun reInit() {
        val wasOpen = this.highlighted
        if (wasOpen) this.close(true)
        this.elements.clear()
        this.init?.invoke(this)
        if (wasOpen) this.open(true)
    }

    val elements: MutableList<IElement>

    val elementsSequence
        get() = elements.asSequence().filter(IElement::listed)

    val otherElementsSequence
        get() = elements.asSequence().filter { !it.listed }

    val validElementsSequence
        get() = elementsSequence.filter{it.valid && it.hasOpened}

    val visibleElementsSequence
        get() = validElementsSequence.filter(IElement::visible)

    fun update() {
        this.elements.forEach(IElement::update)

        this.futureOperations.forEach { it() }
        this.futureOperations.clear()
    }

    operator fun plusAssign(element: IElement) {
        if (elements.none { it == element }) {
            if (elements.isNotEmpty()) {
                val bb1 = elements[0].boundingBox
                val bbNew = element.boundingBox
                if (bb1.widthI() >= bbNew.widthI()) {
                    element.boundingBox = BoundingBox2D(bbNew.min, vec(bb1.width(), bbNew.height()))
                } else {
                    elements.forEach {
                        val bb = it.boundingBox
                        it.boundingBox = BoundingBox2D(bb.min, vec(bbNew.width(), bb.height()))
                    }
                    element.boundingBox = bbNew
                }
            } else element.boundingBox = element.boundingBox
            elements += element

            element.parent = this
        }
    }

    operator fun IElement.unaryPlus() {
        parent?.plusAssign(this)
    }

    private fun childrenOrderedForAppearing(): Sequence<IElement> {
        val count = validElementsSequence.count()
        return if (count == 0) emptySequence()
        else {
            val selectedIdx = validElementsSequence.indexOfFirst { it.highlighted }
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

    fun mouseOverEffect() {
        GLCore.glBindTexture(RES_ITEM_GLINT)
        //GLCore.depth(true)
        GlStateManager.depthMask(false)
        GlStateManager.depthFunc(514)
        GLCore.glBlend(true)
        GLCore.blendFunc(GlStateManager.SourceFactor.SRC_COLOR.factor, GlStateManager.DestFactor.ONE.factor)
        GlStateManager.matrixMode(5890)
        GLCore.pushMatrix()
        GLCore.scale(8.0f, 8.0f, 8.0f)
        val f = (net.minecraft.client.Minecraft.getSystemTime() % 3000L).toFloat() / 3000.0f / 8.0f
        GLCore.translate(f, 0.0f, 0.0f)
        GLCore.glRotatef(-50.0f, 0.0f, 0.0f, 1.0f)
        GLCore.glTexturedRectV2(pos.x, pos.y, width = width.toDouble(), height = height.toDouble())
        GLCore.popMatrix()
        GLCore.pushMatrix()
        GLCore.scale(8.0f, 8.0f, 8.0f)
        val f1 = (net.minecraft.client.Minecraft.getSystemTime() % 4873L).toFloat() / 4873.0f / 8.0f
        GLCore.translate(-f1, 0.0f, 0.0f)
        GLCore.glRotatef(10.0f, 0.0f, 0.0f, 1.0f)
        GLCore.glTexturedRectV2(pos.x, pos.y, width = width.toDouble(), height = height.toDouble())
        GLCore.popMatrix()
        GlStateManager.matrixMode(5888)
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA)
        GlStateManager.depthFunc(515)
        GlStateManager.depthMask(true)
        //GLCore.depth(false)
    }

    val childrenXOffset
        get() = 0
    val childrenYOffset
        get() = 0
    val childrenXSeparator
        get() = 0
    val childrenYSeparator
        get() = 0

    val futureOperations: MutableList<IElement.() -> Unit>

    fun performLater(block: IElement.() -> Unit) {
        futureOperations += block
    }

    fun <T : Any> basicAnimation(target: T, property: String, init: (BasicAnimation<T>.() -> Unit)? = null) = com.saomc.saoui.api.elements.basicAnimation(target, AnimatableProperty.get(target.javaClass, property), init)

    operator fun Animator.plusAssign(animation: Animation<*>) = this.add(animation)

    companion object {
        val RES_ITEM_GLINT
            get() = ResourceLocation("textures/misc/enchanted_item_glint.png")
    }

    @CoreGUIDsl
    fun category(icon: IIcon, label: String, body: (IElement.() -> Unit)? = null): IElement {
        val cat = IconLabelElement(icon, label, this, init = body)
        +cat
        return cat
    }

    @CoreGUIDsl
    fun partyMenu(): PartyElement {
        val cat = PartyElement(parent)
        cat.disabled = !SAOCore.isSAOMCLibServerSide
        +cat
        return cat
    }

    @CoreGUIDsl
    fun friendMenu(): FriendElement {
        val cat = FriendElement(this)
        +cat
        return cat
    }

    @CoreGUIDsl
    fun profile(player: PlayerInfo): ProfileElement {
        val cat = ProfileElement(player, player.uuid == Minecraft().session.profile.id, this)
        +cat
        return cat
    }

    @CoreGUIDsl
    fun crafting(): CraftingElement {
        val cat = CraftingElement(this)
        +cat
        return cat
    }

    @CoreGUIDsl
    fun recipes(): IElement {
        val cat = IconLabelElement(IconCore.CRAFTING, I18n.format("sao.element.recipes"), this) {
            addRecipes(AdvancementUtil.getRecipes())
        }
        cat.parent = this
        +cat
        return cat
    }

    @CoreGUIDsl
    fun advancementCategory(advancement: Advancement): AdvancementElement {
        return if (advancement.getProgress() != null && advancement.getProgress()!!.isDone) {
            val cat = AdvancementElement(advancement, true) {
                category(Items.WRITTEN_BOOK.toIcon(), I18n.format("sao.element.quest.completed")) {
                    addAdvancements(AdvancementUtil.getAdvancements(advancement, true))
                }
                category(Items.WRITABLE_BOOK.toIcon(), I18n.format("sao.element.quest.inProgress")) {
                    addAdvancements(AdvancementUtil.getAdvancements(advancement, false))
                }
            }
            cat.parent = this
            +cat
            cat
        } else advancement(advancement)
    }

    @CoreGUIDsl
    fun addAdvancements(advancements: Sequence<Advancement>) {
        advancements.forEach {
            advancement(it)
        }
    }

    @CoreGUIDsl
    fun advancement(advancement: Advancement): AdvancementElement {
        val parent = this
        val cat = AdvancementElement(advancement) {
            onClick { vec, mouse ->
                controllingGUI?.openGui(PopupAdvancement(advancement))?.plusAssign {
                    var index = parent.elements.indexOf(this)
                    when (it) {
                        PopupAdvancement.Result.NEXT -> {
                            if (++index >= parent.elements.size)
                                index = 0
                            parent.elements[index].mouseClicked(vec, mouse)
                        }
                        PopupAdvancement.Result.PREVIOUS -> {
                            if (--index < 0)
                                index = parent.elements.size.minus(1)
                            parent.elements[index].mouseClicked(vec, mouse)
                        }
                        else -> {
                        }
                    }
                }
                true
            }
        }
        cat.parent = this
        +cat
        return cat

    }

    @CoreGUIDsl
    fun addRecipes(advancements: List<Advancement>) {
        advancements.forEach { advancement ->
            advancement.rewards.recipes?.forEach { recipe ->
                +RecipeElement(advancement, ForgeRegistries.RECIPES.getValue(recipe)!!, this)
            }
        }
    }

    @CoreGUIDsl
    fun optionButton(option: OptionCore): IElement {
        val but = object : IconLabelElement(IconCore.OPTION, option.displayName, this, description = option.description.toMutableList()) {
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

    @CoreGUIDsl
    fun optionCategory(option: OptionCore): IElement {
        val cat = IconLabelElement(IconCore.OPTION, option.displayName, this, description = option.description.toMutableList())
        option.subOptions.forEach {
            cat += if (it.isCategory) optionCategory(it)
            else optionButton(it)
        }
        cat.parent = this
        return cat
    }

}
