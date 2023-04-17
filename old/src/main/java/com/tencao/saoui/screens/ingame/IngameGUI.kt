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

package be.bluexin.mcui.screens.ingame

import com.google.common.base.Predicate
import com.google.common.base.Predicates
import be.bluexin.mcui.util.Client
import com.tencao.saomclib.capabilities.getPartyCapability
import com.tencao.saomclib.party.PlayerInfo
import be.bluexin.mcui.GLCore
import be.bluexin.mcui.capabilities.getRenderData
import be.bluexin.mcui.config.ConfigHandler
import be.bluexin.mcui.config.OptionCore
import be.bluexin.mcui.screens.util.HealthStep
import be.bluexin.mcui.themes.ThemeManager
import be.bluexin.mcui.themes.elements.HudPartType
import be.bluexin.mcui.themes.util.HudDrawContext
import net.minecraft.Client.mc
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.GuiOverlayDebug
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.client.resources.I18n
import net.minecraft.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.util.EntitySelectors
import net.minecraft.util.StringUtils
import net.minecraft.util.math.AxisAlignedBB
import net.minecraftforge.client.GuiIngameForge
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType.*
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.lwjgl.opengl.GL11

@SideOnly(Side.CLIENT)
class IngameGUI(mc: Minecraft) : GuiIngameForge(mc) {

    private var eventParent: RenderGameOverlayEvent? = null
    private var offsetUsername: Int = 0
    private val debugOverlay: GuiOverlayDebugForge = GuiOverlayDebugForge(mc)

    private lateinit var context: HudDrawContext

    override fun renderGameOverlay(partialTicks: Float) {
        mc.mcProfiler.startSection("setup")
        if (!::context.isInitialized) {
            this.context = HudDrawContext()
        }
        val username = mc.player.displayNameString
        val maxNameWidth = fontRenderer.getStringWidth(username)
        val usernameBoxes = 1 + (maxNameWidth + 4) / 5
        offsetUsername = 18 + usernameBoxes * 5
        val res = ScaledResolution(mc)
        eventParent = RenderGameOverlayEvent(partialTicks, res)
        val width = res.scaledWidth
        val height = res.scaledHeight
        context.setTime(partialTicks)
        context.setScaledResolution(res)
        context.z = zLevel
        context.player = mc.player
        GLCore.glBlend(true)
        mc.mcProfiler.endSection()

        super.renderGameOverlay(partialTicks)

        if (OptionCore.ALWAYS_SHOW.isEnabled && !this.mc.playerController.shouldDrawHUD() && this.mc.renderViewEntity is Player) {
            if (renderHealth) renderHealth(width, height)
            // TODO add option//
            if (renderArmor) renderArmor(width, height)
            if (renderFood) renderFood(width, height)
            if (renderHealthMount) renderHealthMount(width, height)
            if (renderAir) renderAir(width, height)
            mc.entityRenderer.setupOverlayRendering()
        } // Basically adding what super doesn't render by default
    }

    override fun renderCrosshairs(partialTicks: Float) {
        if (OptionCore.VANILLA_UI.isEnabled) {
            super.renderCrosshairs(partialTicks)
        } else {
            ThemeManager.HUD.draw(HudPartType.CROSS_HAIR, context)
            this.renderAttackIndicator(partialTicks, resolution)
            if (OptionCore.RENDER_CROSSHAIRS.isEnabled) initiateEvents(CROSSHAIRS)
        }
    }

    override fun renderArmor(width: Int, height: Int) {
        if (OptionCore.VANILLA_UI.isEnabled) {
            super.renderArmor(width, height)
        } else {
            ThemeManager.HUD.draw(HudPartType.ARMOR, context)
            if (OptionCore.RENDER_ARMOR.isEnabled) initiateEvents(ARMOR)
        }
    }

    override fun renderHotbar(res: ScaledResolution, partialTicks: Float) {
        if (OptionCore.DEFAULT_HOTBAR.isEnabled || OptionCore.VANILLA_UI.isEnabled) super.renderHotbar(res, partialTicks)
        else {
            if (mc.playerController?.isSpectator == true) {
                this.spectatorGui.renderTooltip(res, partialTicks)
            } else {
                mc.mcProfiler.startSection("hotbar")
                ThemeManager.HUD.draw(HudPartType.HOTBAR, context)
                mc.mcProfiler.endSection()
            }
            if (OptionCore.RENDER_HOTBAR.isEnabled) initiateEvents(HOTBAR)
        }
    }

    override fun renderAir(width: Int, height: Int) {
        if (OptionCore.VANILLA_UI.isEnabled) {
            super.renderAir(width, height)
        } else {
            mc.mcProfiler.startSection("air")
            ThemeManager.HUD.draw(HudPartType.AIR, context)
            mc.mcProfiler.endSection()
            if (OptionCore.RENDER_AIR.isEnabled) initiateEvents(AIR)
        }
    }

    override fun renderAttackIndicator(p_184045_1_: Float, p_184045_2_: ScaledResolution) {
        if (true/*OptionCore.VANILLA_UI.isEnabled*/) {
            GLCore.glBindTexture(Gui.ICONS)
            super.renderAttackIndicator(p_184045_1_, p_184045_2_)
        }
        // todo: implement
    }

    override fun renderPotionEffects(resolution: ScaledResolution) {
        if (OptionCore.VANILLA_UI.isEnabled) super.renderPotionEffects(resolution)
        else {
            ThemeManager.HUD.draw(HudPartType.EFFECTS, context)
            // mc.profile("potionEffects") {
            //
            // }
        }
    }

    override fun renderPotionIcons(resolution: ScaledResolution) {
        if (OptionCore.VANILLA_UI.isEnabled) super.renderPotionIcons(resolution)
        else {
            this.renderPotionEffects(resolution)
            if (OptionCore.RENDER_POTION_ICONS.isEnabled) initiateEvents(POTION_ICONS)
        }
    }

    override fun renderHealth(width: Int, height: Int) {
        if (OptionCore.VANILLA_UI.isEnabled) {
            super.renderHealth(width, height)
        } else {
            ThemeManager.HUD.draw(HudPartType.HEALTH_BOX, context)
            if (OptionCore.RENDER_HEALTH.isEnabled) initiateEvents(HEALTH)
        }
        renderParty()
        if (OptionCore.ENEMY_ONSCREEN_HEALTH.isEnabled) renderEnemyHealth(width, height)
    }

    private fun renderParty() {
        val pt = mc.player.getPartyCapability().partyData
        if ((pt == null || !pt.isParty) && ConfigHandler.debugFakePT == 0) return

        mc.mcProfiler.startSection("party")

        GlStateManager.disableLighting()
        GLCore.glAlphaTest(true)
        GLCore.glBlend(true)

        var members: MutableList<PlayerInfo> = mutableListOf()
        if (pt?.isParty == true) {
            members = pt.getMembers().filter { !it.equals(mc.player) }.toMutableList()
            if (OptionCore.HIDE_OFFLINE_PARTY.isEnabled) members.removeIf { it.player == null }
        } else {
            for (i in 0 until ConfigHandler.debugFakePT) members.add(PlayerInfo(mc.player))
        }

        context.setPt(members)
        ThemeManager.HUD.draw(HudPartType.PARTY, context)

        GlStateManager.disableLighting()
        mc.mcProfiler.endSection()
    }

    fun renderEnemyHealth(width: Int, height: Int) {
        mc.mcProfiler.startSection("enemy health")
        val entities: MutableList<LivingEntity> = mutableListOf()
        val trackedEntity = getMouseOver(mc.renderPartialTicks)
        if (trackedEntity is LivingEntity && trackedEntity.getRenderData()?.colorStateHandler?.shouldDrawHealth() == true) context.setTargetEntity(trackedEntity)
        else context.setTargetEntity(null)
        entities.addAll(
            Client.mc.world.getEntitiesInAABBexcluding(Client.mc.player, AxisAlignedBB(Client.mc.player.position.add(-10, -5, -10), Client.mc.player.position.add(10, 5, 10))) {
                it is LivingEntity && it.getRenderData()?.isAggressive == true && it.getRenderData()?.colorStateHandler?.shouldDrawHealth() == true && !entities.contains(it)
            }.map { it as LivingEntity }.sortedBy { LivingEntity -> LivingEntity.getDistance(Client.mc.player) }.take(5)
        )
        entities.sortBy { it.health / it.maxHealth }
        context.setNearbyEntities(entities)
        val baseY = 35
        val h = 15.0
        val offset = width - 20.0
        GLCore.glCullFace(false)
        GLCore.glBlend(true)
        ThemeManager.HUD.draw(HudPartType.ENTITY_HEALTH_HUD, context)
        HealthStep
        /*

        entities.sortedBy { it.health / it.maxHealth }.forEachIndexed { index, entity ->
            GLCore.glBindTexture(StringNames.entities)
            val health = entity.health / entity.maxHealth
            if (entity.getRenderData() != null)
                GLCore.color(entity.getRenderData()!!.getColorStateHandler().colorState.rgba)
            else
                HealthStep.VERY_LOW.glColor()
            GLCore.glTexturedRectV2(offset - 1, baseY + 1.5 + index * h, zLevel.toDouble(), -79.0 * health, 14.0, 1.0, 0.0, srcWidth = 255.0, srcHeight = 30.0)
            GLCore.color(1.0F, 1.0F, 1.0F, 1.0F)
            GLCore.glTexturedRectV2(offset, baseY + index * h, zLevel.toDouble(), -80.0, 15.0, 1.0, 30.0, srcWidth = 255.0, srcHeight = 30.0)
            var name = entity.displayName.formattedText
            if (name.length > 10) name = name.substring(0, 10)
            GLCore.glString(name, Vec2d(offset - GLCore.glStringWidth(name) - 5, baseY + 1 + index * h + (13 - fontRenderer.FONT_HEIGHT) / 2), ColorUtil.DEFAULT_COLOR.rgba)
        }
        GLCore.glCullFace(true)

         */

        mc.mcProfiler.endSection()
    }

    /**
     * Gets the block or object that is being moused over.
     */
    fun getMouseOver(partialTicks: Float): Entity? {
        mc.mcProfiler.startSection("track enemy")
        val entity = mc.renderViewEntity
        var pointedEntity: Entity? = null
        if (entity != null) {
            if (mc.world != null) {
                mc.pointedEntity = null
                val distance = 32.0
                val vec3d = entity.getPositionEyes(partialTicks)
                val vec3d1 = entity.getLook(1.0f)
                val vec3d2 = vec3d.addVector(vec3d1.x * distance, vec3d1.y * distance, vec3d1.z * distance)
                val list = mc.world.getEntitiesInAABBexcluding(entity, entity.entityBoundingBox.expand(vec3d1.x * distance, vec3d1.y * distance, vec3d1.z * distance).grow(1.0, 1.0, 1.0), Predicates.and(EntitySelectors.NOT_SPECTATING, Predicate { p_apply_1_ -> p_apply_1_ != null && p_apply_1_.canBeCollidedWith() }))
                var d2 = distance * distance
                for (j in list.indices) {
                    val entity1 = list[j]
                    val axisalignedbb = entity1.entityBoundingBox.grow(entity1.collisionBorderSize.toDouble())
                    val raytraceresult = axisalignedbb.calculateIntercept(vec3d, vec3d2)
                    if (axisalignedbb.contains(vec3d)) {
                        if (d2 >= 0.0) {
                            pointedEntity = entity1
                            d2 = 0.0
                        }
                    } else if (raytraceresult != null) {
                        val d3 = vec3d.squareDistanceTo(raytraceresult.hitVec)
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
        mc.mcProfiler.endSection()
        return pointedEntity
    }

    override fun renderFood(width: Int, height: Int) {
        if (OptionCore.VANILLA_UI.isEnabled) {
            GLCore.glBindTexture(Gui.ICONS)
            super.renderFood(width, height)
        } else {
            mc.mcProfiler.startSection("food")
            ThemeManager.HUD.draw(HudPartType.FOOD, context)
            mc.mcProfiler.endSection()
            if (OptionCore.RENDER_FOOD.isEnabled) initiateEvents(FOOD)
        }
    }

    override fun renderExperience(width: Int, height: Int) {
        if (OptionCore.VANILLA_UI.isEnabled) {
            super.renderExperience(width, height)
        } else {
            if (!OptionCore.ALWAYS_SHOW.isEnabled && !this.mc.playerController.shouldDrawHUD()) return
            mc.mcProfiler.startSection("expLevel")

            ThemeManager.HUD.draw(HudPartType.EXPERIENCE, context)

            mc.mcProfiler.endSection()
            if (OptionCore.RENDER_EXPERIENCE.isEnabled) initiateEvents(EXPERIENCE)
        }
    }

    override fun renderJumpBar(width: Int, height: Int) {
        if (OptionCore.VANILLA_UI.isEnabled) {
            super.renderJumpBar(width, height)
        } else {
            // These are here because vanilla hides them when showing jump bar
            renderExperience(width, height)
            renderFood(width, height)

            mc.mcProfiler.startSection("jumpBar")
            ThemeManager.HUD.draw(HudPartType.JUMP_BAR, context)
            mc.mcProfiler.endSection()

            if (OptionCore.RENDER_JUMPBAR.isEnabled) initiateEvents(JUMPBAR)
        }
    }

    override fun renderHealthMount(width: Int, height: Int) {
        if (OptionCore.VANILLA_UI.isEnabled) {
            super.renderHealthMount(width, height)
        } else {
            ThemeManager.HUD.draw(HudPartType.MOUNT_HEALTH, context)
            if (OptionCore.RENDER_HEALTHMOUNT.isEnabled) initiateEvents(HEALTHMOUNT)
        }
    }

    override fun renderHUDText(width: Int, height: Int) {
        if (OptionCore.VANILLA_UI.isEnabled || OptionCore.DEFAULT_DEBUG.isEnabled) {
            super.renderHUDText(width, height)
        } else {
            mc.mcProfiler.startSection("forgeHudText")
            OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0)
            val listL = ArrayList<String>()
            val listR = ArrayList<String>()

            if (mc.isDemo) {
                val time = mc.world.totalWorldTime
                if (time >= 120500L) {
                    listR.add(I18n.format("demo.demoExpired"))
                } else {
                    listR.add(I18n.format("demo.remainingTime", StringUtils.ticksToElapsedTime((120500L - time).toInt())))
                }
            }

            if (this.mc.gameSettings.showDebugInfo && !pre(DEBUG)) {
                listL.addAll(debugOverlay.left)
                listR.addAll(debugOverlay.right)
                post(DEBUG)
            }

            val event = RenderGameOverlayEvent.Text(eventParent!!, listL, listR)
            if (!MinecraftForge.EVENT_BUS.post(event)) {
                var top = 20
                for (msg in listL) {
                    drawRect(1, top - 1, 2 + fontRenderer.getStringWidth(msg) + 1, top + fontRenderer.FONT_HEIGHT - 1, -1873784752)
                    fontRenderer.drawString(msg, 2, top, 14737632)
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
                    drawRect(left - 1, top - 1, left + w + 1, top + fontRenderer.FONT_HEIGHT - 1, -1873784752)
                    fontRenderer.drawString(msg, left, top, 14737632)
                    top += fontRenderer.FONT_HEIGHT
                }
            }

            mc.mcProfiler.endSection()
            post(TEXT)
        }
    }

    fun initiateEvents(type: ElementType) {
        if (!pre(type)) post(type)
    }

    // c/p from GuiIngameForge
    /**
     * Returns true if cancelled.
     */
    private fun pre(type: ElementType): Boolean {
        return MinecraftForge.EVENT_BUS.post(RenderGameOverlayEvent.Pre(eventParent!!, type))
    }

    private fun post(type: ElementType) {
        MinecraftForge.EVENT_BUS.post(RenderGameOverlayEvent.Post(eventParent!!, type))
    }

    private inner class GuiOverlayDebugForge constructor(mc: Minecraft) : GuiOverlayDebug(mc) {

        override fun renderDebugInfoLeft() {}

        override fun renderDebugInfoRight(res: ScaledResolution) {}

        val left: List<String>
            get() = this.call()

        val right: MutableList<String>
            get() = this.getDebugInfoRight<Comparable<Any>>()
    }
}
