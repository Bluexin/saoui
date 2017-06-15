package com.saomc.saoui.themes.elements.menus

import com.saomc.saoui.GLCore
import com.saomc.saoui.SAOCore
import com.saomc.saoui.api.elements.*
import com.saomc.saoui.api.events.ElementAction
import com.saomc.saoui.api.screens.Actions
import com.saomc.saoui.api.screens.IIcon
import net.minecraft.client.Minecraft
import net.minecraft.client.resources.I18n
import net.minecraftforge.common.MinecraftForge
import com.saomc.saoui.resources.StringNames
import com.saomc.saoui.config.OptionCore
import com.saomc.saoui.util.ColorUtil


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

    fun actionPerformed(element: IElement, action: Actions, data: Int, menutElement: MenuElementParent){
        MinecraftForge.EVENT_BUS.post(ElementAction(element.name, action, data, element.isOpen, !focus, menutElement, element.elementType))
    }

    fun addSlot(type: MenuDefEnum, icon: IIcon, name: String, displayName: String, elementType: ElementDefEnum){
        val data: SlotData = SlotData(type, icon, name, I18n.format(displayName), elementType)
        elements.add(data)
        setWidth(data.displayName)
    }

    fun draw(mc: Minecraft, mouseX: Int, mouseY: Int) {
        if (enabled) {
            elements.forEach { it.draw(mc, mouseX, mouseY) }
            if (parentCategory != null){
                var y = getY(elements.first()) + parent.parentY
                val length = getLineLength()
                SAOCore.LOGGER.info("Length: " + length)
                val color0: Int = if (focus) ColorUtil.DEFAULT_COLOR.rgba else ColorUtil.DEFAULT_COLOR.rgba and ColorUtil.DISABLED_MASK.rgba
                GLCore.glBlend(true)
                GLCore.glBindTexture(if (OptionCore.SAO_UI.isEnabled) StringNames.gui else StringNames.guiCustom)

                GLCore.glColorRGBA(ColorUtil.multiplyAlpha(color0, 1.0F))

                GLCore.glTexturedRect(getX() + parent.parentX - 2.0, y.toDouble(), 2.0, length.toDouble(), 40.0, 41.0, 1.0, 25.0)
                y = parentCategory.getY(categoryElement!!) + (categoryElement!!.height / 2 - 5) + parent.parentY
                GLCore.glTexturedRect(getX() + parent.parentX - 10.0, y.toDouble(), 20.0, 25.0, 10.0, 10.0)
                GLCore.glBlend(false)


            }
        }
    }

    fun init(parent: MenuElementParent) {
        this.parent = parent
        this.enabled = category == CategoryEnum.MAIN
        this.focus = enabled
        elements.forEach { it.init(parent, this) }
        if (this.parentCategory != null) {
            this.categoryElement = parentCategory.getParentElement(category.name)
        }
    }

    fun isFocus(): Boolean{
        return focus
    }

    fun setWidth(name: String){
        val newWidth = 40 + (GLCore.glStringWidth(if (name.length > 50) name.substring(0, 50) else name))
        if (newWidth > width) width = newWidth
    }

    fun getX(): Int{
        return (categoryElement?.width?: 20).plus(parentCategory?.getX()?: 0).plus(xIncrement)
    }

    fun getY(element: IElement): Int{
        if (element.isMenu()) {
            return (parentCategory?.getY(categoryElement!!) ?: 0).plus(elements.filter { it.isMenu() }.indexOf(element).times(yIncrement)).plus(getYOffset())
        } else return parentCategory?.getY(categoryElement!!) ?: 0
    }

    fun getLineLength(): Int{
        var length: Int
        if (elements.filter { it.isMenu() }.size > 5)
            length = elements.filter { it.isMenu() }.subList(0, 4).sumBy { it.height }
        else
            length = elements.filter { it.isMenu() }.sumBy { it.height }
        when (elements.filter { it.isMenu() }.size){
            1 -> return length
            2 -> return yIncrement + length
            3 -> return yIncrement * 2 + length
            4 -> return yIncrement * 3 + length
            else -> return yIncrement * 4 + length
        }
    }

    fun getYOffset(): Int{
        if (category == CategoryEnum.MAIN) return 0
        when (elements.filter { it.isMenu() }.size){
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
        return elements.find{ it.name.equals(name, true) }!!
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
        elements.forEach { !it.isOpen }
    }

    fun close(){
        elements.forEach { it.close() }
        Unit
    }
}