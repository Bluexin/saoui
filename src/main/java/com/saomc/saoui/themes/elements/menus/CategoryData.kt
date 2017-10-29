package com.saomc.saoui.themes.elements.menus

import com.saomc.saoui.GLCore
import com.saomc.saoui.api.elements.*
import com.saomc.saoui.api.events.ElementAction
import com.saomc.saoui.api.screens.Actions
import com.saomc.saoui.api.screens.IIcon
import net.minecraft.client.Minecraft
import net.minecraft.client.resources.I18n
import net.minecraftforge.common.MinecraftForge

data class CategoryData(val category: CategoryEnum, val parentCategory: CategoryData?) {

    private var elements = mutableListOf<IElement>()
    private lateinit var parent: MenuElementParent
    var categoryElement: IElement? = null
    //If the elements should render
    private var enabled: Boolean = false
    //If the elements are selectable or locked
    private var focus: Boolean = false
    //The y value each element will have from the next. Needs to be moved to xml
    private var yIncrement = 24
    //The x value each element will have from the parent element. Needs to be moved to xml
    private var xIncrement = 16
    //Dynamic width for elements
    var width = 100

    fun actionPerformed(element: IElement, action: Actions, data: Int, menutElement: MenuElementParent) {
        MinecraftForge.EVENT_BUS.post(ElementAction(element.name, action, data, element.isOpen, !focus, menutElement, element.elementType))
    }

    fun addElement(type: MenuDefEnum, icon: IIcon, name: String, displayName: String, elementType: ElementDefEnum) {
        val data = SlotData(type, icon, name, I18n.format(displayName), elementType)
        elements.add(data)
        setWidth(data.displayName)
    }

    operator fun plusAssign(element: IElement) {
        elements.plusAssign(element)
    }

    fun draw(mc: Minecraft, mouseX: Int, mouseY: Int) {
        if (enabled)
            elements.forEach { it.draw(mc, mouseX, mouseY) }
    }

    fun init(parent: MenuElementParent) {
        this.parent = parent
        this.enabled = category == CategoryEnum.MAIN
        this.focus = enabled
        elements.forEach { it.init(parent, this) }
//        if (this.parentCategory != null) {
//            this.categoryElement = parentCategory.getParentElement(category.name)
//        }
    }

    fun isFocus(): Boolean {
        return focus
    }

    fun setWidth(name: String) {
        val newWidth = 40 + (GLCore.glStringWidth(if (name.length > 50) name.substring(0, 50) else name))
        if (newWidth > width) width = newWidth
    }

    fun getX(): Int {
        return (categoryElement?.width ?: 20).plus(parentCategory?.getX() ?: 0).plus(xIncrement)
    }

    fun getY(element: IElement): Int {
        if (categoryElement != null)
            return (parentCategory?.getY(categoryElement!!) ?: 0).plus((elements.indexOf(element)).times(yIncrement)).plus(getYOffset())
        else return (elements.indexOf(element)).times(yIncrement)
    }

    fun getYOffset(): Int {
        when (elements.size) {
            1 -> return 0
            2 -> return -13
            3 -> return -24
            4 -> return -36
            else -> return -48
        }
    }

    /**
     * Used to get an element belonging to a category
     */
    fun getParentElement(name: String): IElement {
        return elements.find { it.name.equals(name, true) }!!
    }

    fun mouseClicked(cursorX: Int, cursorY: Int, action: Actions): Boolean {
        if (enabled) {
            val element: IElement? = elements.firstOrNull { it.mouseClicked(cursorX, cursorY, action) }
            if (element != null) {
                actionPerformed(element, action, 0, parent)
                return true
            }
            return false
        }
        return false
    }

    fun mouseScroll(cursorX: Int, cursorY: Int, delta: Int) {
        if (enabled) {
            val element: IElement? = elements.firstOrNull { it.mouseScroll(cursorX, cursorY, delta) }
            if (element != null) {
                actionPerformed(element, Actions.MOUSE_WHEEL, delta, parent)
            }
        }
    }

    /**
     * Called when the menu is just opening or closing
     */
    fun setEnabled(flag: Boolean) {
        if (flag || focus && !flag) {
            this.enabled = flag
            parentCategory?.setOpen(category, flag)
            resetElemments()
        }
    }

    fun setOpen(category: CategoryEnum, flag: Boolean) {
        focus = !flag
        getParentElement(category.name).isOpen = flag
    }

    fun resetElemments() {
        focus = true
        elements.forEach { !it.isOpen }
    }

    fun close() {
        elements.forEach { it.close() }
    }

    fun category(name: String, body: (CategoryData.() -> Unit)? = null): CategoryData {
        val cat = CategoryData(CategoryEnum.getOrAdd(name, this.category), this)
        if (body != null) cat.body()
        return cat
    }

    operator fun IElement.unaryPlus() {
        this@CategoryData += this
    }

    fun categoryIcon(icon: IIcon) {
        categoryElement = SlotData(MenuDefEnum.ICON_BUTTON, icon, this.category.name, "sao.element.${this.category.name.toLowerCase()}", ElementDefEnum.CATEGORY)
    }

    fun categoryIconLabel(icon: IIcon) {
        categoryElement = SlotData(MenuDefEnum.ICON_LABEL_BUTTON, icon, this.category.name, "sao.element.${this.category.name.toLowerCase()}", ElementDefEnum.CATEGORY)
    }
}

fun category(name: String, body: (CategoryData.() -> Unit)? = null): CategoryData {
    val cat = CategoryData(CategoryEnum.getOrAdd(name, null), null)
    if (body != null) cat.body()
    return cat
}
