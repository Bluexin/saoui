package be.bluexin.mcui.themes.settings

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import be.bluexin.mcui.config.Setting
import be.bluexin.mcui.themes.ThemeMetadata
import be.bluexin.mcui.themes.util.json.JsonResourceLocationTypeAdapter
import be.bluexin.mcui.themes.util.json.JsonSettingAdapterFactory
import be.bluexin.mcui.util.Client
import be.bluexin.mcui.util.append
import net.minecraft.resources.ResourceLocation
import kotlin.jvm.optionals.getOrNull

object SettingsLoader {
    fun loadSettings(theme: ThemeMetadata): List<Setting<*>>? {
        return Client.resourceManager.getResource(theme.themeRoot.append("/settings.json"))
            .map<List<Setting<*>>> { resource ->
                try {
                    JsonSettingAdapterFactory.currentNamespace.set(theme.id)
                    resource.open().use {
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
