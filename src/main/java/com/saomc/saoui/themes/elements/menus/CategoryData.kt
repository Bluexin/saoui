package com.saomc.saoui.themes.elements.menus

import com.saomc.saoui.api.elements.CategoryEnum
import com.saomc.saoui.api.elements.MenuDefEnum
import com.saomc.saoui.api.events.ElementAction
import com.saomc.saoui.api.screens.Actions
import com.saomc.saoui.api.screens.IIcon
import net.minecraft.client.Minecraft
import net.minecraftforge.common.MinecraftForge


data class CategoryData(val category: CategoryEnum, val parentCategory: CategoryData?) {

    private var elements = mutableListOf<ElementData>()
    private lateinit var parent: MenuElementParent
    var categoryElement: ElementData? = null
    //If the elements should render
    private var enabled: Boolean = false
    //If the elements are selectable or locked
    private var focus: Boolean = false
    //The y value each element will have from the next. Needs to be moved to xml
    private var yIncrement = 24
    //The x value each element will have from the parent element. Needs to be moved to xml
    private var xIncrement = 16

    fun actionPerformed(element: ElementData, action: Actions, data: Int, menutElement: MenuElementParent){
        MinecraftForge.EVENT_BUS.post(ElementAction(element.name, action, data, element.isOpen, !focus, menutElement, element.isCategory))
    }

    fun addElement(type: MenuDefEnum, icon: IIcon, name: String, displayName: String, isCategory: Boolean){
        val data: ElementData = ElementData(type, icon, name, displayName, isCategory, this)
        elements.add(data)
    }

    fun draw(mc: Minecraft, mouseX: Int, mouseY: Int) {
        if (enabled)
            elements.forEach { it.draw(mc, mouseX, mouseY) }
    }

    fun init(parent: MenuElementParent) {
        this.parent = parent
        this.enabled = category == CategoryEnum.MAIN
        this.focus = enabled
        elements.forEach { it.init(parent) }
        if (this.parentCategory != null) {
            this.categoryElement = parentCategory.getParentElement(category.name)
        }
    }

    fun isFocus(): Boolean{
        return focus
    }

    fun getX(): Int{
        return (categoryElement?.getWidth()?: 20).plus(parentCategory?.getX()?: 0).plus(xIncrement)
    }

    fun getY(element: ElementData): Int{
        if (categoryElement != null)
            return (parentCategory?.getY(categoryElement!!)?: 0).plus((elements.indexOf(element)).times(yIncrement))
        else return (elements.indexOf(element)).times(yIncrement)
    }

    fun getParentElement(name: String): ElementData{
        return elements.find{ it.name.equals(name, true) }!!
    }

    fun mouseClicked(cursorX: Int, cursorY: Int, actions: Actions): Boolean {
        if (enabled) {
            val element: ElementData? = elements.firstOrNull { it.mouseOver(cursorX, cursorY) }
            if (element != null) {
                actionPerformed(element, actions, 0, parent)
                return true
            }
            return false
        }
        return false
    }

    /**
     * Called when the menu is just opening or closing
     */
    fun setEnabled(flag: Boolean){
        if (flag || focus && !flag) {
            this.enabled = flag
            parentCategory?.setOpen(category, flag)
            resetElemments()
        }
    }

    fun setOpen(category: CategoryEnum, flag: Boolean){
        focus = !flag
        getParentElement(category.name).isOpen = flag
    }

    fun resetElemments(){
        focus = true
        elements.forEach { it.isOpen == false }
    }

    fun close(){
        elements.forEach { it.close() }
        Unit
    }
}