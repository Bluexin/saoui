package com.tencao.saoui.effects.particles

import com.tencao.saoui.SAOCore
import net.minecraft.client.Minecraft
import net.minecraft.client.particle.IAnimatedSprite
import net.minecraft.particles.ParticleType
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent
import net.minecraftforge.event.RegistryEvent.Register
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.registries.IForgeRegistry
import net.minecraftforge.registries.IForgeRegistryEntry

object ModParticles {
    val DEATH_PARTICLE: ParticleType<DeathParticleData> = DeathParticleType()

    fun registerParticles(evt: Register<ParticleType<*>?>) {
        register(evt.registry, "death_particle", DEATH_PARTICLE)
    }

    fun <V : IForgeRegistryEntry<V>?> register(reg: IForgeRegistry<V>, name: ResourceLocation?, thing: IForgeRegistryEntry<V>) {
        reg.register(thing.setRegistryName(name))
    }

    fun <V : IForgeRegistryEntry<V>?> register(reg: IForgeRegistry<V>, name: String, thing: IForgeRegistryEntry<V>) {
        register(reg, ResourceLocation(SAOCore.MODID, name), thing)
    }

    @SubscribeEvent
    fun registerFactories(evt: ParticleFactoryRegisterEvent) {
        Minecraft.getInstance().particles.registerFactory(
            DEATH_PARTICLE
        ) { sprite: IAnimatedSprite ->
            DeathParticleType.DeathParticleFactory(
                sprite
            )
        }
    }

    @SubscribeEvent
    fun onIParticleTypeRegistration(evt: Register<ParticleType<*>?>) {
        registerParticles(evt)
    }
}
