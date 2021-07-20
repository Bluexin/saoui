package com.saomc.saoui.util

import com.teamwizardry.librarianlib.core.util.Client
import net.minecraft.advancements.Advancement
import net.minecraft.advancements.AdvancementProgress
import net.minecraft.advancements.AdvancementRewards
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.IngameGui
import net.minecraft.client.multiplayer.ClientAdvancementManager
import net.minecraft.resources.IResourcePack
import net.minecraft.resources.SimpleReloadableResourceManager
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.common.ObfuscationReflectionHelper
import java.lang.reflect.Field

object ReflectHelper {
    //val advancementToProgress: Field = ObfuscationReflectionHelper.findField(ClientAdvancementManager::class.java, "field_192803_d")
    //val advancementList: Field = ObfuscationReflectionHelper.findField(ClientAdvancementManager::class.java,"field_192802_c")
    val recipes: Field = ObfuscationReflectionHelper.findField(AdvancementRewards::class.java, "field_192117_d")
    //val defaultResourcePacks: Field = ObfuscationReflectionHelper.findField(Minecraft::class.java, "field_110449_ao")
    //val domainResourceManagers: Field = ObfuscationReflectionHelper.findField(SimpleReloadableResourceManager::class.java, "field_110548_a")
    //val targetClass: Field = ObfuscationReflectionHelper.findField(EntityAINearestAttackableTarget::class.java, "field_75307_b")
    val debugFPS: Field = ObfuscationReflectionHelper.findField(Minecraft::class.java, "field_71470_ab")
    val worldTicks: Field = ObfuscationReflectionHelper.findField(Client::class.java, "worldTicks", )
    val globalTicks: Field = ObfuscationReflectionHelper.findField(Client::class.java, "globalTicks")
    val ingameGUI: Field = ObfuscationReflectionHelper.findField(Minecraft::class.java, "field_71456_v")

    init {
        //advancementToProgress.isAccessible = true
        //advancementList.isAccessible = true
        recipes.isAccessible = true
        //defaultResourcePacks.isAccessible = true
        //domainResourceManagers.isAccessible = true
        debugFPS.isAccessible = true
        worldTicks.isAccessible = true
        globalTicks.isAccessible = true
        ingameGUI.isAccessible = true
    }
}

//@Suppress("UNCHECKED_CAST")
//fun ClientAdvancementManager.getAdvancementToProgress() = ReflectHelper.advancementToProgress.get(this) as Map<Advancement, AdvancementProgress>

@Suppress("UNCHECKED_CAST")
fun AdvancementRewards.getRecipes() = ReflectHelper.recipes.get(this) as Array<ResourceLocation>?

//@Suppress("UNCHECKED_CAST")
//fun Minecraft.getDefaultResourcePacks() = ReflectHelper.defaultResourcePacks.get(this) as MutableList<IResourcePack>

//@Suppress("UNCHECKED_CAST")
//fun SimpleReloadableResourceManager.getDomainResourceManagers() = ReflectHelper.domainResourceManagers.get(this) as Map<String, FallbackResourceManager>

//@Suppress("UNCHECKED_CAST")
//fun EntityAINearestAttackableTarget<*>.getAttackClass() = ReflectHelper.targetClass.get(this) as Class<*>

@Suppress("UNCHECKED_CAST")
fun Minecraft.getDebugFPS() = ReflectHelper.debugFPS.get(this) as Int

fun Client.getWorldTicks() = ReflectHelper.worldTicks.get(this) as Float
fun Client.getGlobalTicks() = ReflectHelper.globalTicks.get(this) as Float


fun Minecraft.getIngameGUI() =  ReflectHelper.ingameGUI.get(this) as IngameGui
fun Minecraft.setIngameGUI(gui: IngameGui) = ReflectHelper.ingameGUI.set(this, gui)