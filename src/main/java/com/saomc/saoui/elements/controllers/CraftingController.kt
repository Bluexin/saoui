package com.saomc.saoui.elements.controllers

import com.saomc.saoui.elements.IconLabelElement
import com.saomc.saoui.screens.CoreGUI
import com.saomc.saoui.screens.util.PopupCraft
import com.saomc.saoui.screens.util.toIcon
import com.saomc.saoui.util.CraftingUtil
import com.saomc.saoui.util.IconCore
import net.minecraft.client.resources.I18n
import net.minecraft.creativetab.CreativeTabs

class CraftingController(controller: IController): Controller(delegate = IconLabelElement(icon = IconCore.CRAFTING, label = I18n.format("guiCrafting"), controller = controller), controllingParent = controller) {

    var initCheck: Boolean = false

    override fun update() {
        if (!initCheck) {
            CraftingUtil.updateItemHelper(true)
            initRecipes()
            initCheck = true
            /*
            BlockPos.getAllInBox(Minecraft().player.position.add(-3, -3, -3), Minecraft().player.position.add(3, 3, 3)).forEach {
                val state = Minecraft().world.getBlockState(it)
                BlockWorkbench.
                state.block.onBlockActivated(Minecraft().world, it, state, Minecraft().player, EnumHand.MAIN_HAND, EnumFacing.UP, 0f, 0f, 0f)
            }*/
        }
        if (CraftingUtil.updateItemHelper())
            initRecipes()
        super.update()
    }

    fun initRecipes() {
        if (this.highlighted) {
            controllingParent.reInit()
        }
        elements.clear()
        CraftingUtil.getCategories().forEach { tab ->
            if (CraftingUtil.anyValidRecipes(tab)) {
                Controller(IconLabelElement(tab.icon.toIcon(), getTabName(tab), this),this).let {
                    CraftingUtil.getRecipes(tab).forEach { recipe ->
                        +IconLabelElement(icon = recipe.recipeOutput.toIcon(), controller = it, label = recipe.recipeOutput.displayName, function = {
                            (tlController as? CoreGUI<*>)?.openGui(
                                    PopupCraft(recipe)
                            )
                        })
                    }
                }
            }
        }
        if (!this.highlighted) elements.forEach { it.hide() }
    }

    fun getTabName(tab: CreativeTabs): String{
        val name = tab.tabLabel.trim()
        if (name.isEmpty())
            return ""
        val newText = StringBuilder(name.length * 2)

        for (i in name.indices){
            if (i != 0 && name[i].isUpperCase() && name[i - 1] != ' ')
                newText.append(' ')
            newText.append(name[i])
        }
        return newText.toString().capitalize()
    }
}