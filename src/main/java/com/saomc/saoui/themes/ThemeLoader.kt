package com.saomc.saoui.themes

import com.helger.commons.io.IHasInputStream
import com.helger.css.ECSSVersion
import com.helger.css.decl.CSSStyleRule
import com.helger.css.decl.visit.CSSVisitor
import com.helger.css.decl.visit.DefaultCSSVisitor
import com.helger.css.reader.CSSReader
import com.saomc.saoui.SAOCore
import com.saomc.saoui.api.entity.rendering.ColorState
import com.saomc.saoui.screens.ingame.HealthStep
import com.saomc.saoui.themes.elements.Hud
import com.saomc.saoui.util.ColorUtil
import net.minecraft.client.Minecraft
import net.minecraft.util.ResourceLocation
import java.io.IOException
import java.nio.charset.StandardCharsets
import javax.xml.bind.JAXBContext
import javax.xml.bind.JAXBException

/**
 * Part of saoui by Bluexin.
 *
 * @author Bluexin
 */
object ThemeLoader {

    //TODO: tests

    lateinit var HUD: Hud

    @Throws(JAXBException::class)
    fun load() {
        val start = System.currentTimeMillis()
        val hudRL = ResourceLocation(SAOCore.MODID, "themes/hud.xml")

        val context = JAXBContext.newInstance(Hud::class.java)
        val um = context.createUnmarshaller()

        try {
            Minecraft.getMinecraft().resourceManager.getResource(hudRL).inputStream.use { HUD = um.unmarshal(it) as Hud }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        HUD.setup()

        SAOCore.LOGGER.info("Loaded theme and set it up in " + (System.currentTimeMillis() - start) + "ms.")

        test()
    }

    fun test() {
        val start = System.currentTimeMillis()
        val cssRl = ResourceLocation(SAOCore.MODID, "themes/style.css")

        try {
            val aCSS = CSSReader.readFromStream(object : IHasInputStream {
                override fun isReadMultiple() = false
                override fun getInputStream() = Minecraft.getMinecraft().resourceManager.getResource(cssRl).inputStream
            }, StandardCharsets.UTF_8, ECSSVersion.CSS30)
            if (aCSS == null) {
                // Most probably a syntax error
                SAOCore.LOGGER.warn("Failed to read CSS - please see previous logging entries!")
            } else {
                CSSVisitor.visitCSS(aCSS, object : DefaultCSSVisitor() {
                    override fun onBeginStyleRule(aStyleRule: CSSStyleRule) {
                        // Let's hardcode this for now. A proper CSS engine will come later O:-)
                        var hbg = aStyleRule.getAllDeclarationsOfPropertyName("background-color").firstOrNull()?.expression?.allSimpleMembers?.firstOrNull()?.value?.substring(1)
                        if (hbg != null && hbg.length == 6) hbg += "ff"
                        val bg = hbg?.toLongOrNull(16)?.toInt()
                        var hfg = aStyleRule.getAllDeclarationsOfPropertyName("color").firstOrNull()?.expression?.allSimpleMembers?.firstOrNull()?.value?.substring(1)
                        if (hfg != null && hfg.length == 6) hfg += "ff"
                        val fg = hfg?.toLongOrNull(16)?.toInt()
                        SAOCore.LOGGER.info("Set ${aStyleRule.allSelectors.joinToString { it.asCSSString }} bg ${"0x%08X".format(bg)} ($hbg) fg ${"0x%08X".format(fg)} ($hfg)")

                        when (aStyleRule.allSelectors.joinToString { it.asCSSString }) {
                            "*" -> {
                                if (bg != null) ColorUtil.DEFAULT_COLOR.rgba = bg
                                if (fg != null) ColorUtil.DEFAULT_FONT_COLOR.rgba = fg
                            }
                            ":hover" -> {
                                if (bg != null) ColorUtil.HOVER_COLOR.rgba = bg
                                if (fg != null) ColorUtil.HOVER_FONT_COLOR.rgba = fg
                            }
                            ":disabled" -> {
                                if (bg != null) ColorUtil.DISABLED_MASK.rgba = bg
                            }
                            ".confirm" -> {
                                if (bg != null) ColorUtil.CONFIRM_COLOR.rgba = bg
                            }
                            ".confirm:hover" -> {
                                if (bg != null) ColorUtil.CONFIRM_COLOR_LIGHT.rgba = bg
                            }
                            ".cancel" -> {
                                if (bg != null) ColorUtil.CANCEL_COLOR.rgba = bg
                            }
                            ".cancel:hover" -> {
                                if (bg != null) ColorUtil.CANCEL_COLOR_LIGHT.rgba = bg
                            }
                            ".popup" -> {
                                if (bg != null) ColorUtil.DEFAULT_BOX_COLOR.rgba = bg
                                if (fg != null) ColorUtil.DEFAULT_BOX_FONT_COLOR.rgba = fg
                            }
                            ".cursor" -> {
                                if (bg != null) ColorUtil.CURSOR_COLOR.rgba = bg
                            }
                            ".dead" -> {
                                if (bg != null) ColorUtil.DEAD_COLOR.rgba = bg
                            }
                            ".hardcore-dead" -> {
                                if (bg != null) ColorUtil.HARDCORE_DEAD_COLOR.rgba = bg
                            }
                            ".hp .very_low" -> {
                                if (bg != null) HealthStep.VERY_LOW.rgba = bg
                            }
                            ".hp .low" -> {
                                if (bg != null) HealthStep.LOW.rgba = bg
                            }
                            ".hp .very_damaged" -> {
                                if (bg != null) HealthStep.VERY_DAMAGED.rgba = bg
                            }
                            ".hp .damaged" -> {
                                if (bg != null) HealthStep.DAMAGED.rgba = bg
                            }
                            ".hp .okay" -> {
                                if (bg != null) HealthStep.OKAY.rgba = bg
                            }
                            ".hp .good" -> {
                                if (bg != null) HealthStep.GOOD.rgba = bg
                            }
                            ".hp .creative" -> {
                                if (bg != null) HealthStep.CREATIVE.rgba = bg
                            }
                            ".cursor .innocent" -> {
                                if (bg != null) ColorState.INNOCENT.rgba = bg
                            }
                            ".cursor .violent" -> {
                                if (bg != null) ColorState.VIOLENT.rgba = bg
                            }
                            ".cursor .killer" -> {
                                if (bg != null) ColorState.KILLER.rgba = bg
                            }
                            ".cursor .boss" -> {
                                if (bg != null) ColorState.BOSS.rgba = bg
                            }
                            ".cursor .creative" -> {
                                if (bg != null) ColorState.CREATIVE.rgba = bg
                            }
                            ".cursor .op" -> {
                                if (bg != null) ColorState.OP.rgba = bg
                            }
                            ".cursor .invalid" -> {
                                if (bg != null) ColorState.INVALID.rgba = bg
                            }
                            ".cursor .gamemaster" -> {
                                if (bg != null) ColorState.GAMEMASTER.rgba = bg
                            }
                        }
                    }
                })
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        SAOCore.LOGGER.info("Loaded CSS in " + (System.currentTimeMillis() - start) + "ms.")

    }
}
