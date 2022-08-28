/*
 * Copyright (C) 2016-2019 Arnaud 'Bluexin' Solé
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.tencao.saoui.capabilities

import com.tencao.saomclib.capabilities.AbstractCapability
import com.tencao.saomclib.capabilities.AbstractEntityCapability
import com.tencao.saomclib.capabilities.Key
import com.tencao.saomclib.getOrNull
import com.tencao.saoui.SAOCore
import com.tencao.saoui.api.entity.rendering.*
import com.tencao.saoui.api.events.ColorStateEvent
import com.tencao.saoui.util.getDebugFPS
import net.minecraft.client.Minecraft
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.CompoundNBT
import net.minecraft.nbt.INBT
import net.minecraft.util.Direction
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.CapabilityInject
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * Part of saoui
 *
 *
 * This [Capability] contains info for the SAOUI about color states, the amount of HP bars to render, etc.
 * It also contains info like custom offsets for hp bars to fix some renders (lookin at ya chicken).
 *
 * @author Bluexin
 */

class RenderCapability : AbstractEntityCapability() {

    /**
     * Where this capability is getting it's customization settings from.
     */
    lateinit var customizationProvider: ICustomizationProvider

    /**
     * Where this capability is getting it's Color State data from.
     */
    lateinit var colorStateHandler: IColorStateHandler

    var healthSmooth: Float = -1f
        get() {
            return if (field == -1f) {
                theEnt.health
            } else field
        }

    val isAggressive: Boolean
        get() = colorStateHandler.colorState == ColorState.KILLER

    fun update(partialTicks: Float) {
        updateHealthSmooth(partialTicks)
        colorStateHandler.tick()
    }

    fun updateHealthSmooth(partialTicks: Float) {
        if (theEnt is PlayerEntity) {
            Minecraft.getInstance().profiler.startSection("updateHealthSmooth")
            when {
                theEnt.health == theEnt.maxHealth -> healthSmooth = theEnt.maxHealth
                theEnt.health <= 0 -> {
                    val value = (18 - theEnt.deathTime).toFloat() / 18
                    healthSmooth = max(0.0f, theEnt.health * value)
                }
                (healthSmooth * 10).roundToInt() != (theEnt.health * 10).roundToInt() -> healthSmooth += (theEnt.health - healthSmooth) * (gameTimeDelay(partialTicks) * HEALTH_ANIMATION_FACTOR)
                else -> healthSmooth = theEnt.health
            }
            healthSmooth = max(0.0f, healthSmooth)
            Minecraft.getInstance().profiler.endSection()
        } else healthSmooth = -1f
    }

    private fun gameTimeDelay(time: Float): Float {
        return if (time >= 0f) time else HEALTH_FRAME_FACTOR / gameFPS()
    }

    private fun gameFPS(): Int {
        return Minecraft.getInstance().getDebugFPS()
    }

    /**
     * The entity this capability refers to.
     */
    lateinit var theEnt: LivingEntity

    override fun setup(param: Any): AbstractCapability {
        super.setup(param)
        theEnt = param as LivingEntity

        val colorStateEvent = ColorStateEvent(theEnt)
        MinecraftForge.EVENT_BUS.post(colorStateEvent)
        customizationProvider = colorStateEvent.provider ?: getProvider()
        colorStateHandler = colorStateEvent.state ?: getColorState()
        return this
    }

    private fun getProvider(): ICustomizationProvider {
        return if (theEnt is ICustomizableEntity) (theEnt as ICustomizableEntity).provider else StaticCustomizationProvider(
            0.0,
            0.0,
            0.0,
            1.0,
            1
        )
    }

    private fun getColorState(): IColorStateHandler {
        return (theEnt as? IColorStatedEntity)?.colorState ?: (theEnt as? PlayerEntity)?.let { PlayerColorStateHandler(it) } ?: MobColorStateHandler(theEnt)
    }

    class Storage : Capability.IStorage<RenderCapability> {

        override fun writeNBT(capability: Capability<RenderCapability>?, instance: RenderCapability, side: Direction?): INBT {
            val tag = CompoundNBT()
            instance.customizationProvider.save(tag)
            instance.colorStateHandler.save(tag)
            return tag
        }

        override fun readNBT(capability: Capability<RenderCapability>?, instance: RenderCapability, side: Direction?, nbt: INBT) {
            instance.customizationProvider.load(nbt as CompoundNBT)
            instance.colorStateHandler.load(nbt)
        }
    }

    override val shouldSyncOnDeath = true

    override val shouldSyncOnDimensionChange = true

    override val shouldRestoreOnDeath = true

    override val shouldSendOnLogin = true

    companion object {
        @Key
        val KEY = ResourceLocation(SAOCore.MODID, "renders")

        /**
         * Unique instance for the capability (for registering).
         * Use [net.minecraft.entity.Entity.hasCapability] to know if an entity has this capability
         * and [net.minecraft.entity.Entity.getCapability] to get the actual capability instance.
         */
        @CapabilityInject(RenderCapability::class)
        lateinit var RENDER_CAPABILITY: Capability<RenderCapability>

        /**
         * Sync the client player.
         * Not yet sure how to sync everything properly.
         *
         * @param player player to sync
         */
        fun syncClient(player: PlayerEntity) { // TODO: implement
            //        PacketPipeline.sendTo(new SyncExtStats(player), (EntityPlayerMP) player);
        }

        private const val HEALTH_ANIMATION_FACTOR = 0.075f
        private const val HEALTH_FRAME_FACTOR = HEALTH_ANIMATION_FACTOR * HEALTH_ANIMATION_FACTOR * 0x40f * 0x64f
    }
}

/**
 * Gets the capability for an entity.
 *
 * @param ent the entity to get the capability for
 * @return the capability
 */
fun LivingEntity.getRenderData() = this.getCapability(RenderCapability.RENDER_CAPABILITY, null).getOrNull()
