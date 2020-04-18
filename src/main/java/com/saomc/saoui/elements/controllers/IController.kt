package com.saomc.saoui.elements.controllers

import com.saomc.saoui.GLCore
import com.saomc.saoui.api.elements.IndexedScheduledCounter
import com.saomc.saoui.api.screens.IIcon
import com.saomc.saoui.config.OptionCore
import com.saomc.saoui.elements.AdvancementElement
import com.saomc.saoui.elements.DrawType
import com.saomc.saoui.elements.IElement
import com.saomc.saoui.elements.IconLabelElement
import com.saomc.saoui.screens.CoreGUIDsl
import com.saomc.saoui.screens.unaryPlus
import com.saomc.saoui.screens.util.toIcon
import com.saomc.saoui.util.AdvancementUtil
import com.saomc.saoui.util.IconCore
import com.saomc.saoui.util.getProgress
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.minus
import com.teamwizardry.librarianlib.features.math.BoundingBox2D
import com.teamwizardry.librarianlib.features.math.Vec2d
import net.minecraft.advancements.Advancement
import net.minecraft.client.resources.I18n
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Items
import java.lang.ref.WeakReference
import kotlin.math.max
import kotlin.math.min

interface IController: IElement{
    val delegate: IElement?
        get() = null

    val tlController: IController
            get() = controllingParent.tlController

    val init: (IController.() -> Unit)
        get() = {}

    /**
     * All elements excluding delegate that belongs to this controller
     */
    val elements: MutableList<IElement>
        get() = mutableListOf()

    /**
     * All listed elements as a sequence
     */
    val elementsSequence
        get() = elements.asSequence().filter(IElement::listed)

    /**
     * All other elements that aren't listed
     */
    val otherElementsSequence
        get() = elements.asSequence().filter { !it.listed }

    /**
     * All valid elements
     */
    val validElementsSequence
        get() = elementsSequence.filter(IElement::valid)

    /**
     * All visible elements for rendering
     */
    val visibleElementsSequence
        get() = validElementsSequence.filter(IElement::visible)

    /**
     * Gets the offsets used by the child elements
     */
    open val childrenXOffset: Int
        get() = 0
    open val childrenYOffset: Int
        get() = 0
    open val childrenXSeparator: Int
        get() = 0
    open val childrenYSeparator: Int
        get() = 0

    /**
     * Checks if the controller has opened
     */
    var hasOpened
        get() = false
        set(_) = Unit

    /**
     * The animation controller for this controller
     */
    var openAnim: WeakReference<IndexedScheduledCounter>?
        get() = null
        set(_) = Unit

    /**
     * Same as open but without moving elements.
     */
    fun openInit(){
        highlighted = true
        selected = true
        hasOpened = true
        init.invoke(this)

        elementsSequence.forEach(IElement::open)
    }

    fun reInit(){
        closeInit()
        openInit()
    }

    /**
     * Opens the current controller
     * @see IElement.open
     */
    override fun open() {
        highlighted = true
        selected = true
        hasOpened = true

        val children = childrenOrderedForAppearing().toList()

        val anim = IndexedScheduledCounter(3f, maxIdx = children.count() - 1) {
            children.elementAt(it).open()
            @Suppress("NestedLambdaShadowedImplicitParameter")
            if (it == children.count() - 1) elementsSequence.forEach(IElement::open)
        }
        +anim
        openAnim = WeakReference(anim)
        controllingParent.move(vec(-boundingBox.width(), 0))
    }

    /**
     * Same as close but without moving elements.
     */
    fun closeInit(){
        scroll = -3
        elements.forEach {
            it.hide()
            if (it is Controller) {
                it.close()
            }
        }
        highlighted = false
        selected = false
        hasOpened = false
        openAnim?.get()?.terminated = true
        openAnim = null
    }

    /**
     * Closes the current controller
     * @see IElement.close
     */
    override fun close() {
        scroll = -3
        elements.forEach {
            it.hide()
            if (it is Controller) {
                it.close()
            }
        }
        highlighted = false
        selected = false
        hasOpened = false
        controllingParent.move(vec(boundingBox.width(), 0))
        openAnim?.get()?.terminated = true
        openAnim = null
    }

    override fun drawBackground(mouse: Vec2d, partialTicks: Float) {}
    override fun draw(mouse: Vec2d, partialTicks: Float) {}
    override fun drawForeground(mouse: Vec2d, partialTicks: Float) {}

    fun drawChildren(mouse: Vec2d, partialTicks: Float, drawType: DrawType) {
        if (visibleElementsSequence.count() > 0) {
            val c = validElementsSequence.take(7).count()
            val centering = ((c + c % 2 - 2) * childrenYSeparator) / 2.0
            GLCore.translate(pos.x + childrenXOffset, pos.y + childrenYOffset - centering, 0.0)
            var nmouse = mouse - pos - vec(childrenXOffset, childrenYOffset - centering)
            childrenOrderedForRendering().forEachIndexed { i, it ->
                if (c == 7 && (i == 0 || i == 6)) it.opacity /= 2
                when (drawType){
                    DrawType.BACKGROUND -> it.drawBackground(mouse, partialTicks)
                    DrawType.DRAW -> it.draw(mouse, partialTicks)
                    DrawType.FOREGROUND -> it.drawForeground(mouse, partialTicks)
                }
                it.draw(nmouse, partialTicks)
                GLCore.translate(childrenXSeparator.toDouble(), childrenYSeparator.toDouble(), 0.0)
                nmouse -= vec(childrenXSeparator, childrenYSeparator)
                if (c == 7 && (i == 0 || i == 6)) it.opacity *= 2
            }
            nmouse = mouse - pos - vec(childrenXOffset, childrenYOffset - centering)
            otherElementsSequence.forEach {
                when (drawType){
                    DrawType.BACKGROUND -> it.drawBackground(mouse, partialTicks)
                    DrawType.DRAW -> it.draw(mouse, partialTicks)
                    DrawType.FOREGROUND -> it.drawForeground(mouse, partialTicks)
                }
            }
        }
    }

    fun childrenOrderedForRendering(): Sequence<IElement> {
        val count = visibleElementsSequence.count()
        return if (count == 0) emptySequence()
        else {
            val selectedIdx = if (visibleElementsSequence.any { it is Controller }) visibleElementsSequence.indexOfFirst { it.highlighted } else -1
            when {
                selectedIdx >= 0 -> {
                    val skipFront = (selectedIdx - (count / 2 - (count + 1) % 2) + count) % count
                    visibleElementsSequence.drop(skipFront) + visibleElementsSequence.take(skipFront)
                }
                validElementsSequence.count() < 7 -> visibleElementsSequence
                else -> {
                    val s = visibleElementsSequence + visibleElementsSequence
                    s.drop(min(max((scroll + count) % count, 0), count)).take(min(7, count))
                }
            }
        }
    }

    fun childrenOrderedForAppearing(): Sequence<IElement> {
        val count = validElementsSequence.count()
        return if (count == 0) emptySequence()
        else {
            val selectedIdx = if (validElementsSequence.any { it is Controller }) validElementsSequence.indexOfFirst { it.highlighted } else -1
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

    /**
     * Adds an element to the current controller
     */
    open operator fun plusAssign(element: IElement) {
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

        element.controllingParent = this
    }

    /**
     * Adds an element to the current controller
     */
    open operator fun IElement.unaryPlus() {
        this@IController += this
    }

    @CoreGUIDsl
    fun category(icon: IIcon, label: String, body: IController.() -> Unit = {}): Controller {
        val cat = Controller(IconLabelElement(icon, label, this), this, body)
        +cat
        return cat
    }

    @CoreGUIDsl
    fun category(element: IElement, body: IController.() -> Unit = {}): Controller {
        val cat = Controller(element, this, body)
        +cat
        return cat
    }

    @CoreGUIDsl
    fun partyMenu(): PartyController {
        val cat = PartyController(this)
        +cat
        return cat
    }

    @CoreGUIDsl
    fun friendMenu(): FriendController {
        val cat = FriendController(this)
        +cat
        return cat
    }

    @CoreGUIDsl
    fun profile(player: EntityPlayer): ProfileController {
        val cat = ProfileController(this, player)
        +cat
        return cat
    }

    @CoreGUIDsl
    fun crafting(): CraftingController {
        val cat = CraftingController(this)
        +cat
        return cat
    }

    /*
    @CoreGUIDsl
    fun recipes(): CategoryButton {
        val cat = CategoryButton(IconLabelElement(IconCore.CRAFTING, I18n.format("sao.element.recipes")), this){
            addRecipes(AdvancementUtil.getRecipes())
        }
        +cat
        return cat
    }*/

    @CoreGUIDsl
    fun advancementCategory(advancement: Advancement): IElement {
        return if (advancement.getProgress() != null && advancement.getProgress()!!.isDone) {
            val cat = category(AdvancementElement(advancement, this)) {
                category(Items.WRITTEN_BOOK.toIcon(), I18n.format("sao.element.quest.completed")) {
                    addAdvancements(AdvancementUtil.getAdvancements(advancement, true))
                }
                category(Items.WRITABLE_BOOK.toIcon(), I18n.format("sao.element.quest.inProgress")) {
                    addAdvancements(AdvancementUtil.getAdvancements(advancement, false))
                }
            }
            +cat
            cat
        }
        else advancement(advancement)
    }

    @CoreGUIDsl
    fun addAdvancements(advancements: Sequence<Advancement>){
        advancements.forEach {
            advancement(it)
        }
    }

    @CoreGUIDsl
    fun advancement(advancement: Advancement): AdvancementElement {
        val cat = AdvancementElement(advancement, this)
        +cat
        return cat
    }

    /*
    @CoreGUIDsl
    fun addRecipes(advancements: Sequence<Advancement>){
        advancements.forEach {advancement ->
            advancement.rewards.recipes.forEach { recipe ->
                +CategoryButton(RecipeElement(advancement, ForgeRegistries.RECIPES.getValue(recipe)!!), this)
            }
        }
    }*/

    @CoreGUIDsl
    fun optionButton(option: OptionCore): IconLabelElement {
        val controller = this
        val but = object : IconLabelElement(IconCore.OPTION, option.displayName, controller) {
            override var highlighted: Boolean
                get() = option.isEnabled
                set(value) = if (value) option.enable() else option.disable()
        }
        but.onOpen {
            option.flip()
        }
        return but
    }

    @CoreGUIDsl
    fun optionCategory(option: OptionCore): Controller {
        val cat = category(IconLabelElement(IconCore.OPTION, option.displayName, this, description = option.description.toMutableList()))
        option.subOptions.forEach {
            cat += if (it.isCategory) optionCategory(it)
            else optionButton(it)
        }
        return cat
    }
}
