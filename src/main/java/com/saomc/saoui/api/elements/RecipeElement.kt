package com.saomc.saoui.api.elements

import com.saomc.saoui.screens.ItemIcon
import net.minecraft.advancements.Advancement
import net.minecraft.item.crafting.IRecipe

class RecipeElement(val advancement: Advancement, val recipe: IRecipe): IconLabelElement(ItemIcon{recipe.recipeOutput}, recipe.recipeOutput.displayName) {


}