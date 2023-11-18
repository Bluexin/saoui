package com.tencao.saoui.effects.particles

import com.mojang.serialization.Codec
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.particle.*
import net.minecraft.core.particles.ParticleType

class DeathParticleType : ParticleType<DeathParticleData>(false, DeathParticleData.DESERIALIZER) {

    override fun codec(): Codec<DeathParticleData> {
        return DeathParticleData.CODEC
    }

    class DeathParticleFactory : ParticleProvider<DeathParticleData> {
        private lateinit var sprite: SpriteSet

        constructor() {
        }

        constructor(sprite: SpriteSet) {
            this.sprite = sprite
        }

        override fun createParticle(
            data: DeathParticleData,
            world: ClientLevel,
            x: Double,
            y: Double,
            z: Double,
            mx: Double,
            my: Double,
            mz: Double
        ): Particle {
            val ret = DeathParticle(
                world,
                x,
                y,
                z,
                data.r,
                data.g,
                data.b
            )
            ret.pickSprite(sprite)
            return ret
        }
    }
}
