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

package com.tencao.saoui

import com.tencao.saoui.util.Client
import net.minecraft.client.resources.sounds.SimpleSoundInstance
import net.minecraft.resources.ResourceLocation
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundSource
import net.minecraft.world.entity.Entity
import java.util.*

enum class SoundCore {

    CONFIRM,
    DIALOG_CLOSE,
    MENU_POPUP,
    MESSAGE,
    ORB_DROPDOWN,
    PARTICLES_DEATH;

    val resource = ResourceLocation(Constants.MODID, name.lowercase(Locale.getDefault()))
    lateinit var sound: SoundEvent

    fun play() = Client.minecraft.soundManager.play(SimpleSoundInstance.forUI(this.sound, 1.0f))

    fun playAtEntity(ent: Entity) = ent.level.playLocalSound(ent.x, ent.y, ent.z, this.sound, SoundSource.AMBIENT, 1.0f, 1.0f, true)

}
