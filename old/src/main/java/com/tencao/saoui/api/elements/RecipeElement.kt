package be.bluexin.mcui.api.elements

import be.bluexin.mcui.screens.util.toIcon
import net.minecraft.advancements.Advancement
import net.minecraft.item.crafting.IRecipe

class RecipeElement(val advancement: Advancement, val recipe: IRecipe) : IconLabelElement(recipe.recipeOutput.toIcon(), recipe.recipeOutput.displayName)
