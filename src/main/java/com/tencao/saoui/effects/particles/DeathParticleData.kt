package com.tencao.saoui.effects.particles

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.network.PacketBuffer
import net.minecraft.particles.IParticleData
import net.minecraft.particles.IParticleData.IDeserializer
import net.minecraft.particles.ParticleType
import java.util.*
import javax.annotation.Nonnull

class DeathParticleData() : IParticleData {

    var r = 0f
    var g = 0f
    var b = 0f
    val size = 0f
    var maxAgeMul = 0f
    var depthTest = false
    var noClip = false

    constructor(r: Float, g: Float, b: Float) : this() {
        this.r = r
        this.g = g
        this.b = b
    }

    override fun getType(): ParticleType<*> {
        return ModParticles.DEATH_PARTICLE
    }

    override fun write(buffer: PacketBuffer) {
        buffer.writeFloat(r)
        buffer.writeFloat(g)
        buffer.writeFloat(b)
    }

    override fun getParameters(): String {
        return String.format(Locale.ROOT, "%.2f, %.2f, %.2f", net.minecraft.util.registry.Registry.PARTICLE_TYPE.getKey(type), this.r, this.g, this.b)
    }

    companion object {

        val CODEC: Codec<DeathParticleData> =
            RecordCodecBuilder.create { instance: RecordCodecBuilder.Instance<DeathParticleData> ->
                instance.group(
                    Codec.FLOAT.fieldOf("r")
                        .forGetter { d: DeathParticleData -> d.r },
                    Codec.FLOAT.fieldOf("g")
                        .forGetter { d: DeathParticleData -> d.g },
                    Codec.FLOAT.fieldOf("b")
                        .forGetter { d: DeathParticleData -> d.b }
                )
                    .apply(
                        instance
                    ) { r, g, b -> DeathParticleData(r, g, b) }
            }

        val DESERIALIZER: IDeserializer<DeathParticleData> = object : IDeserializer<DeathParticleData> {
            @Nonnull
            @Throws(CommandSyntaxException::class)
            override fun deserialize(
                @Nonnull type: ParticleType<DeathParticleData>,
                @Nonnull reader: StringReader
            ): DeathParticleData {
                reader.expect(' ')
                val r: Float = reader.readFloat()
                reader.expect(' ')
                val g: Float = reader.readFloat()
                reader.expect(' ')
                val b: Float = reader.readFloat()
                return DeathParticleData(r, g, b)
            }

            override fun read(@Nonnull type: ParticleType<DeathParticleData?>, buf: PacketBuffer): DeathParticleData {
                return DeathParticleData(
                    buf.readFloat(),
                    buf.readFloat(),
                    buf.readFloat()
                )
            }
        }
    }
}
