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

package com.tencao.saoui.renders

import com.mojang.blaze3d.matrix.MatrixStack
import com.mojang.blaze3d.platform.GlStateManager
import com.tencao.saomclib.Client
import com.tencao.saomclib.GLCore
import com.tencao.saoui.api.entity.rendering.ColorState
import com.tencao.saoui.capabilities.getRenderData
import com.tencao.saoui.config.OptionCore
import com.tencao.saoui.effects.particles.DeathParticleData
import com.tencao.saoui.resources.StringNames
import com.tencao.saoui.screens.util.HealthStep
import com.tencao.saoui.social.StaticPlayerHelper
import com.tencao.saoui.util.ColorUtil
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.world.LightType
import net.minecraftforge.fml.ModList
import org.lwjgl.opengl.GL11
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

object StaticRenderer { // TODO: add usage of scale, offset etc from capability

    private const val HEALTH_COUNT = 32
    private const val HEALTH_ANGLE = 0.35
    private const val HEALTH_RANGE = 0.975
    private const val HEALTH_OFFSET = 0.75f
    private const val HEALTH_HEIGHT = 0.21
    private var partialTicks = -1f
    private val mc = Client.minecraft
    private val renderManager
        get() = Client.minecraft.renderManager

    /**
     * Caches the player view for all entities,
     */
    private var playerView = arrayOfNulls<Pair<Double, Double>>(HEALTH_COUNT + 1)

    /**
     * Caches the cursor view for all entities,
     */
    private var cursorView = Pair(0.0, 0.0)

    fun render(matrixStack: MatrixStack) {
        if (Client.player == null) return
        val renderManager = Client.minecraft.renderManager
        if (mc.renderPartialTicks != partialTicks) {
            updateView()
        }
        Client.minecraft.renderPartialTicks
        mc.profiler.startSection("setupStaticRender")

        GLCore.glBindTexture(StringNames.entities)
        Client.minecraft.world!!.allEntities.asSequence().filter { it is LivingEntity && it != Client.minecraft.player }.forEach {
            val living = it as LivingEntity
            if (living.isInWater && !mc.player!!.world.getBlockState(mc.player!!.position.up()).material.isLiquid) {
                val state = mc.player!!.world.getBlockState(living.position.up(2))
                if (state.material.isLiquid || state.material.isSolid || state.material.isOpaque) {
                    return@forEach
                }
            }
            /*
            val x = (living.lastTickPosX + (living.posX - living.lastTickPosX) * partialTicks.toDouble()) - renderManager.info.projectedView.x
            val y = (living.lastTickPosY + (living.posY - living.lastTickPosY) * partialTicks.toDouble()) - renderManager.info.projectedView.y
            val z = (living.lastTickPosZ + (living.posZ - living.lastTickPosZ) * partialTicks.toDouble()) - renderManager.info.projectedView.z

             */

            val camera = renderManager.info.projectedView
            val pos = living.getEyePosition(partialTicks)
            val x: Double = living.prevPosX + (living.posX - living.prevPosX) * partialTicks
            val y: Double = living.prevPosY + (living.posY - living.prevPosY) * partialTicks
            val z: Double = living.prevPosZ + (living.posZ - living.prevPosZ) * partialTicks
            val dead = living.health <= 0

            if (living.deathTime == 1) living.deathTime++

            if (!dead && !living.isInvisibleToPlayer(mc.player) && living != mc.player && living.isInRangeToRender3d(mc.player!!.posX, mc.player!!.posY, mc.player!!.posZ) && mc.player!!.canEntityBeSeen(living)) {
                living.getRenderData()?.let { renderCap ->
                    GLCore.pushMatrix()
                    matrixStack.push()
                    matrixStack.translate(x - camera.x, y - camera.y, z - camera.z)
                    val matrix = matrixStack.last.matrix
                    GlStateManager.multMatrix(matrix)
                    val state = renderCap.colorStateHandler.colorState
                    if (checkCrystal(state) && renderCap.colorStateHandler.shouldDrawCrystal()) doRenderColorCursor(living, state)
                    if (checkHealth(state) && renderCap.colorStateHandler.shouldDrawHealth()) doRenderHealthBar(living)
                    matrix.invert()
                    GlStateManager.multMatrix(matrix)
                    matrixStack.pop()
                    GLCore.popMatrix()
                }
            }
        }
        mc.profiler.endSection()
    }

    fun updateView() {
        partialTicks = mc.renderPartialTicks
        for (i in 0..HEALTH_COUNT) {
            val value = i.toDouble() / HEALTH_COUNT
            val rad = Math.toRadians((renderManager.info.yaw - 135).toDouble()) + (value - 0.5) * Math.PI * HEALTH_ANGLE
            playerView[i] = Pair(cos(rad), sin(rad))
        }
        val a = mc.world!!.gameTime % 40 / 20.0 * Math.PI
        cursorView = Pair(cos(a), sin(a))
    }

    fun checkHealth(color: ColorState): Boolean {
        return when (color) {
            ColorState.INNOCENT -> OptionCore.INNOCENT_HEALTH.isEnabled
            ColorState.VIOLENT -> OptionCore.VIOLENT_HEALTH.isEnabled
            ColorState.KILLER -> OptionCore.KILLER_HEALTH.isEnabled
            ColorState.BOSS -> OptionCore.BOSS_HEALTH.isEnabled
            ColorState.CREATIVE -> OptionCore.CREATIVE_HEALTH.isEnabled
            ColorState.OP -> OptionCore.OP_HEALTH.isEnabled
            ColorState.INVALID -> OptionCore.INVALID_HEALTH.isEnabled
            ColorState.DEV -> OptionCore.DEV_HEALTH.isEnabled
        }
    }

    fun checkCrystal(color: ColorState): Boolean {
        return when (color) {
            ColorState.INNOCENT -> OptionCore.INNOCENT_CRYSTAL.isEnabled
            ColorState.VIOLENT -> OptionCore.VIOLENT_CRYSTAL.isEnabled
            ColorState.KILLER -> OptionCore.KILLER_CRYSTAL.isEnabled
            ColorState.BOSS -> OptionCore.BOSS_CRYSTAL.isEnabled
            ColorState.CREATIVE -> OptionCore.CREATIVE_CRYSTAL.isEnabled
            ColorState.OP -> OptionCore.OP_CRYSTAL.isEnabled
            ColorState.INVALID -> OptionCore.INVALID_CRYSTAL.isEnabled
            ColorState.DEV -> OptionCore.DEV_CRYSTAL.isEnabled
        }
    }

    private fun doRenderColorCursor(entity: LivingEntity, color: ColorState) {
        if (entity.ridingEntity != null) return

        if (entity.world.isRemote) {
            val sizeMult = if (entity.isChild) 0.5f else 1.0f

            val f = 1.6f
            val f1 = 0.016666668f * f

            GLCore.pushMatrix()
            GLCore.translate(0.0f, sizeMult * entity.height + sizeMult * 1.1f, 0.0f)
            GLCore.glNormal3f(0.0f, 1.0f, 0.0f)
            // GLCore.glRotate(-renderManager.info.projectedView.y.toFloat(), 0.0f, 1.0f, 0.0f)
            GLCore.scale(-(f1 * sizeMult), -(f1 * sizeMult), f1 * sizeMult)
            GLCore.lighting(false)

            GLCore.glCullFace(false)
            GLCore.depth(true)

            GLCore.glAlphaTest(true)
            GLCore.glBlend(true)
            GLCore.tryBlendFuncSeparate(770, 771, 1, 0)

            val sunBrightness = limit(cos(entity.world.getCelestialAngleRadians(1.0f).toDouble()).toFloat() * 2.0f + 0.2f, 0.0f, 1.0f)
            val light = entity.world.getLightFor(LightType.SKY, entity.position) / 15.0f * sunBrightness
            GLCore.color(color.rgba, light)
            GLCore.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)

            if (OptionCore.SPINNING_CRYSTALS.isEnabled) {
                val a = entity.world.gameTime % 40 / 20.0 * Math.PI
                // val cos = cos(a)//Math.PI / 3 * 2);
                // val sin = sin(a)//Math.PI / 3 * 2);
                val cos = cursorView.first
                val sin = cursorView.second

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

    fun limit(value: Float, min: Float, max: Float): Float {
        if (java.lang.Float.isNaN(value) || value <= min) {
            return min
        }
        return if (value >= max) {
            max
        } else value
    }

    private fun doRenderHealthBar(living: LivingEntity) {
        if (living.ridingEntity != null && living.ridingEntity === mc.player) return
        if (living.health > living.maxHealth) return
        if (ModList.get().isLoaded("neat")) return

        GLCore.pushMatrix()
        GLCore.depth(true)
        GLCore.glCullFace(false)
        GLCore.glBlend(true)
        GLCore.lighting(false)

        GLCore.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)

        val hitPoints = (getHealthFactor(living) * HEALTH_COUNT).toInt()
        val sunBrightness = limit(cos(living.world.getCelestialAngleRadians(1.0f).toDouble()).toFloat() * 2.0f + 0.2f, 0.0f, 1.0f)
        val light = living.world.getLightFor(LightType.SKY, living.position) / 15.0f * sunBrightness
        useColor(living, light)

        val size = if (living.isChild) 0.5f else 1.0f
        val width = size.toDouble() * living.width.toDouble() * HEALTH_RANGE
        val height = size * living.height * HEALTH_OFFSET

        GLCore.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_TEX)
        for (i in 0..hitPoints) {
            val value = (i + HEALTH_COUNT - hitPoints).toDouble() / HEALTH_COUNT
            val x0 = width * playerView[i + HEALTH_COUNT - hitPoints]!!.first
            val y0 = height.toDouble()
            val z0 = width * playerView[i + HEALTH_COUNT - hitPoints]!!.second

            val uv = value - (HEALTH_COUNT - hitPoints).toDouble() / HEALTH_COUNT

            GLCore.addVertex(x0, y0 + HEALTH_HEIGHT, z0, 1.0 - uv, 0.0)
            GLCore.addVertex(x0, y0, z0, 1.0 - uv, 0.125)
        }

        GLCore.draw()

        GLCore.color(ColorUtil.DEFAULT_COLOR.rgba, light)
        GLCore.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_TEX)

        for (i in 0..HEALTH_COUNT) {
            val value = i.toDouble() / HEALTH_COUNT

            val x0 = width * playerView[i]!!.first
            val y0 = height.toDouble()
            val z0 = width * playerView[i]!!.second

            GLCore.addVertex(x0, y0 + HEALTH_HEIGHT, z0, 1.0 - value, 0.125)
            GLCore.addVertex(x0, y0, z0, 1.0 - value, 0.25)
        }

        GLCore.draw()

        GLCore.glCullFace(true)
        GLCore.lighting(true)
        GLCore.popMatrix()
    }

    fun doSpawnDeathParticles(mc: Minecraft, living: Entity) {
        if (OptionCore.PARTICLES.isEnabled) {
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

                    mc.world!!.addParticle(DeathParticleData(color[0], color[1], color[2]), living.posX + x0, living.posY + y0, living.posZ + z0, 0.0, 0.0, 0.0)
                }
            }
            mc.profiler.endSection()
        }
    }

    private fun useColor(living: Entity, light: Float) {
        if (living is LivingEntity) {
            GLCore.color(HealthStep.getStep(living).rgba, light)
        } else {
            GLCore.color(HealthStep.GOOD.rgba, light)
        }
    }

    private fun getHealthFactor(living: LivingEntity): Float {
        Client.minecraft.profiler.startSection("getHealthFactor")
        val normalFactor = living.health / StaticPlayerHelper.getMaxHealth(living)
        val delta = 1.0f - normalFactor

        val health = normalFactor + delta * delta / 2 * normalFactor
        Client.minecraft.profiler.endSection()
        return health
    }

    fun sqrt(int: Int) = int * int
}
