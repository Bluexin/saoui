package com.tencao.saoui.themes

import com.helger.commons.io.IHasInputStream
import com.helger.css.ECSSVersion
import com.helger.css.decl.CSSStyleRule
import com.helger.css.decl.visit.CSSVisitor
import com.helger.css.decl.visit.DefaultCSSVisitor
import com.helger.css.reader.CSSReader
import com.tencao.saomclib.Client
import com.tencao.saoui.GLCore
import com.tencao.saoui.SAOCore
import com.tencao.saoui.api.entity.rendering.ColorState
import com.tencao.saoui.config.OptionCore
import com.tencao.saoui.config.Setting
import com.tencao.saoui.resources.StringNames
import com.tencao.saoui.screens.util.HealthStep
import com.tencao.saoui.themes.elements.ElementGroup
import com.tencao.saoui.themes.elements.Fragment
import com.tencao.saoui.themes.elements.Hud
import com.tencao.saoui.themes.settings.SettingsLoader
import com.tencao.saoui.util.ColorUtil
import com.tencao.saoui.util.append
import net.minecraft.client.Minecraft
import net.minecraft.util.ResourceLocation
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.nio.charset.StandardCharsets

abstract class AbstractThemeLoader(protected val type: ThemeFormat) {

    fun load(theme: ThemeMetadata) {
        if (OptionCore.CUSTOM_FONT.isEnabled) GLCore.setFont(Minecraft.getMinecraft(), OptionCore.CUSTOM_FONT.isEnabled)

        val start = System.currentTimeMillis()

        runCatching {
            SettingsLoader.loadSettings(theme)?.forEach(Setting<*>::register)
            val hud = loadHud(theme.themeRoot.append("/${type.hudFileSuffix}"))
            val fragments = theme.fragments.mapValues { (_, path) -> this.loadFragment(path) }

            hud to fragments
        }.onSuccess { (hud, fragments) ->
            hud.setup(fragments)
            ThemeManager.HUD = hud // FIXME : code smell
        }.onFailure {
            SAOCore.LOGGER.warn("Failed to load $theme", it)
            return
        }

        loadCss(theme.themeRoot.append("/style.css"))

        StringNames.init()

        SAOCore.LOGGER.info(
            "Loaded {} ({}) and set it up in {}ms.",
            theme.name, theme.id, System.currentTimeMillis() - start
        )
    }

    /**
     * Load [Hud] from File reference
     */
    fun loadHud(location: File): Hud = FileInputStream(location).loadHud()

    /**
     * Load [Hud] from ResourceLocation reference (using mc ResourceManager)
     */
    fun loadHud(location: ResourceLocation): Hud =
        Client.resourceManager.getResource(location).inputStream.loadHud()

    /**
     * Load [ElementGroup] from File reference
     */
    fun loadFragment(location: File): Fragment =
        when (val loader = ThemeFormat.fromFileExtension(location.extension)?.loader?.invoke()) {
            this -> FileInputStream(location).loadFragment()
            null -> error("Unknown fragment format for $location")
            else -> loader.loadFragment(location)
        }

    /**
     * Load [ElementGroup] from ResourceLocation reference (using mc ResourceManager)
     */
    fun loadFragment(location: ResourceLocation): Fragment =
        when (val loader = ThemeFormat.fromFileExtension(location.resourcePath)?.loader?.invoke()) {
            this -> Client.resourceManager.getResource(location).inputStream.loadFragment()
            null -> error("Unknown fragment format for $location")
            else -> loader.loadFragment(location)
        }

    /**
     * Load [Hud] from [InputStream].
     * Implementations should throw on errors.
     */
    protected abstract fun InputStream.loadHud(): Hud

    /**
     * Load [ElementGroup] from [InputStream].
     * Implementations should throw on errors.
     */
    protected abstract fun InputStream.loadFragment(): Fragment

    private fun loadCss(location: ResourceLocation) {
        val start = System.currentTimeMillis()

        try {
            val aCSS = CSSReader.readFromStream(
                object : IHasInputStream {
                    override fun isReadMultiple() = false
                    override fun getInputStream() = Client.resourceManager.getResource(location).inputStream
                },
                StandardCharsets.UTF_8,
                ECSSVersion.CSS30
            )
            if (aCSS == null) {
                // Most probably a syntax error
                SAOCore.LOGGER.warn("Failed to read CSS - please see previous logging entries!")
            } else {
                CSSVisitor.visitCSS(
                    aCSS,
                    object : DefaultCSSVisitor() {
                        override fun onBeginStyleRule(aStyleRule: CSSStyleRule) {
                            // Let's hardcode this for now. A proper CSS engine will come later O:-)
                            var hbg = aStyleRule.getAllDeclarationsOfPropertyName("background-color")
                                .firstOrNull()?.expression?.allSimpleMembers?.firstOrNull()?.value?.substring(1)
                            if (hbg != null && hbg.length == 6) hbg += "ff"
                            val bg = hbg?.toLongOrNull(16)?.toInt()
                            var hfg = aStyleRule.getAllDeclarationsOfPropertyName("color")
                                .firstOrNull()?.expression?.allSimpleMembers?.firstOrNull()?.value?.substring(1)
                            if (hfg != null && hfg.length == 6) hfg += "ff"
                            val fg = hfg?.toLongOrNull(16)?.toInt()
                            SAOCore.LOGGER.info(
                                "Set ${aStyleRule.allSelectors.joinToString { it.asCSSString }} bg ${
                                    "0x%08X".format(
                                        bg
                                    )
                                } ($hbg) fg ${"0x%08X".format(fg)} ($hfg)"
                            )

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
                                    if (bg != null) ColorUtil.DISABLED_COLOR.rgba = bg
                                    if (fg != null) ColorUtil.DISABLED_FONT_COLOR.rgba = fg
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
                                    if (bg != null) ColorState.DEV.rgba = bg
                                }
                            }
                        }
                    }
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        SAOCore.LOGGER.info("Loaded CSS in " + (System.currentTimeMillis() - start) + "ms.")
    }
}