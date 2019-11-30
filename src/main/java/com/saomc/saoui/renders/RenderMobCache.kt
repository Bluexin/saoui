package com.saomc.saoui.renders

import net.minecraft.entity.EntityLivingBase
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.common.registry.EntityRegistry
import java.util.function.Predicate

data class RenderMobCache(val ref: ResourceLocation, val isChild: Boolean, val width: Float, val height: Float): Predicate<EntityLivingBase> {

    constructor(entity: EntityLivingBase): this(EntityRegistry.getEntry(entity.javaClass)!!.registryName!!, entity.isChild, entity.width, entity.height)

    val sizeMult by lazy { if (isChild) 0.5f else 1.0f}

    override fun test(entity: EntityLivingBase): Boolean {
        return EntityRegistry.getEntry(entity.javaClass) == ref
    }

    val xPos by lazy { sizeMult * width * HEALTH_RANGE }
    val yPos by lazy { sizeMult * height * HEALTH_OFFSET }
    val zPos by lazy { sizeMult * width * HEALTH_RANGE }



    companion object{
        private const val HEALTH_COUNT = 32
        private const val HEALTH_RANGE = 0.975
        private const val HEALTH_OFFSET = 0.75f
        private const val HEALTH_HEIGHT = 0.21
    }
}