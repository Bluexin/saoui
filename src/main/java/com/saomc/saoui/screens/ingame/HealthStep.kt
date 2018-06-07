package com.saomc.saoui.screens.ingame

import com.saomc.saoui.GLCore
import com.saomc.saoui.social.StaticPlayerHelper
import net.minecraft.client.Minecraft
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

@SideOnly(Side.CLIENT)
enum class HealthStep constructor(private val limit: Float, var color: Int) { // Could be loaded from file. Currently the HPBar loads it's own colors.

    VERY_LOW(0.1f, 0xBD0000FF.toInt()),
    LOW(0.2f, 0xF40000FF.toInt()),
    VERY_DAMAGED(0.3f, 0xF47800FF.toInt()),
    DAMAGED(0.4f, 0xF4BD00FF.toInt()),
    OKAY(0.5f, 0xEDEB38FF.toInt()),
    GOOD(1.0f, 0x93F43EFF.toInt()),
    CREATIVE(-1.0f, 0xB32DE3FF.toInt());

    private operator fun next(): HealthStep {
        return values()[ordinal + 1]
    }

    fun glColor() {
        GLCore.glColorRGBA(color)
    }

    companion object {

        fun getStep(mc: Minecraft, entity: EntityLivingBase, time: Float): HealthStep {
            return getStep(entity, (StaticPlayerHelper.getHealth(mc, entity, time) / StaticPlayerHelper.getMaxHealth(entity)).toDouble())
        }

        fun getStep(entity: EntityLivingBase, health: Double): HealthStep {
            return if (entity is EntityPlayer && (entity.capabilities.isCreativeMode || entity.isSpectator)) CREATIVE else getStep(health)
        }

        fun getStep(health: Double): HealthStep {
            var step = first()
            while (health > step.limit && step.ordinal + 1 < values().size) step = step.next()
            return if (step == CREATIVE) GOOD else step
        }

        private fun first(): HealthStep {
            return values()[0]
        }
    }

}
