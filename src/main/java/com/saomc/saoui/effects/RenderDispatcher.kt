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

package com.saomc.saoui.effects

import com.saomc.saoui.GLCore
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.Tessellator
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.lwjgl.opengl.GL11

/**
 * This code was original created by <Vazkii> and has been modified to our needs
 * All credit goes to him
</Vazkii> */
@SideOnly(Side.CLIENT)
object RenderDispatcher {

    var particleFxCount = 0

    fun dispatch() {
        val tessellator = Tessellator.getInstance()

        val profiler = Minecraft.getMinecraft().profiler

        //GL11.glPushAttrib(GL11.GL_LIGHTING);
        GLCore.glBlend(true)
        GLCore.blendFunc(GL11.GL_ONE, GL11.GL_ONE)

        profiler.startSection("death particle")
        DeathParticles.dispatchQueuedRenders(tessellator)
        profiler.endSection()
        GLCore.blendFunc(GL11.GL_ONE, GL11.GL_ZERO)
        GLCore.glBlend(false)
        //GL11.glPopAttrib();
    }
}
