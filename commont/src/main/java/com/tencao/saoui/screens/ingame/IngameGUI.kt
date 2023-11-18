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

package com.tencao.saoui.screens.ingame

import com.mojang.blaze3d.vertex.PoseStack
import com.tencao.saoui.GLCore
import com.tencao.saoui.config.ConfigHandler
import com.tencao.saoui.config.OptionCore
import com.tencao.saoui.themes.ThemeManager
import com.tencao.saoui.themes.elements.HudPartType
import com.tencao.saoui.themes.util.HudDrawContext
import com.tencao.saoui.util.Client
import com.tencao.saoui.util.ClientUtil
import com.tencao.saoui.util.PartyUtil
import com.tencao.saoui.util.render.colorStateHandler
import com.tencao.saoui.util.scaledWidth
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import net.minecraft.client.multiplayer.PlayerInfo
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.player.Player
import net.minecraft.world.phys.AABB

class IngameGUI(val mc: Minecraft) : Gui(mc) {

    private var offsetUsername: Int = 0
    private var partialTicks: Float = 0f
    val zLevel get() = this.blitOffset

    override fun render(poseStack: PoseStack, partialTicks: Float) {
        this.partialTicks = partialTicks
        if (OptionCore.VANILLA_UI.isEnabled){
            super.render(poseStack, partialTicks)
        }
        else {
            mc.profiler.push("setup")
            /*
        if (this.mc.gameSettings.viewBobbing) {
            GLCore.pushMatrix()
            applyBobbing(partialTicks)
        }*/
            val username = mc.player!!.displayName.contents
            val maxNameWidth = font.width(username)
            val usernameBoxes = 1 + (maxNameWidth + 4) / 5
            offsetUsername = 18 + usernameBoxes * 5
            if (mc.player != null) {
                context.setTime(partialTicks)
            }
            context.z = blitOffset.toFloat()
            GLCore.glBlend(true)
            mc.profiler.pop()

            if (OptionCore.ALWAYS_SHOW.isEnabled && !this.mc.gameMode!!.canHurtPlayer() && this.mc.cameraEntity is Player) {
                renderPlayerHealth(poseStack)
                renderArmor(poseStack)
                renderFood(poseStack)
                renderHealthMount(poseStack)
                renderAir(poseStack)
                renderCrosshair(poseStack)
                renderHotbar(poseStack)
                renderPotionIcons(poseStack)
                renderExperience(poseStack)
                renderHorseJumpBar(poseStack)

                // mc.entityRenderer.setupOverlayRendering()
            } // Basically adding what super doesn't render by default

            /*
        if (this.mc.gameSettings.viewBobbing) {
            GLCore.popMatrix()
        }*/
        }
    }

     fun renderCrosshair(poseStack: PoseStack) {
         ThemeManager.HUD.draw(HudPartType.CROSS_HAIR, context, poseStack)
         // TODO Fix this

         //super.renderCrosshair(mStack)
         //if (OptionCore.RENDER_CROSSHAIRS.isEnabled) initiateEvents(mStack, CROSSHAIRS)

     }

     fun renderArmor(poseStack: PoseStack) {
         ThemeManager.HUD.draw(HudPartType.ARMOR, context, poseStack)
     }

     fun renderHotbar(poseStack: PoseStack) {
         if (mc.gameMode?.isAlwaysFlying == true) {
             this.spectatorGui.renderHotbar(poseStack, partialTicks)
         } else {
             mc.profiler.push("hotbar")
             ThemeManager.HUD.draw(HudPartType.HOTBAR, context, poseStack)
             mc.profiler.pop()
         }
     }

     fun renderAir(poseStack: PoseStack) {
         mc.profiler.push("air")
         ThemeManager.HUD.draw(HudPartType.AIR, context, poseStack)
         mc.profiler.pop()

     }

    fun renderPotionIcons(poseStack: PoseStack) {
        ThemeManager.HUD.draw(HudPartType.EFFECTS, context, poseStack)
    }

     fun renderPlayerHealth(poseStack: PoseStack) {
         // if (pre(HEALTH)) return
         ThemeManager.HUD.draw(HudPartType.HEALTH_BOX, context, poseStack)

         renderParty(poseStack)
         if (OptionCore.ENEMY_ONSCREEN_HEALTH.isEnabled) renderEnemyHealth(poseStack)
     }

    private fun renderParty(poseStack: PoseStack) {
        mc.profiler.push("party")

        GLCore.lighting(false)
        GLCore.glAlphaTest(true)
        GLCore.glBlend(true)

        val members: List<PlayerInfo> =
        if (ConfigHandler.debugFakePT > 0){
            PartyUtil.getFakePartyInfo()
        }
        else if (ClientUtil.isFTBTeamsLoaded){
            PartyUtil.getPartyInfo()
        }
        else return


        context.setPt(members)
        ThemeManager.HUD.draw(HudPartType.PARTY, context, poseStack)

        GLCore.lighting(false)
        mc.profiler.pop()
    }

    fun renderEnemyHealth(poseStack: PoseStack) {

        mc.profiler.push("enemy health")
        val entities: MutableList<LivingEntity> = mutableListOf()
        val trackedEntity = getMouseOver(mc.frameTime)
        if (trackedEntity is LivingEntity && trackedEntity.colorStateHandler.shouldDrawHealth()) context.setTargetEntity(trackedEntity)
        entities.addAll(
            Client.minecraft.level!!.getEntities(Client.minecraft.player, AABB(Client.minecraft.player!!.blockPosition().offset(-10, -5, -10), Client.minecraft.player!!.blockPosition().offset(10, 5, 10))) {
                it is Mob && it.isAggressive && it.colorStateHandler.shouldDrawHealth() && !entities.contains(it)
            }.map { it as LivingEntity }.sortedBy { entityLivingBase -> entityLivingBase.distanceTo(Client.minecraft.player!!) }.take(5)
        )
        entities.sortBy { it.health / it.maxHealth }
        context.setNearbyEntities(entities)
        val baseY = 35
        val h = 15.0
        val offset = scaledWidth - 20.0
        GLCore.glCullFace(false)
        GLCore.glBlend(true)
        ThemeManager.HUD.draw(HudPartType.ENTITY_HEALTH_HUD, context, poseStack)

        /*
        entities.sortedBy { it.health / it.maxHealth }.forEachIndexed { index, entity ->
            GLCore.glBindTexture(StringNames.entities)

            val health = entity.health / entity.maxHealth
            if (entity.getRenderData() != null)
                GLCore.color(entity.getRenderData()!!.colorStateHandler.colorState.rgba)
            else
                HealthStep.VERY_LOW.glColor()
            GLCore.glTexturedRectV2(offset - 1, baseY + 1.5 + index * h, zLevel.toDouble(), -79.0 * health, 14.0, 1.0, 0.0, srcWidth = 255.0, srcHeight = 30.0)
            GLCore.color(1.0F, 1.0F, 1.0F, 1.0F)
            GLCore.glTexturedRectV2(offset, baseY + index * h, zLevel.toDouble(), -80.0, 15.0, 1.0, 30.0, srcWidth = 255.0, srcHeight = 30.0)
            var name = entity.scoreboardName
            if (name.length > 10) name = name.substring(0, 10)
            GLCore.glString(mStack, name, Vec2d(offset - GLCore.glStringWidth(name) - 5, baseY + 1 + index * h + (13 - fontRenderer.FONT_HEIGHT) / 2), ColorUtil.DEFAULT_COLOR.rgba)
        }*/
        GLCore.glCullFace(true)

        mc.profiler.pop()
    }

    /**
     * Gets the block or object that is being moused over.
     */

    fun getMouseOver(partialTicks: Float): Entity? {

        mc.profiler.push("track enemy")
        val entity = mc.cameraEntity
        var pointedEntity: Entity? = null
        if (entity != null) {
            if (mc.level != null) {
                mc.crosshairPickEntity = null
                val d0 = 64.0
                val vec3d = entity.getEyePosition(partialTicks)
                val vec3d1 = entity.getViewVector(1.0f)
                val vec3d2 = vec3d.add(vec3d1.x * d0, vec3d1.y * d0, vec3d1.z * d0)
                val list = mc.level!!.getEntities(entity, entity.boundingBox.expandTowards(vec3d1.x * d0, vec3d1.y * d0, vec3d1.z * d0).inflate(1.0, 1.0, 1.0)){ !entity.isSpectator}
                var d2 = d0
                for (j in list.indices) {
                    val entity1 = list[j]
                    val axisalignedbb = entity1.boundingBox.inflate(entity1.pickRadius.toDouble())
                    val raytraceresult = axisalignedbb.intersect(AABB(vec3d, vec3d2))
                    if (axisalignedbb.contains(vec3d)) {
                        if (d2 >= 0.0) {
                            pointedEntity = entity1
                            d2 = 0.0
                        }
                    } else if (raytraceresult != null) {
                        val d3 = vec3d.distanceTo(raytraceresult.center)
                        if (d3 < d2 || d2 == 0.0) {
                            if (entity1.rootVehicle === entity.rootVehicle && entity1.skipAttackInteraction(entity1.vehicle)) {
                                if (d2 == 0.0) {
                                    pointedEntity = entity1
                                }
                            } else {
                                pointedEntity = entity1
                                d2 = d3
                            }
                        }
                    }
                }
            }
        }
        mc.profiler.pop()
        return pointedEntity
    }

     fun renderFood(poseStack: PoseStack) {
         mc.profiler.push("food")
         ThemeManager.HUD.draw(HudPartType.FOOD, context, poseStack)
         mc.profiler.pop()

     }

    /**
     * RenderExpBar
     */
     fun renderExpBar(poseStack: PoseStack) {
        if (!OptionCore.ALWAYS_SHOW.isEnabled && !this.mc.gameMode!!.canHurtPlayer()) return
        mc.profiler.push("expLevel")

        ThemeManager.HUD.draw(HudPartType.EXPERIENCE, context, poseStack)

        mc.profiler.pop()

    }

     fun renderExperience( poseStack: PoseStack) {
        renderExpBar(poseStack)
    }

     fun renderHorseJumpBar(poseStack: PoseStack) {
         // These are here because vanilla hides them when showing jump bar
         renderExpBar(poseStack)
         renderFood(poseStack)

         // if (pre(JUMPBAR)) return
         mc.profiler.push("jumpBar")
         ThemeManager.HUD.draw(HudPartType.JUMP_BAR, context, poseStack)
         mc.profiler.pop()

     }

     fun renderHealthMount(poseStack: PoseStack) {
         // if (pre(HEALTHMOUNT)) return
         ThemeManager.HUD.draw(HudPartType.MOUNT_HEALTH, context, poseStack)

     }

    companion object {
        private var context: HudDrawContext = HudDrawContext()
    }
}
