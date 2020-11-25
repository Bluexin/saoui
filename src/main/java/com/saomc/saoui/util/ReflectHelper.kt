package com.saomc.saoui.util

import net.minecraft.advancements.Advancement
import net.minecraft.advancements.AdvancementList
import net.minecraft.advancements.AdvancementProgress
import net.minecraft.advancements.AdvancementRewards
import net.minecraft.client.multiplayer.ClientAdvancementManager
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.ReflectionHelper
import java.lang.reflect.Field

object ReflectHelper {
    val advancementToProgress: Field = ReflectionHelper.findField(ClientAdvancementManager::class.java, "field_192803_d", "advancementToProgress")
    val advancementList: Field = ReflectionHelper.findField(ClientAdvancementManager::class.java, "field_192802_c", "advancementList")
    val recipes: Field = ReflectionHelper.findField(AdvancementRewards::class.java, "field_192117_d", "recipes")

    init {
        advancementToProgress.isAccessible = true
        advancementList.isAccessible = true
        recipes.isAccessible = true
    }
}

@Suppress("UNCHECKED_CAST")
fun ClientAdvancementManager.getAdvancementToProgress() = ReflectHelper.advancementToProgress.get(this) as Map<Advancement, AdvancementProgress>

@Suppress("UNCHECKED_CAST")
fun ClientAdvancementManager.getAdvancementList() = ReflectHelper.advancementList.get(this) as AdvancementList

@Suppress("UNCHECKED_CAST")
fun AdvancementRewards.getRecipes() = ReflectHelper.recipes.get(this) as Array<ResourceLocation>?