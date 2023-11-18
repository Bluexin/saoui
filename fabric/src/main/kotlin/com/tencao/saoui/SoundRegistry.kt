package com.tencao.saoui

import net.minecraft.core.Registry
import net.minecraft.sounds.SoundEvent

object SoundRegistry {

    /**
     * Register sounds to registry
     * TODO Add support for custom sounds
     */
    fun register(){
        SoundCore.values().forEach {
            it.sound = SoundEvent(it.resource)
            Registry.register(Registry.SOUND_EVENT, it.resource, it.sound)
        }
    }
}