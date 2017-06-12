package com.saomc.saoui.effects

import com.saomc.saoui.GLCore
import com.saomc.saoui.resources.StringNames
import net.minecraft.client.Minecraft
import net.minecraft.client.particle.Particle
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.VertexBuffer
import net.minecraft.entity.Entity
import net.minecraft.util.EnumFacing
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

import java.util.ArrayDeque
import java.util.Queue


@SideOnly(Side.CLIENT)
class DeathParticles private constructor(world: World, xCoord: Double, yCoord: Double, zCoord: Double, redValue: Float, greenValue: Float, blueVale: Float, scale: Float) : Particle(world, xCoord, yCoord, zCoord, 0.0, 0.0, 0.0) {

    private val ParticleScale: Float
    private var time: Float = 0.toFloat()
    private var particleX: Float = 0.toFloat()
    private var particleY: Float = 0.toFloat()
    private var particleZ: Float = 0.toFloat()
    private var f0: Float = 0.toFloat()
    private var f1: Float = 0.toFloat()
    private var rotationY: Float = 0.toFloat() // Rotation around Y axis
    private val speedRotationY: Float // Rotation speed around Y axis

    constructor(world: World, xCoord: Double, yCoord: Double, zCoord: Double, redValue: Float, greenValue: Float, blueValue: Float) : this(world, xCoord, yCoord, zCoord, redValue, greenValue, blueValue, 1.0f)

    init {
        this.motionX = ((Math.random() * 2.0 - 1.0).toFloat() * 0.05f).toDouble()
        this.motionY = ((Math.random() * 2.0 - 1.0).toFloat() * 0.05f).toDouble()
        this.motionZ = ((Math.random() * 2.0 - 1.0).toFloat() * 0.05f).toDouble()
        this.rotationY = rand.nextFloat() * 2
        this.speedRotationY = (rand.nextFloat() + 2.0f) / if (rand.nextBoolean()) 16.0f else -16.0f
        this.particleRed = redValue
        this.particleGreen = greenValue
        this.particleBlue = blueVale
        this.particleScale *= scale
        this.ParticleScale = this.particleScale
        this.particleMaxAge = (8.0 / (Math.random() * 0.8 + 0.2)).toInt()
        this.particleMaxAge = (this.particleMaxAge.toFloat() * scale).toInt()
    }

    override fun renderParticle(worldrender: VertexBuffer, entity: Entity?, time: Float, x: Float, y: Float, z: Float, f0: Float, f1: Float) {
        this.time = time
        this.particleX = x
        this.particleY = y
        this.particleZ = z
        this.f0 = f0
        this.f1 = f1

        queuedRenders.add(this)
    }

    private fun renderQueued(tessellator: Tessellator) {
        var particle = (this.particleAge.toFloat() + time) / this.particleMaxAge.toFloat() * 32.0f

        if (particle < 0.0f) particle = 0.0f

        if (particle > 1.0f) particle = 1.0f

        this.particleScale = this.ParticleScale * particle
        val scale = 0.1f * this.particleScale
        val xPos = (this.prevPosX + (this.posX - this.prevPosX) * time.toDouble() - Particle.interpPosX).toFloat()
        val yPos = (this.prevPosY + (this.posY - this.prevPosY) * time.toDouble() - Particle.interpPosY).toFloat()
        val zPos = (this.prevPosZ + (this.posZ - this.prevPosZ) * time.toDouble() - Particle.interpPosZ).toFloat()
        val colorIntensity = 1.0f

        val x1: Double
        val x2: Double
        val x3: Double
        val x4: Double
        val y1: Double
        val y2: Double
        val y3: Double
        val y4: Double
        val z1: Double
        val z2: Double
        val z3: Double
        val z4: Double

        x1 = (-(this.particleX + f0) * scale).toDouble()
        y1 = (-this.particleY * scale).toDouble()
        z1 = (-(this.particleZ + f1) * scale).toDouble()
        x2 = ((f0 - this.particleX) * scale).toDouble()
        y2 = (this.particleY * scale).toDouble()
        z2 = ((f1 - this.particleZ) * scale).toDouble()
        x3 = ((this.particleX + f0) * scale).toDouble()
        y3 = (this.particleY * scale).toDouble()
        z3 = ((this.particleZ + f1) * scale).toDouble()
        x4 = ((this.particleX - f0) * scale).toDouble()
        y4 = (-this.particleY * scale).toDouble()
        z4 = ((this.particleZ - f1) * scale).toDouble()
        val e = Minecraft.getMinecraft().player.horizontalFacing
        val q = e == EnumFacing.NORTH || e == EnumFacing.SOUTH
        val a = (if (q) if (rotationY < 1.5f && rotationY > 0.5f) rotationY - 1.0f else rotationY + 1.0f else rotationY) * Math.PI
        val cos = Math.cos(a)
        val sin = Math.sin(a)

        if (a < Math.PI) {
            GLCore.addVertex(xPos + x1 * cos, yPos + y1, zPos + z1 * sin, 0.0, 1.0, this.particleRed * colorIntensity, this.particleGreen * colorIntensity, this.particleBlue * colorIntensity, 1f)
            GLCore.addVertex(xPos + x2 * cos, yPos + y2, zPos + z2 * sin, 1.0, 1.0, this.particleRed * colorIntensity, this.particleGreen * colorIntensity, this.particleBlue * colorIntensity, 1f)
            GLCore.addVertex(xPos + x3 * cos, yPos + y3, zPos + z3 * sin, 1.0, 0.0, this.particleRed * colorIntensity, this.particleGreen * colorIntensity, this.particleBlue * colorIntensity, 1f)
            GLCore.addVertex(xPos + x4 * cos, yPos + y4, zPos + z4 * sin, 0.0, 0.0, this.particleRed * colorIntensity, this.particleGreen * colorIntensity, this.particleBlue * colorIntensity, 1f)
        } else {
            GLCore.addVertex(xPos - x1 * cos, yPos + y1, zPos - z1 * sin, 0.0, 1.0, this.particleRed * colorIntensity, this.particleGreen * colorIntensity, this.particleBlue * colorIntensity, 1f)
            GLCore.addVertex(xPos - x2 * cos, yPos + y2, zPos - z2 * sin, 1.0, 1.0, this.particleRed * colorIntensity, this.particleGreen * colorIntensity, this.particleBlue * colorIntensity, 1f)
            GLCore.addVertex(xPos - x3 * cos, yPos + y3, zPos - z3 * sin, 1.0, 0.0, this.particleRed * colorIntensity, this.particleGreen * colorIntensity, this.particleBlue * colorIntensity, 1f)
            GLCore.addVertex(xPos - x4 * cos, yPos + y4, zPos - z4 * sin, 0.0, 0.0, this.particleRed * colorIntensity, this.particleGreen * colorIntensity, this.particleBlue * colorIntensity, 1f)
        }

    }

    /**
     * Called to update the entity's position/logic.
     */

    override fun onUpdate() {
        this.prevPosX = this.posX
        this.prevPosY = this.posY
        this.prevPosZ = this.posZ

        if (this.particleAge++ >= this.particleMaxAge) this.isExpired = true

        this.motionY += 0.004
        this.move(this.motionX, this.motionY, this.motionZ)
        this.motionX *= 0.8999999761581421
        this.motionY *= 0.8999999761581421
        this.motionZ *= 0.8999999761581421
        this.rotationY += this.speedRotationY
        this.rotationY = this.rotationY % 2.0f

        if (this.onGround) {
            this.motionX *= 0.699999988079071
            this.motionZ *= 0.699999988079071
        }
    }

    companion object {

        var queuedRenders: Queue<DeathParticles> = ArrayDeque()

        internal fun dispatchQueuedRenders(tessellator: Tessellator) {
            RenderDispatcher.particleFxCount = 0

            Minecraft.getMinecraft().renderEngine.bindTexture(StringNames.particleLarge)

            GLCore.glAlphaTest(true)
            GLCore.glBlend(true)
            GLCore.begin()
            queuedRenders.forEach { p -> p.renderQueued(tessellator) }
            GLCore.draw()
            GLCore.glBlend(false)

            queuedRenders.clear()
        }
    }

}
