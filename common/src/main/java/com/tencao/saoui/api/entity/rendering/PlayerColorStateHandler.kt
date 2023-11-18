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

package com.tencao.saoui.api.entity.rendering

import com.tencao.saoui.Constants
import com.tencao.saoui.api.entity.rendering.ColorState.*
import com.tencao.saoui.util.Client
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Player
import java.lang.ref.WeakReference
import java.util.*

/**
 * Part of saoui
 *
 * @author Bluexin
 */
class PlayerColorStateHandler(thePlayer: Player) :
    IColorStateHandler {

    private val thePlayer: WeakReference<Player> = WeakReference(thePlayer)
    private var ticksForRedemption: Int = 0
    private var tickForGamePlayCheck: Int = 0
    private var currentState = getInnocent()

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
        if (--tickForGamePlayCheck <= 0 && Client.minecraft.connection != null) {
            val gamemode = Client.minecraft.connection!!.getPlayerInfo(thePlayer.get()?.uuid)?.gameMode
            if (gamemode != null) {
                currentState = when {
                    gamemode.isCreative -> {
                        CREATIVE
                    }
                    gamemode.isSurvival -> {
                        getInnocent()
                    }
                    else -> {
                        INVALID
                    }
                }
            }
            tickForGamePlayCheck = 20
        }
        if (ticksForRedemption > 0) {
            if (--ticksForRedemption == 0) {
                if (currentState === VIOLENT) {
                    currentState = getInnocent()
                } else {
                    currentState = VIOLENT
                    ticksForRedemption = TICKS_PER_STATE
                }
            }
        }
    }

    private fun getInnocent(): ColorState {
        return if (thePlayer.get() != null && devs.contains(thePlayer.get()!!.uuid)) DEV else INNOCENT
    }

    /**
     * Save any data to NBT format.
     * The implementation has to create his own sub-tag and to behave properly.
     * This will be used for sync between client and server, and saving on world shutting down.
     *
     * @param tag the NBT tag to save to
     */
    override fun save(tag: CompoundTag) {
        val atag = CompoundTag()
        atag.putInt("ticksForRedemption", this.ticksForRedemption)
        atag.putInt("state", this.currentState.ordinal)
        tag.put(KEY, atag)
    }

    /**
     * Load any data from NBT format.
     * The implementation has to retrieve his own sub-tag and to behave properly.
     * This will be used for sync between client and server, and loading on world starting up.
     *
     * @param tag the NBT tag to save to
     */
    override fun load(tag: CompoundTag) {
        val atag = tag.getCompound(KEY)
        this.ticksForRedemption = atag.getInt("ticksForRedemption")
        this.currentState = values()[atag.getInt("state")]
    }

    companion object {

        private val KEY = ResourceLocation(Constants.MODID, "player_color_handler").toString()

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
        private const val TICKS_PER_STATE: Int = 12000

        private val devs = arrayOf(UUID.fromString("08197bad-1da1-48fd-82f1-9b388c49b6c9"), UUID.fromString("dc1bced1-26df-4c18-8eca-37484229ded1"))
    }
}
