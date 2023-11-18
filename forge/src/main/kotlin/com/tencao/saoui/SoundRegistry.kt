package com.tencao.saoui

import net.minecraft.resources.ResourceLocation
import net.minecraft.sounds.SoundEvent
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import java.util.*

object SoundRegistry {

    /**
     * Register sounds to registry
     * TODO Add support for custom sounds
     */
    @SubscribeEvent
    fun registerSoundEvent(event: RegistryEvent.Register<SoundEvent>) {
        SoundCore.values().forEach {
            val sound = SoundEvent(ResourceLocation(Constants.MODID, it.name.lowercase(Locale.getDefault())))
            event.registry.register(sound.setRegistryName(it.name.lowercase(Locale.getDefault())))
            it.sound = sound
        }
    }
}