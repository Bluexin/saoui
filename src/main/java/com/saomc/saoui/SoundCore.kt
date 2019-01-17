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

package com.saomc.saoui

import com.saomc.saoui.config.OptionCore
import net.minecraft.client.Minecraft
import net.minecraft.client.audio.ISound
import net.minecraft.client.audio.PositionedSoundRecord
import net.minecraft.client.audio.SoundHandler
import net.minecraft.entity.Entity
import net.minecraft.util.ResourceLocation
import net.minecraft.util.SoundCategory

object SoundCore {

    const val CONFIRM = "sao.confirm"
    const val DIALOG_CLOSE = "sao.dialog.close"
    const val MENU_POPUP = "sao.menu.popup"
    const val MESSAGE = "sao.message"
    const val ORB_DROPDOWN = "sao.orb.dropdown"
    const val PARTICLES_DEATH = "sao.particles.death"
    const val LOW_HEALTH = "sao.low.health"

    private fun getResource(name: String): ResourceLocation {
        return ResourceLocation(SAOCore.MODID, name)
    }

    fun isSfxPlaying(name: String): Boolean {
        return Minecraft.getMinecraft().soundHandler.isSoundPlaying(create(getResource(name)))
    }

    fun playFromEntity(entity: Entity?, name: String) {
        if (entity != null) {
            playAtEntity(entity, name)
        }
    }

    fun playAtEntity(entity: Entity, name: String) {
        val mc = Minecraft.getMinecraft()

        if (mc.world != null && mc.world.isRemote) {
            play(mc.soundHandler, name, entity.posX.toFloat(), entity.posY.toFloat(), entity.posZ.toFloat())
        }
    }

    fun play(mc: Minecraft?, name: String) {
        if (mc?.world != null && mc.world.isRemote) {
            play(mc.soundHandler, name)
        }
    }

    fun play(handler: SoundHandler?, name: String) {
        if (OptionCore.SOUND_EFFECTS() && handler != null) {
            handler.playSound(create(getResource(name)))
        }
    }

    private fun play(handler: SoundHandler?, name: String, x: Float, y: Float, z: Float) {
        if (OptionCore.SOUND_EFFECTS() && handler != null) {
            handler.playSound(create(getResource(name), x, y, z))
        }
    }

    fun resumeSounds(mc: Minecraft) {
        mc.soundHandler.resumeSounds()
    }

    //Helper functions - 1.8.8 mirror
    private fun create(soundResource: ResourceLocation): PositionedSoundRecord {
        return PositionedSoundRecord(soundResource, SoundCategory.MASTER, 1.0f, 1.0f, false, 0, ISound.AttenuationType.NONE, 0.0f, 0.0f, 0.0f)
    }

    private fun create(soundResource: ResourceLocation, xPosition: Float, yPosition: Float, zPosition: Float): PositionedSoundRecord {
        return PositionedSoundRecord(soundResource, SoundCategory.MASTER, 4.0f, 1.0f, false, 0, ISound.AttenuationType.LINEAR, xPosition, yPosition, zPosition)
    }
}
