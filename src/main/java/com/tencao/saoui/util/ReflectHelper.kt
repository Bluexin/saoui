package com.tencao.saoui.util

import net.minecraft.advancements.AdvancementRewards
import net.minecraft.client.Minecraft
import net.minecraft.client.MouseHelper
import net.minecraft.client.gui.IngameGui
import net.minecraft.client.gui.fonts.FontResourceManager
import net.minecraft.util.ResourceLocation
import net.minecraft.util.Timer
import net.minecraft.util.math.vector.Matrix3f
import net.minecraftforge.fml.common.ObfuscationReflectionHelper
import java.lang.reflect.Field

object ReflectHelper {
    // val advancementToProgress: Field = ObfuscationReflectionHelper.findField(ClientAdvancementManager::class.java, "field_192803_d")
    // val advancementList: Field = ObfuscationReflectionHelper.findField(ClientAdvancementManager::class.java,"field_192802_c")
    val recipes: Field = ObfuscationReflectionHelper.findField(AdvancementRewards::class.java, "field_192117_d")

    // val defaultResourcePacks: Field = ObfuscationReflectionHelper.findField(Minecraft::class.java, "field_110449_ao")
    // val domainResourceManagers: Field = ObfuscationReflectionHelper.findField(SimpleReloadableResourceManager::class.java, "field_110548_a")
    // val targetClass: Field = ObfuscationReflectionHelper.findField(EntityAINearestAttackableTarget::class.java, "field_75307_b")
    val debugFPS: Field = ObfuscationReflectionHelper.findField(Minecraft::class.java, "field_71470_ab")
    val renderPartialTicksPaused: Field = ObfuscationReflectionHelper.findField(Minecraft::class.java, "field_193996_ah")
    val timer: Field = ObfuscationReflectionHelper.findField(Minecraft::class.java, "field_71428_T")
    val ingameGUI: Field = ObfuscationReflectionHelper.findField(Minecraft::class.java, "field_71456_v")

    // val stateForPlacement: Method = ObfuscationReflectionHelper.findMethod(BlockItem::class.java, "func_195945_b")
    val m00: Field = ObfuscationReflectionHelper.findField(Matrix3f::class.java, "field_226097_a_")
    val m01: Field = ObfuscationReflectionHelper.findField(Matrix3f::class.java, "field_226098_b_")
    val m02: Field = ObfuscationReflectionHelper.findField(Matrix3f::class.java, "field_226099_c_")
    val m10: Field = ObfuscationReflectionHelper.findField(Matrix3f::class.java, "field_226100_d_")
    val m11: Field = ObfuscationReflectionHelper.findField(Matrix3f::class.java, "field_226101_e_")
    val m12: Field = ObfuscationReflectionHelper.findField(Matrix3f::class.java, "field_226102_f_")
    val m20: Field = ObfuscationReflectionHelper.findField(Matrix3f::class.java, "field_226103_g_")
    val m21: Field = ObfuscationReflectionHelper.findField(Matrix3f::class.java, "field_226104_h_")
    val m22: Field = ObfuscationReflectionHelper.findField(Matrix3f::class.java, "field_226105_i_")

    val mouseGrabbed: Field = ObfuscationReflectionHelper.findField(MouseHelper::class.java, "field_198051_p")
    val fontResourceMananger: Field = ObfuscationReflectionHelper.findField(Minecraft::class.java, "field_211501_aD")

    init {
        // advancementToProgress.isAccessible = true
        // advancementList.isAccessible = true
        recipes.isAccessible = true
        // defaultResourcePacks.isAccessible = true
        // domainResourceManagers.isAccessible = true
        debugFPS.isAccessible = true
        renderPartialTicksPaused.isAccessible = true
        timer.isAccessible = true
        ingameGUI.isAccessible = true
        // stateForPlacement.isAccessible = true
        m00.isAccessible = true
        m01.isAccessible = true
        m02.isAccessible = true
        m10.isAccessible = true
        m11.isAccessible = true
        m12.isAccessible = true
        m20.isAccessible = true
        m21.isAccessible = true
        m22.isAccessible = true
        mouseGrabbed.isAccessible = true
        fontResourceMananger.isAccessible = true
    }
}

// @Suppress("UNCHECKED_CAST")
// fun ClientAdvancementManager.getAdvancementToProgress() = ReflectHelper.advancementToProgress.get(this) as Map<Advancement, AdvancementProgress>

@Suppress("UNCHECKED_CAST")
fun AdvancementRewards.getRecipes() = ReflectHelper.recipes.get(this) as Array<ResourceLocation>?

// @Suppress("UNCHECKED_CAST")
// fun Minecraft.getDefaultResourcePacks() = ReflectHelper.defaultResourcePacks.get(this) as MutableList<IResourcePack>

// @Suppress("UNCHECKED_CAST")
// fun SimpleReloadableResourceManager.getDomainResourceManagers() = ReflectHelper.domainResourceManagers.get(this) as Map<String, FallbackResourceManager>

// @Suppress("UNCHECKED_CAST")
// fun EntityAINearestAttackableTarget<*>.getAttackClass() = ReflectHelper.targetClass.get(this) as Class<*>

@Suppress("UNCHECKED_CAST")
fun Minecraft.getDebugFPS() = ReflectHelper.debugFPS.get(this) as Int

fun Minecraft.getRenderPartialTicksPaused() = ReflectHelper.renderPartialTicksPaused.get(this) as Float
fun Minecraft.getTimer() = ReflectHelper.timer.get(this) as Timer

fun Minecraft.getIngameGUI() = ReflectHelper.ingameGUI.get(this) as IngameGui
fun Minecraft.setIngameGUI(gui: IngameGui) = ReflectHelper.ingameGUI.set(this, gui)
val Minecraft.fontResourceMananger: FontResourceManager
    get() = ReflectHelper.fontResourceMananger.get(this) as FontResourceManager

// fun BlockItem.getStateForPlacement(context: BlockItemUseContext) = ReflectHelper.stateForPlacement.invoke(this, context) as BlockState?

val Matrix3f.m00: Float
    get() = ReflectHelper.m00.get(this) as Float
val Matrix3f.m01: Float
    get() = ReflectHelper.m01.get(this) as Float
val Matrix3f.m02: Float
    get() = ReflectHelper.m02.get(this) as Float
val Matrix3f.m10: Float
    get() = ReflectHelper.m10.get(this) as Float
val Matrix3f.m11: Float
    get() = ReflectHelper.m11.get(this) as Float
val Matrix3f.m12: Float
    get() = ReflectHelper.m12.get(this) as Float
val Matrix3f.m20: Float
    get() = ReflectHelper.m20.get(this) as Float
val Matrix3f.m21: Float
    get() = ReflectHelper.m21.get(this) as Float
val Matrix3f.m22: Float
    get() = ReflectHelper.m22.get(this) as Float

var MouseHelper.mouseGrabbed: Boolean
    get() = ReflectHelper.mouseGrabbed.get(this) as Boolean
    set(value) = ReflectHelper.mouseGrabbed.set(this, value)
