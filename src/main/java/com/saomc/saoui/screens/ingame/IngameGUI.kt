package com.saomc.saoui.screens.ingame

import be.bluexin.saomclib.capabilities.PartyCapability
import be.bluexin.saomclib.profile
import com.saomc.saoui.GLCore
import com.saomc.saoui.config.ConfigHandler
import com.saomc.saoui.config.OptionCore
import com.saomc.saoui.neo.screens.ScreenGUI
import com.saomc.saoui.social.StaticPlayerHelper
import com.saomc.saoui.themes.ThemeLoader
import com.saomc.saoui.themes.elements.HudPartType
import com.saomc.saoui.themes.util.HudDrawContext
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiOverlayDebug
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.client.resources.I18n
import net.minecraft.entity.EntityLivingBase
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
        mc.mcProfiler.startSection("setup")
        val username = mc.player.displayNameString
        val maxNameWidth = fontRenderer!!.getStringWidth(username)
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
        if (pre(CROSSHAIRS)) return
        if (mc.currentScreen is ScreenGUI) return
        ThemeLoader.HUD.draw(HudPartType.CROSS_HAIR, context) // TODO: rework
        if (OptionCore.CROSS_HAIR.isEnabled) {
            super.renderCrosshairs(partialTicks)
        }
        post(CROSSHAIRS)
    }

    override fun renderArmor(width: Int, height: Int) {
        if (OptionCore.VANILLA_UI.isEnabled)
            super.renderArmor(width, height)
        else {
            if (replaceEvent(ARMOR)) return
            ThemeLoader.HUD.draw(HudPartType.ARMOR, context)
            post(ARMOR)
        }
    }

    override fun renderHotbar(res: ScaledResolution, partialTicks: Float) {
        if (OptionCore.DEFAULT_HOTBAR.isEnabled) super.renderHotbar(res, partialTicks)
        else {
            if (replaceEvent(HOTBAR)) return
            if (mc.playerController?.isSpectator ?: false)
                this.spectatorGui.renderTooltip(res, partialTicks)
            else {
                mc.mcProfiler.startSection("hotbar")
                ThemeLoader.HUD.draw(HudPartType.HOTBAR, context)
                mc.mcProfiler.endSection()
            }
            post(HOTBAR)
        }
    }

    override fun renderAir(width: Int, height: Int) {
        if (OptionCore.VANILLA_UI.isEnabled)
            super.renderAir(width, height)
        else {
            if (replaceEvent(AIR)) return
            post(AIR)
        }
    }

    override fun renderAttackIndicator(p_184045_1_: Float, p_184045_2_: ScaledResolution) {
        if (OptionCore.VANILLA_UI.isEnabled) super.renderAttackIndicator(p_184045_1_, p_184045_2_)
        // todo: implement
    }

    override fun renderPotionEffects(resolution: ScaledResolution) {
        if (OptionCore.VANILLA_UI.isEnabled) super.renderPotionEffects(resolution)
        // todo: move effects to here?
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
            if (replaceEvent(HEALTH)) return
            mc.profile("health") { ThemeLoader.HUD.draw(HudPartType.HEALTH_BOX, context) }
            post(HEALTH)

            renderParty()
        }
    }

    private fun renderParty() {
        val pt = mc.player.getCapability(PartyCapability.CAP_INSTANCE, null)!!.party
        if ((pt == null || !pt.isParty) && ConfigHandler.debugFakePT == 0) return

        mc.mcProfiler.startSection("party")

        GLCore.glAlphaTest(true)
        GLCore.glBlend(true)

        var members: MutableList<EntityPlayer> = mutableListOf()
        if (pt?.isParty ?: false)
            members = pt!!.members.filter { it != mc.player }.toMutableList()
        else
            for (i in 1..ConfigHandler.debugFakePT) members.add(mc.player)

        context.setPt(members)
        ThemeLoader.HUD.draw(HudPartType.PARTY, context)

        mc.mcProfiler.endSection()
    }

    override fun renderFood(width: Int, height: Int) {
        if (OptionCore.VANILLA_UI.isEnabled) super.renderFood(width, height)
        else {
            if (replaceEvent(FOOD)) return
            GLCore.glAlphaTest(true)
            GLCore.glBlend(true)
            mc.profile("foodNew") { ThemeLoader.HUD.draw(HudPartType.FOOD, context) }
            post(FOOD)
        }
    }

    private fun renderFood(healthWidth: Int, healthHeight: Int, offsetUsername: Int, stepOne: Int, stepTwo: Int, stepThree: Int) {
        if (replaceEvent(FOOD)) return
        mc.mcProfiler.startSection("food")
        val ctx = context
        val foodValue = (StaticPlayerHelper.getHungerFract(mc, mc.player, ctx.partialTicks) * healthWidth).toInt()
        //        int h = foodValue < 12 ? 12 - foodValue : 0;
        //        int o = healthHeight;
        GLCore.glAlphaTest(true)
        GLCore.glBlend(true)
        GLCore.glColorRGBA(0x8EE1E8)
        /*for (int i = 0; i < foodValue; i++) {
            GLCore.glTexturedRect(offsetUsername + i + 4, 9, zLevel, h, 240, 1, o);
            if (foodValue < healthWidth && i >= foodValue - 3) o--;

            if (foodValue <= 12) {
                h++;
                if (h > 12) break;
            } else if ((i >= stepOne && i <= stepOne + 3) || (i >= stepTwo && i <= stepTwo + 3) || (i >= stepThree)) {
                h++;

                if (h > 12) break;
            }
        }*/

        if (foodValue in stepTwo..(stepThree - 1))
            GLCore.glTexturedRect((offsetUsername + foodValue).toDouble(), 9.0, zLevel.toDouble(), 11.0, 249.0, 7.0, 4.0)
        if (foodValue >= stepOne && foodValue < stepTwo + 4)
            GLCore.glTexturedRect((offsetUsername + foodValue).toDouble(), 9.0, zLevel.toDouble(), 4.0, 249.0, 7.0, 4.0)
        if (foodValue < stepOne + 4 && foodValue > 0) {
            GLCore.glTexturedRect((offsetUsername + foodValue + 2).toDouble(), 9.0, zLevel.toDouble(), 0.0, 249.0, 4.0, 4.0)
            for (i in 0..foodValue - 2 - 1)
                GLCore.glTexturedRect((offsetUsername + i + 4).toDouble(), 9.0, zLevel.toDouble(), 0.0, 249.0, 4.0, 4.0)
        }

        mc.mcProfiler.endSection()
        post(FOOD)
    }

    override fun renderExperience(width: Int, height: Int) {
        if (OptionCore.VANILLA_UI.isEnabled)
            super.renderExperience(width, height)
        else {
            if (pre(EXPERIENCE)) return
            if (!OptionCore.FORCE_HUD.isEnabled && !this.mc.playerController.shouldDrawHUD()) return
            mc.mcProfiler.startSection("expLevel")

            ThemeLoader.HUD.draw(HudPartType.EXPERIENCE, context)

            mc.mcProfiler.endSection()
            post(EXPERIENCE)
        }
    }

    override fun renderJumpBar(width: Int, height: Int) {
        if (OptionCore.VANILLA_UI.isEnabled)
            super.renderJumpBar(width, height)
        else {
            if (replaceEvent(JUMPBAR)) return
            renderExperience(width, height)

            mc.mcProfiler.startSection("jumpBar")
            ThemeLoader.HUD.draw(HudPartType.JUMP_BAR, context)
            mc.mcProfiler.endSection()

            post(JUMPBAR)
        }
    }

    override fun renderHealthMount(width: Int, height: Int) {
        if (OptionCore.VANILLA_UI.isEnabled)
            super.renderHealthMount(width, height)
        else {
            val player = mc.renderViewEntity as EntityPlayer? ?: return
            val tmp = player.ridingEntity as? EntityLivingBase ?: return

            if (replaceEvent(HEALTHMOUNT)) return
            // Not implemented yet
            post(HEALTHMOUNT)
        }
    }

    override fun renderHUDText(width: Int, height: Int) {
        if (OptionCore.VANILLA_UI.isEnabled || OptionCore.DEFAULT_DEBUG.isEnabled)
            super.renderHUDText(width, height)
        else {
            mc.mcProfiler.startSection("forgeHudText")
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
                    if (msg == null) continue
                    drawRect(1, top - 1, 2 + fontRenderer!!.getStringWidth(msg) + 1, top + fontRenderer!!.FONT_HEIGHT - 1, -1873784752)
                    fontRenderer!!.drawString(msg, 2, top, 14737632)
                    top += fontRenderer!!.FONT_HEIGHT
                }

                top = 2
                for (msg in listR) {
                    if (msg == null) continue
                    val w = fontRenderer!!.getStringWidth(msg)

                    val slotsY = (height - 9 * 22) / 2
                    //                        (res.getScaledHeight() - (slotCount * 22)) / 2;

                    /*for (int i = 0; i < slotCount; i++) {
                    GLCore.glColorRGBA(i == inv.currentItem ? 0xFFBA66AA : 0xCDCDCDAA);
                    GLCore.glTexturedRect(res.getScaledWidth() - 24, slotsY + (22 * i), zLevel, 0, 25, 20, 20);
                }*/

                    val left = width - (if (OptionCore.HOR_HOTBAR.isEnabled || top < slotsY - fontRenderer!!.FONT_HEIGHT - 2) 2 else 26) - w
                    drawRect(left - 1, top - 1, left + w + 1, top + fontRenderer!!.FONT_HEIGHT - 1, -1873784752)
                    fontRenderer!!.drawString(msg, left, top, 14737632)
                    top += fontRenderer!!.FONT_HEIGHT
                }
            }

            mc.mcProfiler.endSection()
            post(TEXT)
        }
    }

    private fun replaceEvent(el: ElementType): Boolean {
        /*if (eventParent!!.type == el && eventParent!!.isCanceled) {
            eventParent!!.isCanceled = false
            eventParent!!.result = Event.Result.ALLOW
            pre(el)
            return true
        }
        return false*/
        return pre(el)
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
