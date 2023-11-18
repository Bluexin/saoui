package com.tencao.saoui.util

/*
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
/*
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


var MouseHelper.mouseGrabbed: Boolean
    get() = ReflectHelper.mouseGrabbed.get(this) as Boolean
    set(value) = ReflectHelper.mouseGrabbed.set(this, value)
*/