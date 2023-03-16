package com.tencao.saoui.config

import com.tencao.saoui.SAOCore
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.runBlocking
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.config.Configuration

class Settings {
    private val configurations: Map<ResourceLocation, Configuration> = mutableMapOf()
    private val registry: Map<Pair<ResourceLocation, ResourceLocation>, Setting<Any>> = mutableMapOf()

    private /* TODO */ lateinit var updates: SendChannel<ResourceLocation>

    fun getValue(namespace: ResourceLocation, key: ResourceLocation): Any? {
        val (config, setting) = getConfigAndSetting(namespace, key, "get") ?: return null
        val (_, _, default, comment, read, write, validate) = setting

        val property = config.get(key.resourceDomain, key.resourcePath, write(default), comment)
        return read(property.string)?.takeIf(validate) ?: default
    }

    fun setValue(namespace: ResourceLocation, key: ResourceLocation, value: String) {
        val (config, setting) = getConfigAndSetting(namespace, key, "set") ?: return
        val (_, _, default, comment, read, write, validate) = setting

        val property = config.get(key.resourceDomain, key.resourcePath, write(default), comment)
        read(value)?.takeIf(validate)?.let { property.set(value) }
        runBlocking { updates.send(namespace) }
    }

    fun isValid(namespace: ResourceLocation, key: ResourceLocation, value: String): Boolean {
        val (_, _, _, _, read, _, validate) = registry[namespace to key] ?: run {
            SAOCore.LOGGER.warn("Trying to check validity for unregistered setting : $key in $namespace")
            return false
        }

        return read(value)?.takeIf(validate) != null
    }

    private fun getConfigAndSetting(
        namespace: ResourceLocation, key: ResourceLocation, operation: String
    ): Pair<Configuration, Setting<Any>>? {
        val config = configurations[namespace] ?: run {
            SAOCore.LOGGER.warn("Trying to $operation value from unregistered config : $namespace")
            return null
        }

        val setting = registry[namespace to key] ?: run {
            SAOCore.LOGGER.warn("Trying to $operation value from unregistered setting : $key in $namespace")
            return null
        }

        return config to setting
    }
}
