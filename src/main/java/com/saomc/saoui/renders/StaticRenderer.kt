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

package com.saomc.saoui.renders

import com.saomc.saoui.GLCore
import com.saomc.saoui.api.entity.rendering.ColorState
import com.saomc.saoui.capabilities.getRenderData
import com.saomc.saoui.config.OptionCore
import com.saomc.saoui.effects.DeathParticles
import com.saomc.saoui.resources.StringNames
import com.saomc.saoui.screens.ingame.HealthStep
import com.saomc.saoui.social.StaticPlayerHelper
import com.teamwizardry.librarianlib.features.kotlin.Minecraft
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.monster.EntityMob
import net.minecraftforge.fml.common.Loader
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.lwjgl.opengl.GL11
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

@SideOnly(Side.CLIENT)
object StaticRenderer { // TODO: add usage of scale, offset etc from capability

    private const val HEALTH_COUNT = 32
    private const val HEALTH_ANGLE = 0.35
    private const val HEALTH_RANGE = 0.975
    private const val HEALTH_OFFSET = 0.75f
    private const val HEALTH_HEIGHT = 0.21

    fun render(renderManager: RenderManager, living: EntityLivingBase, x: Double, y: Double, z: Double) {
        val mc = Minecraft.getMinecraft()
        mc.profiler.startSection("setupStaticRender")

        val dead = living.health <= 0

        if (living.deathTime == 1) living.deathTime++

        if (!dead && !living.isInvisibleToPlayer(mc.player) && living != mc.player) {
            living.getRenderData()?.let {renderCap ->
                when (val state = renderCap.getColorStateHandler().colorState){
                    ColorState.VIOLENT -> {
                        if (OptionCore.VIOLENT_HEALTH.isEnabled) doRenderHealthBar(renderManager, mc, living, x, y, z, sqrt(64))
                        if (OptionCore.VIOLENT_CRYSTAL.isEnabled) doRenderColorCursor(renderManager, mc, living, x, y, z, sqrt(64), state)
                    }
                    ColorState.KILLER -> {
                        if (OptionCore.KILLER_HEALTH.isEnabled) doRenderHealthBar(renderManager, mc, living, x, y, z, sqrt(64))
                        if (OptionCore.VIOLENT_CRYSTAL.isEnabled) doRenderColorCursor(renderManager, mc, living, x, y, z, sqrt(64), state)
                    }
                    ColorState.BOSS -> {
                        if (OptionCore.BOSS_HEALTH.isEnabled) doRenderHealthBar(renderManager, mc, living, x, y, z, sqrt(64))
                        if (OptionCore.VIOLENT_CRYSTAL.isEnabled) doRenderColorCursor(renderManager, mc, living, x, y, z, sqrt(64), state)
                    }
                    else -> {
                        if (OptionCore.INNOCENT_HEALTH.isEnabled) doRenderHealthBar(renderManager, mc, living, x, y, z, sqrt(64))
                        if (OptionCore.INNOCENT_CRYSTAL.isEnabled) doRenderColorCursor(renderManager, mc, living, x, y, z, sqrt(64), state)
                    }
                }
            }
        }
        mc.profiler.endSection()
    }

    private fun doRenderColorCursor(renderManager: RenderManager, mc: Minecraft, entity: EntityLivingBase, x: Double, y: Double, z: Double, distance: Int, color: ColorState) {
        mc.profiler.startSection("renderColorCursor")
        if (entity.ridingEntity != null) return

        if (entity.world.isRemote) {
            val d3 = entity.getDistanceSq(renderManager.renderViewEntity)

            if (d3 <= distance) {
                val sizeMult = if (entity.isChild && entity is EntityMob) 0.5f else 1.0f

                val f = 1.6f
                val f1 = 0.016666668f * f

                GLCore.pushMatrix()
                GLCore.glTranslatef(x.toFloat() + 0.0f, y.toFloat() + sizeMult * entity.height + sizeMult * 1.1f, z.toFloat())
                GLCore.glNormal3f(0.0f, 1.0f, 0.0f)
                GLCore.glRotatef(-renderManager.playerViewY, 0.0f, 1.0f, 0.0f)
                GLCore.glScalef(-(f1 * sizeMult), -(f1 * sizeMult), f1 * sizeMult)
                GLCore.lighting(false)

                GLCore.depth(true)

                GLCore.glAlphaTest(true)
                GLCore.glBlend(true)
                GLCore.tryBlendFuncSeparate(770, 771, 1, 0)

                GLCore.glBindTexture(if (OptionCore.SAO_UI.isEnabled) StringNames.entities else StringNames.entitiesCustom)
                GLCore.color(color.rgba)
                GLCore.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)

                if (OptionCore.SPINNING_CRYSTALS.isEnabled) {
                    val a = entity.world.totalWorldTime % 40 / 20.0 * Math.PI
                    val cos = cos(a)//Math.PI / 3 * 2);
                    val sin = sin(a)//Math.PI / 3 * 2);

                    if (a > Math.PI / 2 && a <= Math.PI * 3 / 2) {
                        GLCore.addVertex(9.0 * cos, -1.0, 9.0 * sin, 0.125, 0.25)
                        GLCore.addVertex(9.0 * cos, 17.0, 9.0 * sin, 0.125, 0.375)
                        GLCore.addVertex(-9.0 * cos, 17.0, -9.0 * sin, 0.0, 0.375)
                        GLCore.addVertex(-9.0 * cos, -1.0, -9.0 * sin, 0.0, 0.25)
                    } else {
                        GLCore.addVertex(-9.0 * cos, -1.0, -9.0 * sin, 0.0, 0.25)
                        GLCore.addVertex(-9.0 * cos, 17.0, -9.0 * sin, 0.0, 0.375)
                        GLCore.addVertex(9.0 * cos, 17.0, 9.0 * sin, 0.125, 0.375)
                        GLCore.addVertex(9.0 * cos, -1.0, 9.0 * sin, 0.125, 0.25)
                    }

                    if (a < Math.PI) {
                        GLCore.addVertex(-9.0 * sin, -1.0, 9.0 * cos, 0.125, 0.25)
                        GLCore.addVertex(-9.0 * sin, 17.0, 9.0 * cos, 0.125, 0.375)
                        GLCore.addVertex(9.0 * sin, 17.0, -9.0 * cos, 0.0, 0.375)
                        GLCore.addVertex(9.0 * sin, -1.0, -9.0 * cos, 0.0, 0.25)
                    } else {
                        GLCore.addVertex(9.0 * sin, -1.0, -9.0 * cos, 0.0, 0.25)
                        GLCore.addVertex(9.0 * sin, 17.0, -9.0 * cos, 0.0, 0.375)
                        GLCore.addVertex(-9.0 * sin, 17.0, 9.0 * cos, 0.125, 0.375)
                        GLCore.addVertex(-9.0 * sin, -1.0, 9.0 * cos, 0.125, 0.25)
                    }
                } else {
                    GLCore.addVertex(-9.0, -1.0, 0.0, 0.0, 0.25)
                    GLCore.addVertex(-9.0, 17.0, 0.0, 0.0, 0.375)
                    GLCore.addVertex(9.0, 17.0, 0.0, 0.125, 0.375)
                    GLCore.addVertex(9.0, -1.0, 0.0, 0.125, 0.25)
                }
                GLCore.draw()

                GLCore.lighting(true)
                GLCore.popMatrix()
            }
        }
        mc.profiler.endSection()
    }

    private fun doRenderHealthBar(renderManager: RenderManager, mc: Minecraft, living: EntityLivingBase, x: Double, y: Double, z: Double, distance: Int) {
        mc.profiler.startSection("renderHealthBars")
        mc.profiler.startSection("initCheck")
        if (living.ridingEntity != null && living.ridingEntity === mc.player) return
        if (living.health > living.maxHealth) return
        if (Loader.isModLoaded("neat")) return
        mc.profiler.endSection()


        val d3 = living.getDistanceSq(renderManager.renderViewEntity)

        if (d3 <= distance) {
            GLCore.glBindTexture(if (OptionCore.SAO_UI.isEnabled) StringNames.entities else StringNames.entitiesCustom)
            GLCore.pushMatrix()
            GLCore.depth(true)
            GLCore.glCullFace(false)
            GLCore.glBlend(true)

            GLCore.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)

            val hitPoints = (getHealthFactor(living) * HEALTH_COUNT).toInt()
            useColor(living)

            val sizeMult = if (living.isChild && living is EntityMob) 0.5f else 1.0f

            GLCore.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_TEX)
            for (i in 0..hitPoints) {
                val value = (i + HEALTH_COUNT - hitPoints).toDouble() / HEALTH_COUNT
                val rad = Math.toRadians((renderManager.playerViewY - 135).toDouble()) + (value - 0.5) * Math.PI * HEALTH_ANGLE

                val x0 = x + sizeMult.toDouble() * living.width.toDouble() * HEALTH_RANGE * cos(rad)
                val y0 = y + sizeMult * living.height * HEALTH_OFFSET
                val z0 = z + sizeMult.toDouble() * living.width.toDouble() * HEALTH_RANGE * sin(rad)

                val uv = value - (HEALTH_COUNT - hitPoints).toDouble() / HEALTH_COUNT

                GLCore.addVertex(x0, y0 + HEALTH_HEIGHT, z0, 1.0 - uv, 0.0)
                GLCore.addVertex(x0, y0, z0, 1.0 - uv, 0.125)
            }

            GLCore.draw()

            GLCore.color(1f, 1f, 1f, 1f)
            GLCore.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_TEX)

            for (i in 0..HEALTH_COUNT) {
                val value = i.toDouble() / HEALTH_COUNT
                val rad = Math.toRadians((renderManager.playerViewY - 135).toDouble()) + (value - 0.5) * Math.PI * HEALTH_ANGLE

                val x0 = x + sizeMult.toDouble() * living.width.toDouble() * HEALTH_RANGE * cos(rad)
                val y0 = y + sizeMult * living.height * HEALTH_OFFSET
                val z0 = z + sizeMult.toDouble() * living.width.toDouble() * HEALTH_RANGE * sin(rad)

                GLCore.addVertex(x0, y0 + HEALTH_HEIGHT, z0, 1.0 - value, 0.125)
                GLCore.addVertex(x0, y0, z0, 1.0 - value, 0.25)
            }

            GLCore.draw()

            GLCore.glCullFace(true)
            GLCore.popMatrix()
        }
        mc.profiler.endSection()
    }

    fun doSpawnDeathParticles(mc: Minecraft, living: Entity) {
        mc.profiler.startSection("spawnDeathParticles")
        val world = living.world

        if (living.world.isRemote) {
            val colors = arrayOf(floatArrayOf(1f / 0xFF * 0x9A, 1f / 0xFF * 0xFE, 1f / 0xFF * 0x2E), floatArrayOf(1f / 0xFF * 0x01, 1f / 0xFF * 0xFF, 1f / 0xFF * 0xFF), floatArrayOf(1f / 0xFF * 0x08, 1f / 0xFF * 0x08, 1f / 0xFF * 0x8A))

            val size = living.width * living.height
            val pieces = max(min(size * 64, 128f), 8f).toInt()

            for (i in 0 until pieces) {
                val color = colors[i % 3]

                val x0 = living.width.toDouble() * (Math.random() * 2 - 1) * 0.75
                val y0 = living.height * Math.random()
                val z0 = living.width.toDouble() * (Math.random() * 2 - 1) * 0.75

                mc.effectRenderer.addEffect(DeathParticles(
                        world,
                        living.posX + x0, living.posY + y0, living.posZ + z0,
                        color[0], color[1], color[2]
                ))
            }
        }
        mc.profiler.endSection()
    }

    private fun useColor(living: Entity) {
        if (living is EntityLivingBase) {
            HealthStep.getStep(living).glColor()
        } else {
            HealthStep.GOOD.glColor()
        }
    }

    private fun getHealthFactor(living: EntityLivingBase): Float {
        Minecraft().profiler.startSection("getHealthFactor")
        val normalFactor =living.health / StaticPlayerHelper.getMaxHealth(living)
        val delta = 1.0f - normalFactor

        val health = normalFactor + delta * delta / 2 * normalFactor
        Minecraft().profiler.endSection()
        return health
    }



    fun sqrt(int: Int) = int * int
}
