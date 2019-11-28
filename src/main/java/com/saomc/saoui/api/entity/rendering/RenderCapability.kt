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

package com.saomc.saoui.api.entity.rendering

import be.bluexin.saomclib.capabilities.AbstractCapability
import be.bluexin.saomclib.capabilities.AbstractEntityCapability
import be.bluexin.saomclib.capabilities.Key
import com.saomc.saoui.SAOCore
import com.teamwizardry.librarianlib.features.kotlin.Minecraft
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTBase
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.CapabilityInject
import kotlin.math.max
import kotlin.math.roundToLong

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
     private lateinit var customizationProvider: ICustomizationProvider


    /**
     * Where this capability is getting it's Color State data from.
     */
    private lateinit var colorStateHandler: IColorStateHandler

    var healthSmooth: Float = 0f

    fun update(partialTicks: Float){
        updateHealthSmooth(partialTicks)
    }

    fun updateHealthSmooth(partialTicks: Float){
        if (theEnt.health == theEnt.maxHealth)
            healthSmooth = theEnt.maxHealth
        else if (theEnt.health <= 0){
            val value = (18 - theEnt.deathTime).toFloat() / 18
            healthSmooth =  max(0.0f, theEnt.health * value)
        }else if ((healthSmooth * 10).roundToLong() != (theEnt.health * 10).roundToLong())
            healthSmooth += (theEnt.health - healthSmooth) * (gameTimeDelay(partialTicks) * HEALTH_ANIMATION_FACTOR)
        else
            healthSmooth = theEnt.health
        healthSmooth = max(0.0f, healthSmooth)
    }

    private fun gameTimeDelay(time: Float): Float {
        return if (time >= 0f) time else HEALTH_FRAME_FACTOR / gameFPS()
    }

    private fun gameFPS(): Int {
        return Minecraft().limitFramerate
    }

    /**
     * The entity this capability refers to.
     */
    lateinit var theEnt: EntityLivingBase

    override fun setup(param: Any): AbstractCapability {
        super.setup(param)
        theEnt = param as EntityLivingBase
        customizationProvider = getProvider(param)
        colorStateHandler = getColorState(param)
        return this
    }

    private fun getProvider(ent: EntityLivingBase): ICustomizationProvider {
        return if (ent is ICustomizableEntity) (ent as ICustomizableEntity).provider else StaticCustomizationProvider(0.0, 0.0, 0.0, 1.0, 1) // TODO: implement with event or something
    }

    private fun getColorState(ent: EntityLivingBase): IColorStateHandler {
        return if (ent is IColorStatedEntity) (ent as IColorStatedEntity).colorState else (ent as? EntityPlayer)?.let { PlayerColorStateHandler(it) }
                ?: MobColorStateHandler(ent) // TODO: implement with event or something
    }

    fun getCustomizationProvider():ICustomizationProvider { return customizationProvider}

    fun getColorStateHandler():IColorStateHandler { return colorStateHandler}

    class Storage : Capability.IStorage<RenderCapability> {

        override fun writeNBT(capability: Capability<RenderCapability>?, instance: RenderCapability, side: EnumFacing?): NBTBase? {
            val tag = NBTTagCompound()
            instance.customizationProvider.save(tag)
            instance.colorStateHandler.save(tag)
            return tag
        }

        override fun readNBT(capability: Capability<RenderCapability>?, instance: RenderCapability, side: EnumFacing?, nbt: NBTBase) {
            instance.customizationProvider.load(nbt as NBTTagCompound)
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
        fun syncClient(player: EntityPlayer) {// TODO: implement
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
fun EntityLivingBase.getRenderData() = this.getCapability(RenderCapability.RENDER_CAPABILITY, null)