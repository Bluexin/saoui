package com.saomc.saoui.api.elements

import com.saomc.saoui.screens.CoreGUI
import com.saomc.saoui.screens.ItemIcon
import com.saomc.saoui.screens.util.PopupCraft
import com.saomc.saoui.screens.util.toIcon
import com.saomc.saoui.util.CraftingUtil
import com.saomc.saoui.util.IconCore
import net.minecraft.client.resources.I18n
import net.minecraft.creativetab.CreativeTabs

class CraftingElement: IconLabelElement(IconCore.CRAFTING, I18n.format("guiCrafting")) {

    var init: Boolean = false

    override fun update() {
        if (!init) {
            CraftingUtil.updateItemHelper(true)
            initRecipes()
            init = true
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

    fun initRecipes(){
        if (this.highlighted) {
            (this.parent as CategoryButton).close(true)
            (this.parent as CategoryButton).open(true)
        }
        elements.clear()
        CraftingUtil.getCategories().forEach {tab ->
            if (CraftingUtil.anyValidRecipes(tab)) {
                +CategoryButton(IconLabelElement(ItemIcon { tab.icon }, getTabName(tab))) {
                    CraftingUtil.getRecipes(tab).forEach { recipe ->
                        +IconLabelElement(recipe.recipeOutput.toIcon(), recipe.recipeOutput.displayName).onClick { _, _ ->
                            (tlParent as? CoreGUI<*>)?.openGui(
                                    PopupCraft(recipe)
                            )
                            true
                        }
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