package com.tencao.saoui.effects.particles

import com.mojang.serialization.Codec
import net.minecraft.client.particle.IAnimatedSprite
import net.minecraft.client.particle.IParticleFactory
import net.minecraft.client.particle.Particle
import net.minecraft.client.world.ClientWorld
import net.minecraft.particles.ParticleType

class DeathParticleType : ParticleType<DeathParticleData>(false, DeathParticleData.DESERIALIZER) {

    override fun func_230522_e_(): Codec<DeathParticleData> {
        return DeathParticleData.CODEC
    }

    class DeathParticleFactory : IParticleFactory<DeathParticleData> {
        private lateinit var sprite: IAnimatedSprite

        private constructor() {
            throw UnsupportedOperationException("Use the FlameParticleFactory(IAnimatedSprite sprite) constructor")
        }

        constructor(sprite: IAnimatedSprite) {
            this.sprite = sprite
        }

        override fun makeParticle(
            data: DeathParticleData,
            world: ClientWorld,
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
            ret.selectSpriteRandomly(sprite)
            return ret
        }
    }
}
