package com.saomc.saoui.themes.elements.menus

import com.saomc.saoui.api.events.ElementAction
import com.saomc.saoui.api.screens.Actions
import net.minecraft.client.Minecraft
import net.minecraftforge.common.MinecraftForge


data class CategoryData(val name: String, val parentCategory: CategoryData?) {

    private var elements = mutableListOf<ElementData>()
    private lateinit var parent: MenuElementParent
    private var x = 0
    private var y = 0
    private var enabled: Boolean = false
    //The y value each element will have from the next. Needs to be moved to xml
    private var yIncrement = 24
    //The x value each element will have from the parent element. Needs to be moved to xml
    private var xIncrement = 20

    fun actionPerformed(element: ElementData, action: Actions, data: Int, menutElement: MenuElementParent){
        MinecraftForge.EVENT_BUS.post(ElementAction(element.name, action, data, element.isOpen, !element.focus, menutElement))
    }

    fun addElement(data: ElementData){
        data.setY(elements.size * yIncrement)
        elements.add(data)
    }

    fun draw(mc: Minecraft, mouseX: Int, mouseY: Int) {
        if (enabled)
            elements.forEach { it.draw(mc, mouseX, mouseY) }
    }

    fun init(parent: MenuElementParent) {
        this.parent = parent
        this.enabled = name.equals("menu", true)
        elements.forEach { it.init(parent) }
        if (this.parentCategory != null) {
            this.x = parentCategory.x.plus(xIncrement)
            this.y = parentCategory.getParentElement(name).getY()
        }
    }

    fun getParentElement(name: String): ElementData{
        return elements.first{ it.name.equals(name, true) }
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

    fun parentOf(name: String): Boolean{
        return elements.firstOrNull{ it.name.equals(name, true) } != null
    }

    /**
     * Called when the menu is just opening
     */
    fun setEnabled(flag: Boolean){
        this.enabled = flag
        resetElemments()
    }

    fun setOpen(name: String, flag: Boolean){
        getParentElement(name).isOpen = flag
        elements.forEach { it.focus = !flag }
    }

    fun resetElemments(){
        elements.forEach { it.focus = true && it.isOpen == false }
    }

    fun close(){
        elements.forEach { it.close() }
        Unit
    }
}