package com.tencao.saoui.config

import com.tencao.saoui.SAOCore
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.selects.select
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.config.Configuration
import org.apache.logging.log4j.LogManager
import java.io.File

object Settings {
    val NS_BUILTIN = ResourceLocation(SAOCore.MODID, "builtin")

    private val logger = LogManager.getLogger(this)

    private val configurations: MutableMap<ResourceLocation, Configuration> = mutableMapOf()
    private val registry: MutableMap<Pair<ResourceLocation, ResourceLocation>, Setting<Any>> = mutableMapOf()

    private val updates: Channel<ResourceLocation> = Channel(capacity = Channel.BUFFERED) {
        logger.warn("Undelivered $it")
    }

    operator fun get(namespace: ResourceLocation, key: ResourceLocation): Any? {
        val (config, setting) = getConfigAndSetting(namespace, key, "get") ?: return null
        val (_, _, default, comment, read, write, validate, type) = setting

        val property = config.get(key.resourceDomain, key.resourcePath, write(default), comment, type)
        return read(property.string)?.takeIf(validate) ?: default
    }

    operator fun <T : Any> get(setting: Setting<T>): T {
        val (_, key, default, comment, read, write, validate, type) = setting
        val config = getConfig(setting.namespace, "get") ?: return default

        val property = try {
            config.get(key.resourceDomain, key.resourcePath, write(default), comment, type)
        } catch (e: Exception) {
            throw e
        }
        return read(property.string)?.takeIf(validate) ?: default
    }

    operator fun set(namespace: ResourceLocation, key: ResourceLocation, value: String) {
        val (config, setting) = getConfigAndSetting(namespace, key, "set") ?: return
        val (_, _, default, comment, read, write, validate, type) = setting

        val property = config.get(key.resourceDomain, key.resourcePath, write(default), comment, type)
        read(value)?.takeIf(validate)?.let { property.set(value) }
        notifyUpdate(namespace)
    }

    operator fun <T : Any> set(setting: Setting<T>, value: T) {
        val (namespace, key, default, comment, _, write, validate, type) = setting
        val config = getConfig(namespace, "set") ?: return

        val property = config.get(key.resourceDomain, key.resourcePath, write(default), comment, type)
        value.takeIf(validate)?.let { property.set(write(value)) }
        notifyUpdate(namespace)
    }

    fun isValid(namespace: ResourceLocation, key: ResourceLocation, value: String): Boolean {
        val (_, _, _, _, read, _, validate) = registry[namespace to key] ?: run {
            logger.warn("Trying to check validity for unregistered setting : $key in $namespace")
            return false
        }

        return read(value)?.takeIf(validate) != null
    }

    fun register(setting: Setting<*>) {
        registerNamespace(setting.namespace)
        logger.info("Registering Setting ${setting.namespace}/${setting.key}")
        @Suppress("UNCHECKED_CAST")
        registry[setting.namespace to setting.key] = setting as Setting<Any>
        val read = this[setting]
        logger.info("Read value $read")
        notifyUpdate(setting.namespace)
    }

    private fun notifyUpdate(namespace: ResourceLocation) {
        updates.trySend(namespace).onFailure {
            logger.warn("Failed to send config update for $namespace !")
        }
    }

    private fun registerNamespace(namespace: ResourceLocation) {
        if (namespace !in configurations) {
            logger.info("Registering namespace $namespace")
            val isBuiltIn = namespace == NS_BUILTIN
            val dir = if (isBuiltIn) SAOCore.saoConfDir else File(SAOCore.saoConfDir, namespace.resourceDomain)
            dir.mkdirs()
            configurations[namespace] = Configuration(
                File(dir, if (isBuiltIn) "main.cfg" else "${namespace.resourcePath}.cfg")
            ).also {
                logger.info("Loaded namespace $namespace from ${it.configFile}")
            }
        }
    }

    /**
     * This sets up the listener for [updates], which will save the config file when an update is received for
     * a different config file (namespace) **or** a timeout has been reached
     */
    fun initialize() {
        CoroutineScope(Dispatchers.IO).launch {
            logger.info("[${currentCoroutineContext()}] Starting listener")
            flow<ResourceLocation> {
                logger.info("[${currentCoroutineContext()}] Starting updates filter")
                // emit a value when the new one is different from the previous one or a timeout has been reached
                var lastValue: ResourceLocation? = null
                while (true) select {
                    updates.onReceive { newValue -> // New value received
                        when {
                            lastValue == null -> lastValue = newValue
                            lastValue != newValue -> {
                                lastValue?.let { emit(it) }
                                lastValue = newValue
                            }
                        }
                    }
                    @OptIn(ExperimentalCoroutinesApi::class) // for onTimeout
                    onTimeout(5_000) { // Timeout reached
                        if (lastValue != null) {
                            lastValue?.let { emit(it) }
                            lastValue = null
                        }
                    }
                }
            }.flowOn(Dispatchers.Default).collect {
                configurations[it]?.save()
            }
            logger.warn("[${currentCoroutineContext()}] Stopping listener (this should never happen !)")
        }
    }

    private fun getConfig(namespace: ResourceLocation, operation: String): Configuration? =
        configurations[namespace] ?: run {
            logger.warn("Trying to $operation value from unregistered config : $namespace")
            null
        }

    private fun getConfigAndSetting(
        namespace: ResourceLocation, key: ResourceLocation, operation: String
    ): Pair<Configuration, Setting<Any>>? {
        val config = getConfig(namespace, operation) ?: return null

        val setting = registry[namespace to key] ?: run {
            logger.warn("Trying to $operation value from unregistered setting : $key in $namespace")
            return null
        }

        return config to setting
    }
}
