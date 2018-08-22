package com.saomc.saoui.screens.ingame

import be.bluexin.saomclib.capabilities.PartyCapability
import be.bluexin.saomclib.capabilities.getCapability
import be.bluexin.saomclib.displayNameString
import be.bluexin.saomclib.player
import be.bluexin.saomclib.profile
import com.saomc.saoui.GLCore
import com.saomc.saoui.config.ConfigHandler
import com.saomc.saoui.config.OptionCore
import com.saomc.saoui.themes.ThemeLoader
import com.saomc.saoui.themes.elements.HudPartType
import com.saomc.saoui.themes.util.HudDrawContext
import cpw.mods.fml.common.FMLCommonHandler
import cpw.mods.fml.relauncher.Side
import cpw.mods.fml.relauncher.SideOnly
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.client.resources.I18n
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.Direction
import net.minecraft.util.MathHelper
import net.minecraft.util.StringUtils
import net.minecraft.world.EnumSkyBlock
import net.minecraftforge.client.GuiIngameForge
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType.*
import net.minecraftforge.common.MinecraftForge
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

    override fun renderGameOverlay(partialTicks: Float, hasScreen: Boolean, mouseX: Int, mouseY: Int) {
        mc.mcProfiler.startSection("setup")
        val username = mc.player!!.displayNameString
        val maxNameWidth = mc.fontRendererObj.getStringWidth(username)
        val usernameBoxes = 1 + (maxNameWidth + 4) / 5
        offsetUsername = 18 + usernameBoxes * 5
        val res = ScaledResolution(mc, mc.displayWidth, mc.displayHeight)
        eventParent = RenderGameOverlayEvent(partialTicks, res, mouseX, mouseY)
        val width = res.scaledWidth
        val height = res.scaledHeight
        context.setTime(partialTicks)
        context.setScaledResolution(res)
        context.z = zLevel
        context.player = mc.player
        GLCore.glBlend(true)
        mc.mcProfiler.endSection()

        super.renderGameOverlay(partialTicks, hasScreen, mouseX, mouseY)

        if (OptionCore.FORCE_HUD.isEnabled && !this.mc.playerController.shouldDrawHUD() && this.mc.renderViewEntity is EntityPlayer) {
            if (GuiIngameForge.renderHealth) renderHealth(width, height)
            if (GuiIngameForge.renderArmor) renderArmor(width, height)
            if (GuiIngameForge.renderFood) renderFood(width, height)
            if (GuiIngameForge.renderHealthMount) renderHealthMount(width, height)
            if (GuiIngameForge.renderAir) renderAir(width, height)
            mc.entityRenderer.setupOverlayRendering()
        } // Basically adding what super doesn't render by default

    }

    override fun renderCrosshairs(width: Int, height: Int) {
        if (OptionCore.VANILLA_UI.isEnabled)
            super.renderCrosshairs(width, height)
        else {
            if (pre(CROSSHAIRS)) return
            ThemeLoader.HUD.draw(HudPartType.CROSS_HAIR, context)
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

    override fun renderHotbar(width: Int, height: Int, partialTicks: Float) {
        if (OptionCore.DEFAULT_HOTBAR.isEnabled) super.renderHotbar(width, height, partialTicks)
        else {
            if (pre(HOTBAR)) return
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
            if (pre(AIR)) return
            mc.profile("air") {
                ThemeLoader.HUD.draw(HudPartType.AIR, context)
            }
            post(AIR)
        }
    }

    /*override fun renderAttackIndicator(p_184045_1_: Float, p_184045_2_: ScaledResolution) {
        if (OptionCore.VANILLA_UI.isEnabled) super.renderAttackIndicator(p_184045_1_, p_184045_2_)
    }*/

    /*override*/ fun renderPotionEffects() {
        if (!OptionCore.VANILLA_UI.isEnabled) {
            mc.profile("potionEffects") {
                ThemeLoader.HUD.draw(HudPartType.EFFECTS, context)
            }
        }
    }

    /*override fun renderPotionIcons(resolution: ScaledResolution) {
        if (pre(POTION_ICONS)) return
        this.renderPotionEffects(resolution)
        post(POTION_ICONS)
    }*/

    override fun renderHealth(width: Int, height: Int) {
        if (OptionCore.VANILLA_UI.isEnabled)
            super.renderHealth(width, height)
        else {
            if (pre(HEALTH)) return
            mc.profile("health") { ThemeLoader.HUD.draw(HudPartType.HEALTH_BOX, context) }
            post(HEALTH)
        }
        renderParty()
        renderPotionEffects()
    }

    private fun renderParty() {
        val pt = mc.player!!.getCapability(PartyCapability.CAP_INSTANCE, null)!!.party
        if ((pt == null || !pt.isParty) && ConfigHandler.debugFakePT == 0) return

        mc.mcProfiler.startSection("party")

        GLCore.glAlphaTest(true)
        GLCore.glBlend(true)

        var members: MutableList<EntityPlayer> = mutableListOf()
        if (pt?.isParty == true)
            members = pt.members.filter { it != mc.player }.toMutableList()
        else
            for (i in 1..ConfigHandler.debugFakePT) members.add(mc.player!!)

        context.setPt(members)
        ThemeLoader.HUD.draw(HudPartType.PARTY, context)

        mc.mcProfiler.endSection()
    }

    override fun renderFood(width: Int, height: Int) {
        if (OptionCore.VANILLA_UI.isEnabled) {
            GLCore.glBindTexture(Gui.icons)
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
            // These are here because vanilla hides them when showing jump bar
            renderExperience(width, height)
            renderFood(width, height)

            if (pre(JUMPBAR)) return
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
            if (pre(HEALTHMOUNT)) return
            ThemeLoader.HUD.draw(HudPartType.MOUNT_HEALTH, context)
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
                val time = mc.theWorld.totalWorldTime
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
                val fontRenderer = GLCore.glFont
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

    private inner class GuiOverlayDebugForge constructor(mc: Minecraft) /*: GuiOverlayDebug(mc)*/ {

        val left: List<String>
            get() {
                val left = LinkedList<String>()
                left.add("Minecraft " + MinecraftForge.MC_VERSION + " (" + mc.debug + ")")
                left.add(mc.debugInfoRenders())
                left.add(mc.entityDebug)
                left.add(mc.debugInfoEntities())
                left.add(mc.worldProviderName)
                left.add("") //Spacer

                val x = MathHelper.floor_double(mc.thePlayer.posX)
                val y = MathHelper.floor_double(mc.thePlayer.posY)
                val z = MathHelper.floor_double(mc.thePlayer.posZ)
                val yaw = mc.thePlayer.rotationYaw
                val heading = MathHelper.floor_double((mc.thePlayer.rotationYaw * 4.0f / 360.0f).toDouble() + 0.5) and 3

                left.add(String.format("x: %.5f (%d) // c: %d (%d)", mc.thePlayer.posX, x, x shr 4, x and 15))
                left.add(String.format("y: %.3f (feet pos, %.3f eyes pos)", mc.thePlayer.boundingBox.minY, mc.thePlayer.posY))
                left.add(String.format("z: %.5f (%d) // c: %d (%d)", mc.thePlayer.posZ, z, z shr 4, z and 15))
                left.add(String.format("f: %d (%s) / %f", heading, Direction.directions[heading], MathHelper.wrapAngleTo180_float(yaw)))

                if (mc.theWorld != null && mc.theWorld.blockExists(x, y, z)) {
                    val chunk = mc.theWorld.getChunkFromBlockCoords(x, z)
                    left.add(String.format("lc: %d b: %s bl: %d sl: %d rl: %d",
                            chunk.topFilledSegment + 15,
                            chunk.getBiomeGenForWorldCoords(x and 15, z and 15, mc.theWorld.worldChunkManager).biomeName,
                            chunk.getSavedLightValue(EnumSkyBlock.Block, x and 15, y, z and 15),
                            chunk.getSavedLightValue(EnumSkyBlock.Sky, x and 15, y, z and 15),
                            chunk.getBlockLightValue(x and 15, y, z and 15, 0)))
                } else {
                    left.add("")
                }

                left.add(String.format("ws: %.3f, fs: %.3f, g: %b, fl: %d", mc.thePlayer.capabilities.walkSpeed, mc.thePlayer.capabilities.flySpeed, mc.thePlayer.onGround, mc.theWorld.getHeightValue(x, z)))
                if (mc.entityRenderer != null && mc.entityRenderer.isShaderActive) {
                    left.add(String.format("shader: %s", mc.entityRenderer.shaderGroup.shaderGroupName))
                }

                return left
            }

        val right: MutableList<String>
            get() {
                val max = Runtime.getRuntime().maxMemory()
                val total = Runtime.getRuntime().totalMemory()
                val free = Runtime.getRuntime().freeMemory()
                val used = total - free

                val right = LinkedList<String>()
                right.add("Used memory: " + used * 100L / max + "% (" + used / 1024L / 1024L + "MB) of " + max / 1024L / 1024L + "MB")
                right.add("Allocated memory: " + total * 100L / max + "% (" + total / 1024L / 1024L + "MB)")

                right.add("")
                for (brand in FMLCommonHandler.instance().getBrandings(false)) {
                    right.add(brand)
                }

                return right
            }
    }

}
