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

package com.saomc.saoui.screens.ingame

import be.bluexin.saomclib.capabilities.PartyCapability
import be.bluexin.saomclib.party.PlayerInfo
import be.bluexin.saomclib.profile
import com.saomc.saoui.GLCore
import com.saomc.saoui.config.ConfigHandler
import com.saomc.saoui.config.OptionCore
import com.saomc.saoui.themes.ThemeLoader
import com.saomc.saoui.themes.elements.HudPartType
import com.saomc.saoui.themes.util.HudDrawContext
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.GuiOverlayDebug
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.client.resources.I18n
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.StringUtils
import net.minecraftforge.client.GuiIngameForge
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType.*
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.lwjgl.opengl.GL11
import java.util.*

@SideOnly(Side.CLIENT)
class IngameGUI(mc: Minecraft) : GuiIngameForge(mc) {

    private var eventParent: RenderGameOverlayEvent? = null
    private var offsetUsername: Int = 0
    private val debugOverlay: GuiOverlayDebugForge

    @Deprecated("") // use getContext() instead of directly calling this
    private var ctx: HudDrawContext? = null

    init {
        this.debugOverlay = GuiOverlayDebugForge(mc)
    }

    override fun renderGameOverlay(partialTicks: Float) {
        mc.profiler.startSection("setup")
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
        mc.profiler.endSection()

        super.renderGameOverlay(partialTicks)

        if (OptionCore.FORCE_HUD.isEnabled && !this.mc.playerController.shouldDrawHUD() && this.mc.renderViewEntity is EntityPlayer) {
            if (GuiIngameForge.renderHealth) renderHealth(width, height)
            if (GuiIngameForge.renderArmor) renderArmor(width, height)
            if (GuiIngameForge.renderFood) renderFood(width, height)
            if (GuiIngameForge.renderHealthMount) renderHealthMount(width, height)
            if (GuiIngameForge.renderAir) renderAir(width, height)
            mc.entityRenderer.setupOverlayRendering()
        } // Basically adding what super doesn't render by default

    }

    override fun renderCrosshairs(partialTicks: Float) {
        if (OptionCore.VANILLA_UI.isEnabled)
            super.renderCrosshairs(partialTicks)
        else {
            if (pre(CROSSHAIRS)) return
            ThemeLoader.HUD.draw(HudPartType.CROSS_HAIR, context)
            this.renderAttackIndicator(partialTicks, resolution)
            post(CROSSHAIRS)
        }
    }

    override fun renderArmor(width: Int, height: Int) {
        if (OptionCore.VANILLA_UI.isEnabled)
            super.renderArmor(width, height)
        else {
            if (pre(ARMOR)) return
            ThemeLoader.HUD.draw(HudPartType.ARMOR, context)
            post(ARMOR)
        }
    }

    override fun renderHotbar(res: ScaledResolution, partialTicks: Float) {
        if (OptionCore.DEFAULT_HOTBAR.isEnabled) super.renderHotbar(res, partialTicks)
        else {
            if (pre(HOTBAR)) return
            if (mc.playerController?.isSpectator == true)
                this.spectatorGui.renderTooltip(res, partialTicks)
            else {
                mc.profiler.startSection("hotbar")
                ThemeLoader.HUD.draw(HudPartType.HOTBAR, context)
                mc.profiler.endSection()
            }
            post(HOTBAR)
        }
    }

    override fun renderAir(width: Int, height: Int) {
        if (OptionCore.VANILLA_UI.isEnabled)
            super.renderAir(width, height)
        else {
            if (pre(AIR)) return
            mc.profile("air") {
                ThemeLoader.HUD.draw(HudPartType.AIR, context)
            }
            post(AIR)
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
            mc.profile("potionEffects") {
                ThemeLoader.HUD.draw(HudPartType.EFFECTS, context)
            }
        }
    }

    override fun renderPotionIcons(resolution: ScaledResolution) {
        if (pre(POTION_ICONS)) return
        this.renderPotionEffects(resolution)
        post(POTION_ICONS)
    }

    override fun renderHealth(width: Int, height: Int) {
        if (OptionCore.VANILLA_UI.isEnabled)
            super.renderHealth(width, height)
        else {
            if (pre(HEALTH)) return
            mc.profile("health") { ThemeLoader.HUD.draw(HudPartType.HEALTH_BOX, context) }
            post(HEALTH)
        }
        renderParty()
    }

    private fun renderParty() {
        val pt = mc.player.getCapability(PartyCapability.CAP_INSTANCE, null)!!.party
        if ((pt == null || !pt.isParty) && ConfigHandler.debugFakePT == 0) return

        mc.profiler.startSection("party")

        GLCore.glAlphaTest(true)
        GLCore.glBlend(true)

        val ourPlayerInfo = PlayerInfo(mc.player)
        val members = if (pt?.isParty == true) pt.membersInfo.filter { it != ourPlayerInfo }.toList()
        else (1..ConfigHandler.debugFakePT).map { ourPlayerInfo }

        context.setPt(members)
        ThemeLoader.HUD.draw(HudPartType.PARTY, context)

        mc.profiler.endSection()
    }

    override fun renderFood(width: Int, height: Int) {
        if (OptionCore.VANILLA_UI.isEnabled) {
            GLCore.glBindTexture(Gui.ICONS)
            super.renderFood(width, height)
        } else {
            if (pre(FOOD)) return
            mc.profile("foodNew") { ThemeLoader.HUD.draw(HudPartType.FOOD, context) }
            post(FOOD)
        }
    }

    override fun renderExperience(width: Int, height: Int) {
        if (OptionCore.VANILLA_UI.isEnabled)
            super.renderExperience(width, height)
        else {
            if (pre(EXPERIENCE)) return
            if (!OptionCore.FORCE_HUD.isEnabled && !this.mc.playerController.shouldDrawHUD()) return
            mc.profiler.startSection("expLevel")

            ThemeLoader.HUD.draw(HudPartType.EXPERIENCE, context)

            mc.profiler.endSection()
            post(EXPERIENCE)
        }
    }

    override fun renderJumpBar(width: Int, height: Int) {
        if (OptionCore.VANILLA_UI.isEnabled)
            super.renderJumpBar(width, height)
        else {
            // These are here because vanilla hides them when showing jump bar
            renderExperience(width, height)
            renderFood(width, height)

            if (pre(JUMPBAR)) return
            mc.profiler.startSection("jumpBar")
            ThemeLoader.HUD.draw(HudPartType.JUMP_BAR, context)
            mc.profiler.endSection()

            post(JUMPBAR)
        }
    }

    override fun renderHealthMount(width: Int, height: Int) {
        if (OptionCore.VANILLA_UI.isEnabled)
            super.renderHealthMount(width, height)
        else {
            if (pre(HEALTHMOUNT)) return
            ThemeLoader.HUD.draw(HudPartType.MOUNT_HEALTH, context)
            post(HEALTHMOUNT)
        }
    }

    override fun renderHUDText(width: Int, height: Int) {
        if (OptionCore.VANILLA_UI.isEnabled || OptionCore.DEFAULT_DEBUG.isEnabled)
            super.renderHUDText(width, height)
        else {
            mc.profiler.startSection("forgeHudText")
            OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0)
            val listL = ArrayList<String>()
            val listR = ArrayList<String>()

            if (mc.isDemo) {
                val time = mc.world.totalWorldTime
                if (time >= 120500L)
                    listR.add(I18n.format("demo.demoExpired"))
                else
                    listR.add(I18n.format("demo.remainingTime", StringUtils.ticksToElapsedTime((120500L - time).toInt())))
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

            mc.profiler.endSection()
            post(TEXT)
        }
    }

    // c/p from GuiIngameForge
    private fun pre(type: ElementType): Boolean {
        return MinecraftForge.EVENT_BUS.post(RenderGameOverlayEvent.Pre(eventParent!!, type))
    }

    private fun post(type: ElementType) {
        MinecraftForge.EVENT_BUS.post(RenderGameOverlayEvent.Post(eventParent!!, type))
    }

    private val context: HudDrawContext
        get() {
            if (ctx == null) this.ctx = HudDrawContext(mc.player, mc, itemRenderer)
            return ctx!!
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
