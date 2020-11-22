package com.saomc.saoui.elements.custom

import com.saomc.saoui.elements.IElement
import com.saomc.saoui.elements.IconLabelElement
import com.saomc.saoui.elements.gui.PopupCraft
import com.saomc.saoui.screens.util.toIcon
import com.saomc.saoui.util.CraftingUtil
import com.saomc.saoui.util.IconCore
import net.minecraft.client.resources.I18n
import net.minecraft.creativetab.CreativeTabs

class CraftingElement(override var parent: IElement?) : IconLabelElement(IconCore.CRAFTING, I18n.format("guiCrafting")) {


    val EMPTY = object : IconLabelElement(icon = IconCore.NONE, label = I18n.format("gui.empty")) {
        private var mark = false

        override var valid: Boolean
            get() {
                if (mark) return false
                mark = true
                val r = validElementsSequence.none()
                mark = false
                return r
            }
            set(_) {}

        override var disabled: Boolean
            get() = true
            set(_) {}
    }

    override fun open(reInit: Boolean) {
        super.open(reInit)
        if (CraftingUtil.updateItemHelper())
            initRecipes()
    }

    override fun update() {
        if (CraftingUtil.updateItemHelper())
            initRecipes()
        super.update()
    }

    fun initRecipes() {
        if (this.highlighted) {
            this.parent?.close(true)
            this.parent?.open(true)
        }
        elements.clear()
        CraftingUtil.getCategories().forEach { tab ->
            if (CraftingUtil.anyValidRecipes(tab)) {
                +IconLabelElement(tab.icon.toIcon(), getTabName(tab), this) {
                    CraftingUtil.getRecipes(tab).forEach { recipe ->
                        +IconLabelElement(recipe.recipeOutput.toIcon(), recipe.recipeOutput.displayName, this).onClick { _, _ ->
                            controllingGUI?.openGui(
                                    PopupCraft(recipe)
                            )?.plusAssign {
                                initRecipes()
                            }
                            true
                        }
                    }
                }
            }
        }
        if (validElementsSequence.none()) +EMPTY
        if (!this.highlighted) elements.forEach { it.hide() }
    }

    fun getTabName(tab: CreativeTabs): String {
        val name = tab.tabLabel.trim()
        if (name.isEmpty())
            return ""
        val newText = StringBuilder(name.length * 2)

        for (i in name.indices) {
            if (i != 0 && name[i].isUpperCase() && name[i - 1] != ' ')
                newText.append(' ')
            newText.append(name[i])
        }
        return newText.toString().capitalize()
    }
}