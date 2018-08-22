package com.saomc.saoui.renders

import be.bluexin.saomclib.player
import be.bluexin.saomclib.world
import com.saomc.saoui.GLCore
import com.saomc.saoui.SAOCore
import com.saomc.saoui.api.entity.rendering.RenderCapability
import com.saomc.saoui.config.OptionCore
import com.saomc.saoui.effects.DeathParticles
import com.saomc.saoui.resources.StringNames
import com.saomc.saoui.screens.ingame.HealthStep
import com.saomc.saoui.social.StaticPlayerHelper
import cpw.mods.fml.common.Loader
import cpw.mods.fml.relauncher.Side
import cpw.mods.fml.relauncher.SideOnly
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.monster.EntityMob
import net.minecraft.entity.monster.IMob
import org.lwjgl.opengl.GL11

@SideOnly(Side.CLIENT)
object StaticRenderer { // TODO: add usage of scale, offset etc from capability

    private const val HEALTH_COUNT = 32
    private const val HEALTH_ANGLE = 0.35
    private const val HEALTH_RANGE = 0.975
    private const val HEALTH_OFFSET = 0.75f
    private const val HEALTH_HEIGHT = 0.21

    fun render(renderManager: RenderManager, living: EntityLivingBase, x: Double, y: Double, z: Double) {
        val mc = Minecraft.getMinecraft()

        val dead = StaticPlayerHelper.getHealth(mc, living, SAOCore.UNKNOWN_TIME_DELAY) <= 0

        if (living.deathTime == 1) living.deathTime++

        if (!dead && !living.isInvisibleToPlayer(mc.player)) {
            if (OptionCore.COLOR_CURSOR.isEnabled/* && living.hasCapability(RenderCapability.RENDER_CAPABILITY, null)*/)
                doRenderColorCursor(renderManager, mc, living, x, y, z, 64)

            if (OptionCore.HEALTH_BARS.isEnabled && living != mc.player/* && living.hasCapability(RenderCapability.RENDER_CAPABILITY, null)*/)
                doRenderHealthBar(renderManager, mc, living, x, y, z)
        }
    }

    private fun doRenderColorCursor(renderManager: RenderManager, mc: Minecraft, entity: EntityLivingBase, x: Double, y: Double, z: Double, distance: Int) {
        if (entity.ridingEntity != null) return
        if (OptionCore.LESS_VISUALS.isEnabled && !(entity is IMob || StaticPlayerHelper.getHealth(mc, entity, SAOCore.UNKNOWN_TIME_DELAY) != StaticPlayerHelper.getMaxHealth(entity)))
            return

        if (entity.world.isRemote) {
            val d3 = entity.getDistanceSqToEntity(mc.renderViewEntity)

            if (d3 <= distance * distance) {
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
                GLCore.color(RenderCapability.get(entity).colorStateHandler.colorState.rgba)
                GLCore.begin()

                if (OptionCore.SPINNING_CRYSTALS.isEnabled) {
                    val a = entity.world.totalWorldTime % 40 / 20.0 * Math.PI
                    val cos = Math.cos(a)//Math.PI / 3 * 2);
                    val sin = Math.sin(a)//Math.PI / 3 * 2);

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
    }

    private fun doRenderHealthBar(renderManager: RenderManager, mc: Minecraft, living: EntityLivingBase, x: Double, y: Double, z: Double) {
        if (living.ridingEntity != null && living.ridingEntity === mc.player) return
        if (OptionCore.LESS_VISUALS.isEnabled && !(living is IMob || StaticPlayerHelper.getHealth(mc, living, SAOCore.UNKNOWN_TIME_DELAY) != StaticPlayerHelper.getMaxHealth(living)))
            return
        if (!OptionCore.MOB_HEALTH.isEnabled || Loader.isModLoaded("neat")) return

        GLCore.glBindTexture(if (OptionCore.SAO_UI.isEnabled) StringNames.entities else StringNames.entitiesCustom)
        GLCore.pushMatrix()
        GLCore.depth(true)
        GLCore.glCullFace(false)
        GLCore.glBlend(true)

        GLCore.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)

        val hitPoints = (getHealthFactor(mc, living, SAOCore.UNKNOWN_TIME_DELAY) * HEALTH_COUNT).toInt()
        useColor(mc, living, SAOCore.UNKNOWN_TIME_DELAY)

        val sizeMult = if (living.isChild && living is EntityMob) 0.5f else 1.0f

        GLCore.begin(GL11.GL_TRIANGLE_STRIP)
        for (i in 0..hitPoints) {
            val value = (i + HEALTH_COUNT - hitPoints).toDouble() / HEALTH_COUNT
            val rad = Math.toRadians((renderManager.playerViewY - 135).toDouble()) + (value - 0.5) * Math.PI * HEALTH_ANGLE

            val x0 = x + sizeMult.toDouble() * living.width.toDouble() * HEALTH_RANGE * Math.cos(rad)
            val y0 = y + sizeMult * living.height * HEALTH_OFFSET
            val z0 = z + sizeMult.toDouble() * living.width.toDouble() * HEALTH_RANGE * Math.sin(rad)

            val uv = value - (HEALTH_COUNT - hitPoints).toDouble() / HEALTH_COUNT

            GLCore.addVertex(x0, y0 + HEALTH_HEIGHT, z0, 1.0 - uv, 0.0)
            GLCore.addVertex(x0, y0, z0, 1.0 - uv, 0.125)
        }

        GLCore.draw()

        GLCore.color(1f, 1f, 1f, 1f)
        GLCore.begin(GL11.GL_TRIANGLE_STRIP)

        for (i in 0..HEALTH_COUNT) {
            val value = i.toDouble() / HEALTH_COUNT
            val rad = Math.toRadians((renderManager.playerViewY - 135).toDouble()) + (value - 0.5) * Math.PI * HEALTH_ANGLE

            val x0 = x + sizeMult.toDouble() * living.width.toDouble() * HEALTH_RANGE * Math.cos(rad)
            val y0 = y + sizeMult * living.height * HEALTH_OFFSET
            val z0 = z + sizeMult.toDouble() * living.width.toDouble() * HEALTH_RANGE * Math.sin(rad)

            GLCore.addVertex(x0, y0 + HEALTH_HEIGHT, z0, 1.0 - value, 0.125)
            GLCore.addVertex(x0, y0, z0, 1.0 - value, 0.25)
        }

        GLCore.draw()

        GLCore.glCullFace(true)
        GLCore.popMatrix()
    }

    fun doSpawnDeathParticles(mc: Minecraft, living: Entity) {
        val world = living.world

        if (living.world.isRemote) {
            val colors = arrayOf(floatArrayOf(1f / 0xFF * 0x9A, 1f / 0xFF * 0xFE, 1f / 0xFF * 0x2E), floatArrayOf(1f / 0xFF * 0x01, 1f / 0xFF * 0xFF, 1f / 0xFF * 0xFF), floatArrayOf(1f / 0xFF * 0x08, 1f / 0xFF * 0x08, 1f / 0xFF * 0x8A))

            val size = living.width * living.height
            val pieces = Math.max(Math.min(size * 64, 128f), 8f).toInt()

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
    }

    private fun useColor(mc: Minecraft, living: Entity, time: Float) {
        if (living is EntityLivingBase) {
            HealthStep.getStep(mc, living, time).glColor()
        } else {
            HealthStep.GOOD.glColor()
        }
    }

    private fun getHealthFactor(mc: Minecraft, living: Entity, time: Float): Float {
        val normalFactor = StaticPlayerHelper.getHealth(mc, living, time) / StaticPlayerHelper.getMaxHealth(living)
        val delta = 1.0f - normalFactor

        return normalFactor + delta * delta / 2 * normalFactor
    }

}
