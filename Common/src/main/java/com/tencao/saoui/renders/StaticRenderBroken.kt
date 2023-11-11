package com.tencao.saoui.renders

import com.mojang.blaze3d.matrix.MatrixStack
import com.tencao.saomclib.Client
import com.tencao.saomclib.GLCore
import com.tencao.saoui.api.entity.rendering.ColorState
import com.tencao.saoui.capabilities.getRenderData
import com.tencao.saoui.config.OptionCore
import com.tencao.saoui.resources.StringNames
import com.tencao.saoui.screens.util.HealthStep
import com.tencao.saoui.social.StaticPlayerHelper
import com.tencao.saoui.util.ColorUtil
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.IRenderTypeBuffer
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.util.math.vector.Quaternion
import net.minecraft.util.math.vector.Vector3f
import net.minecraft.world.LightType
import net.minecraftforge.fml.ModList
import org.lwjgl.opengl.GL11
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

class StaticRenderBroken {

    private val HEALTH_COUNT = 32
    private val HEALTH_ANGLE = 0.35
    private val HEALTH_RANGE = 0.975
    private val HEALTH_OFFSET = 0.75f
    private val HEALTH_HEIGHT = 0.21
    private var partialTicks = -1f

    /**
     * Caches the player view for all entities,
     */
    private var playerView = arrayOfNulls<Pair<Double, Double>>(HEALTH_COUNT + 1)

    /**
     * Caches the cursor view for all entities,
     */
    private var cursorView = Pair(0.0, 0.0)
    private val renderManager get() = Client.minecraft.entityRenderDispatcher

    fun render(matrixStack: MatrixStack) {
        if (Client.player == null) return
        if (Client.minecraft.frameTime != partialTicks) {
            updateView()
        }
        Client.minecraft.profiler.push("setupStaticRender")

        GLCore.glBindTexture(StringNames.entities)
        Client.minecraft.level?.entitiesForRendering()?.asSequence()?.filter { it is LivingEntity && it != Client.minecraft.player }?.forEach {
            val living = it as LivingEntity
            if (living.isInWater && !Client.player!!.level.getBlockState(Client.player!!.blockPosition().above()).material.isLiquid) {
                val state = Client.player!!.level.getBlockState(living.blockPosition().above(2))
                if (state.material.isLiquid || state.material.isSolid || state.material.isSolidBlocking) {
                    return@forEach
                }
            }

            val camera = renderManager.camera.position
            val pos = living.getEyePosition(partialTicks)
            val x: Double = living.xo + (living.x - living.xo) * partialTicks
            val y: Double = living.yo + (living.y - living.yo) * partialTicks
            val z: Double = living.zo + (living.z - living.zo) * partialTicks
            val dead = living.health <= 0

            if (living.deathTime == 1) living.deathTime++

            if (!dead && !living.isInvisibleTo(Client.player!!) && living != Client.player!!) {
                living.getRenderData()?.let { renderCap ->
                    matrixStack.pushPose()
                    matrixStack.translate(x - camera.x, y - camera.y, z - camera.z)
                    // GLCore.glRotate(renderManager.info.pitch, 1f, 0f, 0f)
                    // GLCore.glRotate(renderManager.info.yaw + 180, 0f, 1f, 0f)
                    // GLCore.glRotate(renderManager.cameraOrientation)
                    // GLCore.glRotate(Vector3f.ZP.rotationDegrees(180.0F))
                    // GLCore.translate(-camera.x.toDouble(), -camera.y.toDouble(), -camera.z.toDouble())
                    val state = renderCap.colorStateHandler.colorState
                    if (checkCrystal(state) && renderCap.colorStateHandler.shouldDrawCrystal()) doRenderColorCursor(matrixStack, living, sqrt(64), state)
                    // if (checkHealth(state) && renderCap.colorStateHandler.shouldDrawHealth()) doRenderHealthBar(matrixStack, living, sqrt(64))
                    matrixStack.popPose()
                }
            }
        }

        Client.minecraft.profiler.pop()
    }

    fun updateView() {
        partialTicks = Client.minecraft.frameTime
        for (i in 0..HEALTH_COUNT) {
            val value = i.toDouble() / HEALTH_COUNT
            val rad = Math.toRadians((renderManager.camera.yRot - 135).toDouble()) + (value - 0.5) * Math.PI * HEALTH_ANGLE
            playerView[i] = Pair(cos(rad), sin(rad))
        }
        val a = Client.player!!.level.gameTime % 40 / 20.0 * Math.PI
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

    private fun doRenderColorCursor(matrixStack: MatrixStack, entity: LivingEntity, distance: Int, color: ColorState) {
        if (entity.vehicle != null) return

        if (entity.level.isClientSide) {
            val d3 = entity.distanceToSqr(renderManager.camera.entity)

            if (d3 <= distance) {
                val renderBuffer = IRenderTypeBuffer.immediate(Tessellator.getInstance().builder)
                val sizeMult = if (entity.isBaby) 0.5f else 1.0f

                val f = 1.6f
                val f1 = 0.016666668f * f

                matrixStack.pushPose()

                // GLCore.glNormal3f(0.0f, 1.0f, 0.0f)]
                val rotation: Quaternion = renderManager.camera.rotation().copy()
                rotation.mul(-1.0f)
                matrixStack.mulPose(rotation)
                // matrixStack.rotate(Quaternion(0f, 1f, 0f, -renderManager.info.yaw))
                matrixStack.translate(0.0, sizeMult * entity.bbHeight + sizeMult * 1.1, 0.0)

                val scale = 0.026666673f
                matrixStack.scale(-scale, -scale, scale)
                // matrixStack.scale(-(f1 * sizeMult), -(f1 * sizeMult), f1 * sizeMult)

                GLCore.lighting(false)

                GLCore.depth(true)

                GLCore.glAlphaTest(true)
                GLCore.glBlend(true)
                GLCore.tryBlendFuncSeparate(770, 771, 1, 0)

                val sunBrightness = limit(cos(entity.level.getSunAngle(1.0f).toDouble()).toFloat() * 2.0f + 0.2f, 0.0f, 1.0f)
                val light = entity.level.getBrightness(LightType.SKY, entity.blockPosition()) / 15.0f * sunBrightness
                // val colors = GLCore.getColor(color.rgba, light)
                // GLCore.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
                val builder = renderBuffer.getBuffer(RenderTypes.crystalRender)
                // builder.normal(matrixStack.last.normal, 0f, 1f, 0f)

                val entry = matrixStack.last()
                val mat = entry.pose()
                val normal = Vector3f(0.0f, 1.0f, 0.0f)
                normal.transform(entry.normal())

                if (OptionCore.SPINNING_CRYSTALS.isEnabled) {
                    val a = entity.level.gameTime % 40 / 20.0 * Math.PI
                    // val cos = cos(a)//Math.PI / 3 * 2);
                    // val sin = sin(a)//Math.PI / 3 * 2);
                    val cos = cursorView.first.toFloat()
                    val sin = cursorView.second.toFloat()

                    if (a > Math.PI / 2 && a <= Math.PI * 3 / 2) {
                        GLCore.addVertex(builder, mat, 9.0F * cos, -1.0F, 9.0F * sin, 0.125F, 0.25F, color.rgba, light, normal)
                        GLCore.addVertex(builder, mat, 9.0F * cos, 17.0F, 9.0F * sin, 0.125F, 0.375F, color.rgba, light, normal)
                        GLCore.addVertex(builder, mat, -9.0F * cos, 17.0F, -9.0F * sin, 0.0F, 0.375F, color.rgba, light, normal)
                        GLCore.addVertex(builder, mat, -9.0F * cos, -1.0F, -9.0F * sin, 0.0F, 0.25F, color.rgba, light, normal)
                    } else {
                        GLCore.addVertex(builder, mat, -9.0F * cos, -1.0F, -9.0F * sin, 0.0F, 0.25F, color.rgba, light, normal)
                        GLCore.addVertex(builder, mat, -9.0F * cos, 17.0F, -9.0F * sin, 0.0F, 0.375F, color.rgba, light, normal)
                        GLCore.addVertex(builder, mat, 9.0F * cos, 17.0F, 9.0F * sin, 0.125F, 0.375F, color.rgba, light, normal)
                        GLCore.addVertex(builder, mat, 9.0F * cos, -1.0F, 9.0F * sin, 0.125F, 0.25F, color.rgba, light, normal)
                    }

                    if (a < Math.PI) {
                        GLCore.addVertex(builder, mat, -9.0F * sin, -1.0F, 9.0F * cos, 0.125F, 0.25F, color.rgba, light, normal)
                        GLCore.addVertex(builder, mat, -9.0F * sin, 17.0F, 9.0F * cos, 0.125F, 0.375F, color.rgba, light, normal)
                        GLCore.addVertex(builder, mat, 9.0F * sin, 17.0F, -9.0F * cos, 0.0F, 0.375F, color.rgba, light, normal)
                        GLCore.addVertex(builder, mat, 9.0F * sin, -1.0F, -9.0F * cos, 0.0F, 0.25F, color.rgba, light, normal)
                    } else {
                        GLCore.addVertex(builder, mat, 9.0F * sin, -1.0F, -9.0F * cos, 0.0F, 0.25F, color.rgba, light, normal)
                        GLCore.addVertex(builder, mat, 9.0F * sin, 17.0F, -9.0F * cos, 0.0F, 0.375F, color.rgba, light, normal)
                        GLCore.addVertex(builder, mat, -9.0F * sin, 17.0F, 9.0F * cos, 0.125F, 0.375F, color.rgba, light, normal)
                        GLCore.addVertex(builder, mat, -9.0F * sin, -1.0F, 9.0F * cos, 0.125F, 0.25F, color.rgba, light, normal)
                    }
                } else {
                    GLCore.addVertex(builder, mat, -9.0F, -1.0F, 0.0F, 0.0F, 0.25F, color.rgba, light, normal)
                    GLCore.addVertex(builder, mat, -9.0F, 17.0F, 0.0F, 0.0F, 0.375F, color.rgba, light, normal)
                    GLCore.addVertex(builder, mat, 9.0F, 17.0F, 0.0F, 0.125F, 0.375F, color.rgba, light, normal)
                    GLCore.addVertex(builder, mat, 9.0F, -1.0F, 0.0F, 0.125F, 0.25F, color.rgba, light, normal)
                }

                GLCore.lighting(true)

                matrixStack.popPose()

                renderBuffer.endBatch()
            }
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

    private fun doRenderHealthBar(matrixStack: MatrixStack, living: LivingEntity, distance: Int) {
        if (living.vehicle != null && living.vehicle === Client.player) return
        if (living.health > living.maxHealth) return
        if (ModList.get().isLoaded("Neat")) return

        val d3 = living.distanceToSqr(renderManager.camera.entity)

        if (d3 <= distance) {
            GLCore.pushMatrix()
            GLCore.depth(true)
            GLCore.glCullFace(false)
            GLCore.glBlend(true)
            GLCore.lighting(false)

            GLCore.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)

            val hitPoints = (getHealthFactor(living) * HEALTH_COUNT).toInt()
            val sunBrightness = limit(cos(living.level.getSunAngle(1.0f).toDouble()).toFloat() * 2.0f + 0.2f, 0.0f, 1.0f)
            val light = living.level.getBrightness(LightType.SKY, living.blockPosition()) / 15.0f * sunBrightness
            useColor(living, light)

            val size = if (living.isBaby) 0.5f else 1.0f
            val width = size.toDouble() * living.bbWidth.toDouble() * HEALTH_RANGE
            val height = size * living.bbHeight * HEALTH_OFFSET

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
    }

    fun doSpawnDeathParticles(mc: Minecraft, living: Entity) {
        if (OptionCore.PARTICLES.isEnabled) {
            Client.minecraft.profiler.push("spawnDeathParticles")
            val level = living.level

            if (living.level.isClientSide) {
                val colors = arrayOf(floatArrayOf(1f / 0xFF * 0x9A, 1f / 0xFF * 0xFE, 1f / 0xFF * 0x2E), floatArrayOf(1f / 0xFF * 0x01, 1f / 0xFF * 0xFF, 1f / 0xFF * 0xFF), floatArrayOf(1f / 0xFF * 0x08, 1f / 0xFF * 0x08, 1f / 0xFF * 0x8A))

                val size = living.bbWidth * living.bbHeight
                val pieces = max(min(size * 64, 128f), 8f).toInt()

                for (i in 0 until pieces) {
                    val color = colors[i % 3]

                    val x0 = living.bbWidth.toDouble() * (Math.random() * 2 - 1) * 0.75
                    val y0 = living.bbHeight * Math.random()
                    val z0 = living.bbWidth.toDouble() * (Math.random() * 2 - 1) * 0.75

                    /*
                    mc.effectRenderer.addEffect(
                        DeathParticles(
                            level,
                            living.posX + x0, living.posY + y0, living.posZ + z0,
                            color[0], color[1], color[2]
                        )
                    )*/
                }
            }
            Client.minecraft.profiler.pop()
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
        Client.minecraft.profiler.push("getHealthFactor")
        val normalFactor = living.health / StaticPlayerHelper.getMaxHealth(living)
        val delta = 1.0f - normalFactor

        val health = normalFactor + delta * delta / 2 * normalFactor
        Client.minecraft.profiler.pop()
        return health
    }

    fun sqrt(int: Int) = int * int
}
