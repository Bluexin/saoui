package com.saomc.saoui.util

import net.minecraft.advancements.Advancement
import net.minecraft.advancements.AdvancementProgress
import net.minecraft.advancements.AdvancementRewards
import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.ClientAdvancementManager
import net.minecraft.client.resources.FallbackResourceManager
import net.minecraft.client.resources.IResourcePack
import net.minecraft.client.resources.SimpleReloadableResourceManager
import net.minecraft.entity.ai.EntityAINearestAttackableTarget
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.ReflectionHelper
import java.lang.reflect.Field

object ReflectHelper {
    val advancementToProgress: Field = ReflectionHelper.findField(ClientAdvancementManager::class.java, "advancementToProgress", "field_192803_d")
    val advancementList: Field = ReflectionHelper.findField(ClientAdvancementManager::class.java, "advancementList", "field_192802_c")
    val recipes: Field = ReflectionHelper.findField(AdvancementRewards::class.java, "recipes", "field_192117_d")
    val defaultResourcePacks: Field = ReflectionHelper.findField(Minecraft::class.java, "defaultResourcePacks", "field_110449_ao")
    val domainResourceManagers: Field = ReflectionHelper.findField(SimpleReloadableResourceManager::class.java, "domainResourceManagers", "field_110548_a")
    val targetClass: Field = ReflectionHelper.findField(EntityAINearestAttackableTarget::class.java, "targetClass", "field_75307_b")

    init {
        advancementToProgress.isAccessible = true
        advancementList.isAccessible = true
        recipes.isAccessible = true
        defaultResourcePacks.isAccessible = true
        domainResourceManagers.isAccessible = true
    }
}

@Suppress("UNCHECKED_CAST")
fun ClientAdvancementManager.getAdvancementToProgress() = ReflectHelper.advancementToProgress.get(this) as Map<Advancement, AdvancementProgress>

@Suppress("UNCHECKED_CAST")
fun AdvancementRewards.getRecipes() = ReflectHelper.recipes.get(this) as Array<ResourceLocation>?

@Suppress("UNCHECKED_CAST")
fun Minecraft.getDefaultResourcePacks() = ReflectHelper.defaultResourcePacks.get(this) as MutableList<IResourcePack>

@Suppress("UNCHECKED_CAST")
fun SimpleReloadableResourceManager.getDomainResourceManagers() = ReflectHelper.domainResourceManagers.get(this) as Map<String, FallbackResourceManager>

@Suppress("UNCHECKED_CAST")
fun EntityAINearestAttackableTarget<*>.getAttackClass() = ReflectHelper.targetClass.get(this) as Class<*>