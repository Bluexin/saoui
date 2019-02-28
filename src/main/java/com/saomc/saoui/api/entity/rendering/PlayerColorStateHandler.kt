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

import com.saomc.saoui.SAOCore
import com.saomc.saoui.api.entity.rendering.ColorState.*
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.ResourceLocation
import java.lang.ref.WeakReference

/**
 * Part of saoui
 *
 * @author Bluexin
 */
class PlayerColorStateHandler(thePlayer: EntityPlayer) : IColorStateHandler {

    private val thePlayer: WeakReference<EntityPlayer>
    private var ticksForRedemption: Long = 0
    private var currentState = INNOCENT

    init {
        this.thePlayer = WeakReference(thePlayer)
    }

    /**
     * @return the color state the entity should be showing.
     */
    override fun getColorState(): ColorState {
        return this.currentState
    }

    /**
     * Called every tick.
     * Use this to handle anything special.
     */
    override fun tick() {
        if (ticksForRedemption > 0) {
            if (--ticksForRedemption == 0L) {
                if (currentState === VIOLENT)
                    currentState = INNOCENT
                else {
                    currentState = VIOLENT
                    ticksForRedemption = TICKS_PER_STATE
                }
            }
        }
    }

    /**
     * Called when the holder of this state handler hits another player.
     *
     * @param target the player hit
     */
    fun hit(target: EntityPlayer) {
        val targetState = target.getRenderData().getColorStateHandler().colorState
        if (targetState !== KILLER && targetState !== VIOLENT && this.currentState !== KILLER) {
            this.currentState = VIOLENT
            this.ticksForRedemption = TICKS_PER_STATE
        }
    }

    /**
     * Called when the holder of this state handler kills another player.
     *
     * @param target the player killed
     */
    fun kill(target: EntityPlayer) {
        val targetState =target.getRenderData().getColorStateHandler().colorState
        if (targetState !== KILLER && targetState !== VIOLENT) {
            if (this.currentState === KILLER)
                this.ticksForRedemption += TICKS_PER_STATE
            else {
                this.currentState = KILLER
                this.ticksForRedemption = TICKS_PER_STATE
            }
        }
    }

    /**
     * Save any data to NBT format.
     * The implementation has to create his own sub-tag and to behave properly.
     * This will be used for sync between client and server, and saving on world shutting down.
     *
     * @param tag the NBT tag to save to
     */
    override fun save(tag: NBTTagCompound) {
        val atag = NBTTagCompound()
        atag.setLong("ticksForRedemption", this.ticksForRedemption)
        atag.setInteger("state", this.currentState.ordinal)
        tag.setTag(KEY, atag)
    }

    /**
     * Load any data from NBT format.
     * The implementation has to retrieve his own sub-tag and to behave properly.
     * This will be used for sync between client and server, and loading on world starting up.
     *
     * @param tag the NBT tag to save to
     */
    override fun load(tag: NBTTagCompound) {
        val atag = tag.getCompoundTag(KEY)
        this.ticksForRedemption = atag.getLong("ticksForRedemption")
        this.currentState = ColorState.values()[atag.getInteger("state")]
    }

    companion object {

        private val KEY = ResourceLocation(SAOCore.MODID, "playerColorHandler").toString()

        /**
         * How long a player's state will persist.
         *
         *
         * Example:
         * player A (innocent) hits player B (innocent)
         * player A gets VIOLENT status for set amount of ticks
         * player A (violent) hits player B (innocent)
         * player A's VIOLENT status will be **reset** to set amount of ticks
         * player A (violent) kills player B (innocent)
         * player A gets KILLER status for set amount of ticks
         * player A (killer) kills player B (innocent) again
         * player A's KILLER status duration will be **extended** by set amount of ticks
         * player A (killer) hits player B (innocent)
         * player A's status doesn't change
         */
        private val TICKS_PER_STATE: Long = 12000
    }
}
