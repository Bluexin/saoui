package com.tencao.saoui.themes.settings

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.tencao.saoui.config.Setting
import com.tencao.saoui.themes.ThemeMetadata
import com.tencao.saoui.themes.util.json.JsonResourceLocationTypeAdapter
import com.tencao.saoui.themes.util.json.JsonSettingAdapterFactory
import com.tencao.saoui.util.Client
import com.tencao.saoui.util.append
import net.minecraft.resources.ResourceLocation
import java.util.*
import kotlin.jvm.optionals.getOrNull

object SettingsLoader {
    fun loadSettings(theme: ThemeMetadata): List<Setting<*>>? {
        return Optional.of(Client.resourceManager.getResource(theme.themeRoot.append("/settings.json")))
            .map<List<Setting<*>>> { resource ->
                try {
                    JsonSettingAdapterFactory.currentNamespace.set(theme.id)
                    resource.inputStream.use {
                        GsonBuilder().registerTypeAdapter(
                            object : TypeToken<ResourceLocation>() {}.type,
                            JsonResourceLocationTypeAdapter()
                        ).create().fromJson(it.reader(), object : TypeToken<List<Setting<*>>>() {}.type)
                    }
                } finally {
                    JsonSettingAdapterFactory.currentNamespace.remove()
                }
            }.getOrNull()
    }
}
