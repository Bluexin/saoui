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

package com.saomc.saoui.resources

import com.saomc.saoui.SAOCore
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

@SideOnly(Side.CLIENT)
object StringNames {

    val gui = ResourceLocation(SAOCore.MODID, "textures/guiedt.png")
    val slot = ResourceLocation(SAOCore.MODID, "textures/slot.png")
    val entities = ResourceLocation(SAOCore.MODID, "textures/entities.png")
    val entitiesCustom = ResourceLocation(SAOCore.MODID, "textures/entitiescustom.png")
    val particleLarge = ResourceLocation(SAOCore.MODID, "textures/particlelarge.png")
}
