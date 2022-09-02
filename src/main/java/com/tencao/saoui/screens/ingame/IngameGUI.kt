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

import com.mojang.blaze3d.matrix.MatrixStack
import com.tencao.saomclib.Client
import com.tencao.saomclib.GLCore
import com.tencao.saomclib.capabilities.getPartyCapability
import com.tencao.saomclib.party.PlayerInfo
import com.tencao.saoui.capabilities.getRenderData
import com.tencao.saoui.config.ConfigHandler
import com.tencao.saoui.config.OptionCore
import com.tencao.saoui.themes.ThemeLoader
import com.tencao.saoui.themes.elements.HudPartType
import com.tencao.saoui.themes.util.HudDrawContext
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.overlay.DebugOverlayGui
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.EntityPredicates.NOT_SPECTATING
import net.minecraft.util.math.AxisAlignedBB
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType.*
import net.minecraftforge.client.gui.ForgeIngameGui
import net.minecraftforge.common.MinecraftForge

class IngameGUI(mc: Minecraft) : ForgeIngameGui(mc) {

    private var eventParent: RenderGameOverlayEvent? = null
    private var offsetUsername: Int = 0
    private val debugOverlay: GuiOverlayDebugForge = GuiOverlayDebugForge(mc)
    val zLevel get() = this.blitOffset

    override fun renderIngameGui(mStack: MatrixStack, partialTicks: Float) {
        mc.profiler.startSection("setup")
        /*
        if (this.mc.gameSettings.viewBobbing) {
            GLCore.pushMatrix()
            applyBobbing(partialTicks)
        }*/
        val username = mc.player!!.displayName.unformattedComponentText
        val maxNameWidth = fontRenderer.getStringWidth(username)
        val usernameBoxes = 1 + (maxNameWidth + 4) / 5
        offsetUsername = 18 + usernameBoxes * 5
        eventParent = RenderGameOverlayEvent(mStack, partialTicks, mc.mainWindow)
        val width = scaledWidth
        val height = scaledHeight
        if (mc.player != null) {
            context.setTime(partialTicks)
        }
        context.z = blitOffset.toFloat()
        GLCore.glBlend(true)
        mc.profiler.endSection()

        super.renderIngameGui(mStack, partialTicks)

        if (OptionCore.ALWAYS_SHOW.isEnabled && !this.mc.playerController!!.shouldDrawHUD() && this.mc.renderViewEntity is PlayerEntity) {
            if (renderHealth) renderHealth(width, height, mStack)
            // TODO add option//
            if (renderArmor) renderArmor(mStack, width, height)
            if (renderFood) renderFood(width, height, mStack)
            if (renderHealthMount) renderHealthMount(width, height, mStack)
            if (renderAir) renderAir(width, height, mStack)

            // mc.entityRenderer.setupOverlayRendering()
        } // Basically adding what super doesn't render by default

        /*
        if (this.mc.gameSettings.viewBobbing) {
            GLCore.popMatrix()
        }*/
    }

    override fun renderCrosshair(mStack: MatrixStack) {
        if (OptionCore.VANILLA_UI.isEnabled) {
            super.renderCrosshair(mStack)
        } else {
            ThemeLoader.HUD.draw(HudPartType.CROSS_HAIR, context, mStack)
            // TODO Fix this

            super.renderCrosshair(mStack)
            if (OptionCore.RENDER_CROSSHAIRS.isEnabled) initiateEvents(mStack, CROSSHAIRS)
        }
    }

    override fun renderArmor(mStack: MatrixStack, width: Int, height: Int) {
        if (OptionCore.VANILLA_UI.isEnabled) {
            super.renderArmor(mStack, width, height)
        } else {
            ThemeLoader.HUD.draw(HudPartType.ARMOR, context, mStack)
            if (OptionCore.RENDER_ARMOR.isEnabled) initiateEvents(mStack, ARMOR)
        }
    }

    override fun renderHotbar(partialTicks: Float, mStack: MatrixStack) {
        if (OptionCore.DEFAULT_HOTBAR.isEnabled || OptionCore.VANILLA_UI.isEnabled) super.renderHotbar(partialTicks, mStack)
        else {
            if (mc.playerController?.isSpectatorMode == true) {
                this.spectatorGui.func_238528_a_(mStack, partialTicks)
            } else {
                mc.profiler.startSection("hotbar")
                ThemeLoader.HUD.draw(HudPartType.HOTBAR, context, mStack)
                mc.profiler.endSection()
            }
            if (OptionCore.RENDER_HOTBAR.isEnabled) initiateEvents(mStack, HOTBAR)
        }
    }

    override fun renderAir(width: Int, height: Int, mStack: MatrixStack) {
        if (OptionCore.VANILLA_UI.isEnabled) {
            super.renderAir(width, height, mStack)
        } else {
            mc.profiler.startSection("air")
            ThemeLoader.HUD.draw(HudPartType.AIR, context, mStack)
            mc.profiler.endSection()
            if (OptionCore.RENDER_AIR.isEnabled) initiateEvents(mStack, AIR)
        }
    }

    override fun renderPotionIcons(mStack: MatrixStack) {
        if (OptionCore.VANILLA_UI.isEnabled) super.renderPotionIcons(mStack)
        else {
            ThemeLoader.HUD.draw(HudPartType.EFFECTS, context, mStack)
            // mc.profile("potionEffects") {
            //
            // }
            if (OptionCore.RENDER_POTION_ICONS.isEnabled) initiateEvents(mStack, POTION_ICONS)
        }
    }

    override fun renderHealth(width: Int, height: Int, mStack: MatrixStack) {
        if (OptionCore.VANILLA_UI.isEnabled) {
            super.renderHealth(width, height, mStack)
        } else {
            // if (pre(HEALTH)) return
            ThemeLoader.HUD.draw(HudPartType.HEALTH_BOX, context, mStack)
            if (OptionCore.RENDER_HEALTH.isEnabled) initiateEvents(mStack, HEALTH)
        }
        renderParty(mStack)
        if (OptionCore.ENEMY_ONSCREEN_HEALTH.isEnabled) renderEnemyHealth(width, height, mStack)
    }

    private fun renderParty(mStack: MatrixStack) {
        val pt = mc.player?.getPartyCapability()?.partyData ?: return
        if (!pt.isParty && ConfigHandler.debugFakePT.get() == 0) return

        mc.profiler.startSection("party")

        GLCore.lighting(false)
        GLCore.glAlphaTest(true)
        GLCore.glBlend(true)

        var members: MutableList<PlayerInfo> = mutableListOf()
        if (pt.isParty) {
            members = pt.getMembers().filter { !it.equals(mc.player) }.toMutableList()
            if (OptionCore.HIDE_OFFLINE_PARTY.isEnabled) members.removeIf { it.player == null }
        } else {
            for (i in 0 until ConfigHandler.debugFakePT.get()) members.add(PlayerInfo(mc.player!!))
        }

        context.setPt(members)
        ThemeLoader.HUD.draw(HudPartType.PARTY, context, mStack)

        GLCore.lighting(false)
        mc.profiler.endSection()
    }

    fun renderEnemyHealth(width: Int, height: Int, mStack: MatrixStack) {
        mc.profiler.startSection("enemy health")
        val entities: MutableList<LivingEntity> = mutableListOf()
        val trackedEntity = getMouseOver(mc.renderPartialTicks)
        if (trackedEntity is LivingEntity && trackedEntity.getRenderData()?.colorStateHandler?.shouldDrawHealth() == true) context.setTargetEntity(trackedEntity)
        entities.addAll(
            Client.minecraft.world!!.getEntitiesInAABBexcluding(Client.minecraft.player, AxisAlignedBB(Client.minecraft.player!!.position.add(-10, -5, -10), Client.minecraft.player!!.position.add(10, 5, 10))) {
                it is LivingEntity && it.getRenderData()?.isAggressive == true && it.getRenderData()?.colorStateHandler?.shouldDrawHealth() == true && !entities.contains(it)
            }.map { it as LivingEntity }.sortedBy { entityLivingBase -> entityLivingBase.getDistance(Client.minecraft.player!!) }.take(5)
        )
        entities.sortBy { it.health / it.maxHealth }
        context.setNearbyEntities(entities)
        val baseY = 35
        val h = 15.0
        val offset = width - 20.0
        GLCore.glCullFace(false)
        GLCore.glBlend(true)
        ThemeLoader.HUD.draw(HudPartType.ENTITY_HEALTH_HUD, context, mStack)

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

        mc.profiler.endSection()
    }

    /**
     * Gets the block or object that is being moused over.
     */
    fun getMouseOver(partialTicks: Float): Entity? {
        mc.profiler.startSection("track enemy")
        val entity = mc.renderViewEntity
        var pointedEntity: Entity? = null
        if (entity != null) {
            if (mc.world != null) {
                mc.pointedEntity = null
                val d0 = 64.0
                val vec3d = entity.getEyePosition(partialTicks)
                val vec3d1 = entity.getLook(1.0f)
                val vec3d2 = vec3d.add(vec3d1.x * d0, vec3d1.y * d0, vec3d1.z * d0)
                val list = mc.world!!.getEntitiesInAABBexcluding(entity, entity.boundingBox.expand(vec3d1.x * d0, vec3d1.y * d0, vec3d1.z * d0).grow(1.0, 1.0, 1.0), NOT_SPECTATING)
                var d2 = d0
                for (j in list.indices) {
                    val entity1 = list[j]
                    val axisalignedbb = entity1.boundingBox.grow(entity1.collisionBorderSize.toDouble())
                    val raytraceresult = axisalignedbb.intersect(AxisAlignedBB(vec3d, vec3d2))
                    if (axisalignedbb.contains(vec3d)) {
                        if (d2 >= 0.0) {
                            pointedEntity = entity1
                            d2 = 0.0
                        }
                    } else if (raytraceresult != null) {
                        val d3 = vec3d.distanceTo(raytraceresult.center)
                        if (d3 < d2 || d2 == 0.0) {
                            if (entity1.lowestRidingEntity === entity.lowestRidingEntity && !entity1.canRiderInteract()) {
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
        mc.profiler.endSection()
        return pointedEntity
    }

    override fun renderFood(width: Int, height: Int, mStack: MatrixStack) {
        if (OptionCore.VANILLA_UI.isEnabled) {
            super.renderFood(width, height, mStack)
        } else {
            mc.profiler.startSection("food")
            ThemeLoader.HUD.draw(HudPartType.FOOD, context, mStack)
            mc.profiler.endSection()
            if (OptionCore.RENDER_FOOD.isEnabled) initiateEvents(mStack, FOOD)
        }
    }

    /**
     * RenderExpBar
     */
    override fun renderExpBar(mStack: MatrixStack, xPos: Int) {
        if (OptionCore.VANILLA_UI.isEnabled) {
            super.renderExpBar(mStack, xPos)
        } else {
            if (!OptionCore.ALWAYS_SHOW.isEnabled && !this.mc.playerController!!.shouldDrawHUD()) return
            mc.profiler.startSection("expLevel")

            ThemeLoader.HUD.draw(HudPartType.EXPERIENCE, context, mStack)

            mc.profiler.endSection()
            if (OptionCore.RENDER_EXPERIENCE.isEnabled) initiateEvents(mStack, EXPERIENCE)
        }
    }

    override fun renderExperience(x: Int, mStack: MatrixStack) {
        if (OptionCore.VANILLA_UI.isEnabled) {
            super.renderExperience(x, mStack)
        } else renderExpBar(mStack, x)
    }

    override fun renderHorseJumpBar(mStack: MatrixStack, x: Int) {
        if (OptionCore.VANILLA_UI.isEnabled) {
            super.renderHorseJumpBar(mStack, x)
        } else {
            // These are here because vanilla hides them when showing jump bar
            renderExpBar(mStack, x)
            renderFood(scaledWidth, scaledHeight, mStack)

            // if (pre(JUMPBAR)) return
            mc.profiler.startSection("jumpBar")
            ThemeLoader.HUD.draw(HudPartType.JUMP_BAR, context, mStack)
            mc.profiler.endSection()

            if (OptionCore.RENDER_JUMPBAR.isEnabled) initiateEvents(mStack, JUMPBAR)
        }
        super.renderHorseJumpBar(mStack, x)
    }

    override fun renderHealthMount(width: Int, height: Int, mStack: MatrixStack) {
        if (OptionCore.VANILLA_UI.isEnabled) {
            super.renderHealthMount(width, height, mStack)
        } else {
            // if (pre(HEALTHMOUNT)) return
            ThemeLoader.HUD.draw(HudPartType.MOUNT_HEALTH, context, mStack)
            if (OptionCore.RENDER_HEALTHMOUNT.isEnabled) initiateEvents(mStack, HEALTHMOUNT)
        }
    }

    override fun renderHUDText(width: Int, height: Int, mStack: MatrixStack) {
        super.renderHUDText(width, height, mStack)
        /*
        if (OptionCore.VANILLA_UI.isEnabled || OptionCore.DEFAULT_DEBUG.isEnabled)
            super.renderHUDText(width, height, mStack)
        else {
            mc.profiler.startSection("forgeHudText")
            GLCore.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0)
            val listL = ArrayList<String>()
            val listR = ArrayList<String>()

            if (mc.isDemo) {
                val time = mc.world!!.worldInfo.gameTime
                if (time >= 120500L)
                    listR.add(I18n.format("demo.demoExpired"))
                else
                    listR.add(I18n.format("demo.remainingTime", StringUtils.ticksToElapsedTime((120500L - time).toInt())))
            }

            if (this.mc.gameSettings.showDebugInfo && !pre(mStack, DEBUG)) {
                listL.addAll(debugOverlay.left)
                listR.addAll(debugOverlay.right)
                post(mStack, DEBUG)
            }

            val event = RenderGameOverlayEvent.Text(mStack, eventParent!!, listL, listR)
            if (!MinecraftForge.EVENT_BUS.post(event)) {
                var top = 20
                for (msg in listL) {
                    GLCore.drawGradientRect(1.0, top - 1.0, 2.0 + fontRenderer.getStringWidth(msg) + 1, top + fontRenderer.FONT_HEIGHT - 1.0, 0.0, -1873784752)
                    fontRenderer.drawString(mStack, msg, 2f, top.toFloat(), 14737632)
                    top += fontRenderer.FONT_HEIGHT
                }

                top = 2
                for (msg in listR) {
                    val w = fontRenderer.getStringWidth(msg)

                    val slotsY = (height - 9 * 22) / 2
                    //                        (res.getScaledHeight() - (slotCount * 22)) / 2;

                    /*for (int i = 0; i < slotCount; i++) {
                    GLCore.color(i == inv.currentItem ? 0xFFBA66AA : 0xCDCDCDAA);
                    GLCore.glTexturedRect(res.getScaledWidth() - 24, slotsY + (22 * i), zLevel, 0, 25, 20, 20);
                }*/

                    val left = width - (if (OptionCore.HOR_HOTBAR.isEnabled || top < slotsY - fontRenderer.FONT_HEIGHT - 2) 2 else 26) - w
                    GLCore.drawGradientRect(left - 1.0, top - 1.0, left + w + 1.0, top + fontRenderer.FONT_HEIGHT - 1.0, 0.0, -1873784752)
                    fontRenderer.drawString(mStack, msg, left.toFloat(), top.toFloat(), 14737632)
                    top += fontRenderer.FONT_HEIGHT
                }
            }

            mc.profiler.endSection()
            post(mStack, TEXT)
        }*/
    }

    fun initiateEvents(mStack: MatrixStack, type: ElementType) {
        if (!pre(mStack, type)) post(mStack, type)
    }

    // c/p from GuiIngameForge
    private fun pre(mStack: MatrixStack, type: ElementType): Boolean {
        return MinecraftForge.EVENT_BUS.post(RenderGameOverlayEvent.Pre(mStack, eventParent!!, type))
    }

    private fun post(mStack: MatrixStack, type: ElementType) {
        MinecraftForge.EVENT_BUS.post(RenderGameOverlayEvent.Post(mStack, eventParent!!, type))
    }

    private inner class GuiOverlayDebugForge constructor(mc: Minecraft) : DebugOverlayGui(mc) {

        val left: MutableList<String>
            get() = debugInfoLeft

        val right: MutableList<String>
            get() = debugInfoRight
    }

    companion object {
        private var context: HudDrawContext = HudDrawContext()
    }
}
