package com.tencao.saoui.effects.particles

import me.shedaniel.architectury.registry.ParticleProviderRegistry


object ModParticles {
    val DEATH_PARTICLE = DeathParticleType()

    fun registerParticles() {
        ParticleProviderRegistry.register(DEATH_PARTICLE, DeathParticleType.DeathParticleFactory())
    }
}
