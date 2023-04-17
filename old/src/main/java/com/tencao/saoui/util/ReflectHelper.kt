package be.bluexin.mcui.util

import net.minecraft.advancements.Advancement
import net.minecraft.advancements.AdvancementProgress
import net.minecraft.advancements.AdvancementRewards
import net.minecraft.Client.mc
import net.minecraft.client.multiplayer.ClientAdvancementManager
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.client.resources.FallbackResourceManager
import net.minecraft.client.resources.IResourcePack
import net.minecraft.client.resources.SimpleReloadableResourceManager
import net.minecraft.entity.ai.EntityAINearestAttackableTarget
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Timer
import net.minecraftforge.fml.common.ObfuscationReflectionHelper
import java.lang.reflect.Field

object ReflectHelper {
    val advancementToProgress: Field = ObfuscationReflectionHelper.findField(ClientAdvancementManager::class.java, "field_192803_d")
    val advancementList: Field = ObfuscationReflectionHelper.findField(ClientAdvancementManager::class.java, "field_192802_c")
    val recipes: Field = ObfuscationReflectionHelper.findField(AdvancementRewards::class.java, "field_192117_d")
    val defaultResourcePacks: Field = ObfuscationReflectionHelper.findField(Minecraft::class.java, "field_110449_ao")
    val domainResourceManagers: Field = ObfuscationReflectionHelper.findField(SimpleReloadableResourceManager::class.java, "field_110548_a")
    val targetClass: Field = ObfuscationReflectionHelper.findField(EntityAINearestAttackableTarget::class.java, "field_75307_b")
    val renderPartialTicksPaused: Field = ObfuscationReflectionHelper.findField(Minecraft::class.java, "field_193996_ah")
    val timer: Field = ObfuscationReflectionHelper.findField(Minecraft::class.java, "field_71428_T")
    val renderPosX: Field = ObfuscationReflectionHelper.findField(RenderManager::class.java, "field_78725_b")
    val renderPosY: Field = ObfuscationReflectionHelper.findField(RenderManager::class.java, "field_78726_c")
    val renderPosZ: Field = ObfuscationReflectionHelper.findField(RenderManager::class.java, "field_78723_d")

    init {
        advancementToProgress.isAccessible = true
        advancementList.isAccessible = true
        recipes.isAccessible = true
        defaultResourcePacks.isAccessible = true
        domainResourceManagers.isAccessible = true
        renderPartialTicksPaused.isAccessible = true
        timer.isAccessible = true
        renderPosX.isAccessible = true
        renderPosY.isAccessible = true
        renderPosZ.isAccessible = true
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

fun Minecraft.getRenderPartialTicksPaused() = ReflectHelper.renderPartialTicksPaused.get(this) as Float
fun Minecraft.getTimer() = ReflectHelper.timer.get(this) as Timer

val RenderManager.renderPosX
    get() = ReflectHelper.renderPosX.get(this) as Double
val RenderManager.renderPosY
    get() = ReflectHelper.renderPosY.get(this) as Double
val RenderManager.renderPosZ
    get() = ReflectHelper.renderPosZ.get(this) as Double
