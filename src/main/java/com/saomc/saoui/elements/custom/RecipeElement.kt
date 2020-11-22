package com.saomc.saoui.elements.custom

import com.saomc.saoui.elements.IElement
import com.saomc.saoui.elements.IconLabelElement
import com.saomc.saoui.screens.util.toIcon
import net.minecraft.advancements.Advancement
import net.minecraft.item.crafting.IRecipe

class RecipeElement(val advancement: Advancement, val recipe: IRecipe, override var parent: IElement?) : IconLabelElement(recipe.recipeOutput.toIcon(), recipe.recipeOutput.displayName) {


}