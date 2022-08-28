/*
 * Copyright (C) 2016-2019 Arnaud 'Bluexin' Solé
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.tencao.saoui.effects

import com.mojang.blaze3d.vertex.IVertexBuilder
import com.tencao.saomclib.Client
import com.tencao.saomclib.GLCore
import com.tencao.saoui.resources.StringNames
import net.minecraft.client.particle.IParticleRenderType
import net.minecraft.client.particle.Particle
import net.minecraft.client.renderer.ActiveRenderInfo
import net.minecraft.client.world.ClientWorld
import net.minecraft.util.Direction
import java.util.*
import kotlin.math.cos
import kotlin.math.sin

class DeathParticles private constructor(world: ClientWorld, xCoord: Double, yCoord: Double, zCoord: Double, redValue: Float, greenValue: Float, blueVale: Float, scale: Float) : Particle(world, xCoord, yCoord, zCoord, 0.0, 0.0, 0.0) {

    private var time: Float = 0.toFloat()
    private var particleX: Float = 0.toFloat()
    private var particleY: Float = 0.toFloat()
    private var particleZ: Float = 0.toFloat()
    private var f0: Float = 0.toFloat()
    private var f1: Float = 0.toFloat()
    private var rotationY: Float = 0.toFloat() // Rotation around Y axis
    private val speedRotationY: Float // Rotation speed around Y axis

    constructor(world: ClientWorld, xCoord: Double, yCoord: Double, zCoord: Double, redValue: Float, greenValue: Float, blueValue: Float) : this(world, xCoord, yCoord, zCoord, redValue, greenValue, blueValue, 1.0f)

    init {
        this.motionX = ((Math.random() * 2.0 - 1.0).toFloat() * 0.05f).toDouble()
        this.motionY = ((Math.random() * 2.0 - 1.0).toFloat() * 0.05f).toDouble()
        this.motionZ = ((Math.random() * 2.0 - 1.0).toFloat() * 0.05f).toDouble()
        this.rotationY = rand.nextFloat() * 2
        this.speedRotationY = (rand.nextFloat() + 2.0f) / if (rand.nextBoolean()) 16.0f else -16.0f
        this.particleRed = redValue
        this.particleGreen = greenValue
        this.particleBlue = blueVale
        multiplyParticleScaleBy(scale)
        this.maxAge = (8.0 / (Math.random() * 0.8 + 0.2)).toInt()
        this.maxAge = (this.maxAge.toFloat() * scale).toInt()
    }

    override fun renderParticle(buffer: IVertexBuilder, renderInfo: ActiveRenderInfo, partialTicks: Float) {
        this.time = partialTicks
        this.particleX = renderInfo.blockPos.x.toFloat()
        this.particleY = renderInfo.blockPos.y.toFloat()
        this.particleZ = renderInfo.blockPos.z.toFloat()
        this.f0 = renderInfo.pitch
        this.f1 = renderInfo.yaw

        queuedRenders.add(this)

        var particle = (this.age.toFloat() + time) / this.maxAge.toFloat() * 32.0f

        if (particle < 0.0f) particle = 0.0f

        if (particle > 1.0f) particle = 1.0f

        val particleWidth = 0.1f * this.width * particle
        val particleHeight = 0.1f * this.height * particle
        val xPos = (this.prevPosX + (this.posX - this.prevPosX) * time).toFloat()
        val yPos = (this.prevPosY + (this.posY - this.prevPosY) * time).toFloat()
        val zPos = (this.prevPosZ + (this.posZ - this.prevPosZ) * time).toFloat()
        val colorIntensity = 1.0f

        val x1: Double = (-(this.particleX + f0) * particleWidth).toDouble()
        val y1: Double = (-this.particleY * particleHeight).toDouble()
        val z1: Double = (-(this.particleZ + f1) * particleWidth).toDouble()
        val x2: Double = ((f0 - this.particleX) * particleWidth).toDouble()
        val y2: Double = (this.particleY * particleHeight).toDouble()
        val z2: Double = ((f1 - this.particleZ) * particleWidth).toDouble()
        val x3: Double = ((this.particleX + f0) * particleWidth).toDouble()
        val y3: Double = (this.particleY * particleHeight).toDouble()
        val z3: Double = ((this.particleZ + f1) * particleWidth).toDouble()
        val x4: Double = ((this.particleX - f0) * particleWidth).toDouble()
        val y4: Double = (-this.particleY * particleHeight).toDouble()
        val z4: Double = ((this.particleZ - f1) * particleWidth).toDouble()
        val e = Client.player!!.horizontalFacing
        val q = e == Direction.NORTH || e == Direction.SOUTH
        val a = (if (q) if (rotationY < 1.5f && rotationY > 0.5f) rotationY - 1.0f else rotationY + 1.0f else rotationY) * Math.PI
        val cos = cos(a)
        val sin = sin(a)

        Client.textureManager.bindTexture(StringNames.particleLarge)

        if (a < Math.PI) {
            buffer.pos(xPos + x1 * cos, yPos + y1, zPos + z1 * sin).tex(0.0f, 1.0f).color(this.particleRed * colorIntensity, this.particleGreen * colorIntensity, this.particleBlue * colorIntensity, 1f).endVertex()
            buffer.pos(xPos + x1 * cos, yPos + y1, zPos + z1 * sin).tex(0.0f, 1.0f).color(this.particleRed * colorIntensity, this.particleGreen * colorIntensity, this.particleBlue * colorIntensity, 1f).endVertex()
            buffer.pos(xPos + x2 * cos, yPos + y2, zPos + z2 * sin).tex(1.0f, 1.0f).color(this.particleRed * colorIntensity, this.particleGreen * colorIntensity, this.particleBlue * colorIntensity, 1f).endVertex()
            buffer.pos(xPos + x3 * cos, yPos + y3, zPos + z3 * sin).tex(1.0f, 0.0f).color(this.particleRed * colorIntensity, this.particleGreen * colorIntensity, this.particleBlue * colorIntensity, 1f).endVertex()
            buffer.pos(xPos + x4 * cos, yPos + y4, zPos + z4 * sin).tex(0.0f, 0.0f).color(this.particleRed * colorIntensity, this.particleGreen * colorIntensity, this.particleBlue * colorIntensity, 1f).endVertex()
        } else {
            buffer.pos(xPos - x1 * cos, yPos + y1, zPos - z1 * sin).tex(0.0f, 1.0f).color(this.particleRed * colorIntensity, this.particleGreen * colorIntensity, this.particleBlue * colorIntensity, 1f).endVertex()
            buffer.pos(xPos - x2 * cos, yPos + y2, zPos - z2 * sin).tex(1.0f, 1.0f).color(this.particleRed * colorIntensity, this.particleGreen * colorIntensity, this.particleBlue * colorIntensity, 1f).endVertex()
            buffer.pos(xPos - x3 * cos, yPos + y3, zPos - z3 * sin).tex(1.0f, 0.0f).color(this.particleRed * colorIntensity, this.particleGreen * colorIntensity, this.particleBlue * colorIntensity, 1f).endVertex()
            buffer.pos(xPos - x4 * cos, yPos + y4, zPos - z4 * sin).tex(0.0f, 0.0f).color(this.particleRed * colorIntensity, this.particleGreen * colorIntensity, this.particleBlue * colorIntensity, 1f).endVertex()
        }
    }

    /*
    override fun renderParticle(worldrender: BufferBuilder, entity: Entity?, time: Float, x: Float, y: Float, z: Float, f0: Float, f1: Float) {
        this.time = time
        this.particleX = x
        this.particleY = y
        this.particleZ = z
        this.f0 = f0
        this.f1 = f1
        val interpPosX = entity?.positionOffset()?.getX()?: 0.0
        val interpPosY = entity?.positionOffset()?.getY()?: 0.0
        val interpPosZ = entity?.positionOffset()?.getZ()?: 0.0

        queuedRenders.add(this)

        var particle = (this.age.toFloat() + time) / this.maxAge.toFloat() * 32.0f

        if (particle < 0.0f) particle = 0.0f

        if (particle > 1.0f) particle = 1.0f

        val particleWidth = 0.1f * this.width * particle
        val particleHeight = 0.1f * this.height * particle
        val xPos = (this.prevPosX + (this.posX - this.prevPosX) * time.toDouble() - interpPosX).toFloat()
        val yPos = (this.prevPosY + (this.posY - this.prevPosY) * time.toDouble() - interpPosY).toFloat()
        val zPos = (this.prevPosZ + (this.posZ - this.prevPosZ) * time.toDouble() - interpPosZ).toFloat()
        val colorIntensity = 1.0f

        val x1: Double = (-(this.particleX + f0) * particleWidth).toDouble()
        val y1: Double = (-this.particleY * particleHeight).toDouble()
        val z1: Double = (-(this.particleZ + f1) * particleWidth).toDouble()
        val x2: Double = ((f0 - this.particleX) * particleWidth).toDouble()
        val y2: Double = (this.particleY * particleHeight).toDouble()
        val z2: Double = ((f1 - this.particleZ) * particleWidth).toDouble()
        val x3: Double = ((this.particleX + f0) * particleWidth).toDouble()
        val y3: Double = (this.particleY * particleHeight).toDouble()
        val z3: Double = ((this.particleZ + f1) * particleWidth).toDouble()
        val x4: Double = ((this.particleX - f0) * particleWidth).toDouble()
        val y4: Double = (-this.particleY * particleHeight).toDouble()
        val z4: Double = ((this.particleZ - f1) * particleWidth).toDouble()
        val e = Client.player!!.horizontalFacing
        val q = e == Direction.NORTH || e == Direction.SOUTH
        val a = (if (q) if (rotationY < 1.5f && rotationY > 0.5f) rotationY - 1.0f else rotationY + 1.0f else rotationY) * Math.PI
        val cos = cos(a)
        val sin = sin(a)

        Client.textureManager.bindTexture(StringNames.particleLarge)

        if (a < Math.PI) {
            worldrender.pos(xPos + x1 * cos, yPos + y1, zPos + z1 * sin).tex(0.0f, 1.0f).color(this.particleRed * colorIntensity, this.particleGreen * colorIntensity, this.particleBlue * colorIntensity, 1f).endVertex()
            worldrender.pos(xPos + x1 * cos, yPos + y1, zPos + z1 * sin).tex(0.0f, 1.0f).color(this.particleRed * colorIntensity, this.particleGreen * colorIntensity, this.particleBlue * colorIntensity, 1f).endVertex()
            worldrender.pos(xPos + x2 * cos, yPos + y2, zPos + z2 * sin).tex(1.0f, 1.0f).color(this.particleRed * colorIntensity, this.particleGreen * colorIntensity, this.particleBlue * colorIntensity, 1f).endVertex()
            worldrender.pos(xPos + x3 * cos, yPos + y3, zPos + z3 * sin).tex(1.0f, 0.0f).color(this.particleRed * colorIntensity, this.particleGreen * colorIntensity, this.particleBlue * colorIntensity, 1f).endVertex()
            worldrender.pos(xPos + x4 * cos, yPos + y4, zPos + z4 * sin).tex(0.0f, 0.0f).color(this.particleRed * colorIntensity, this.particleGreen * colorIntensity, this.particleBlue * colorIntensity, 1f).endVertex()
        } else {
            worldrender.pos(xPos - x1 * cos, yPos + y1, zPos - z1 * sin).tex(0.0f, 1.0f).color(this.particleRed * colorIntensity, this.particleGreen * colorIntensity, this.particleBlue * colorIntensity, 1f).endVertex()
            worldrender.pos(xPos - x2 * cos, yPos + y2, zPos - z2 * sin).tex(1.0f, 1.0f).color(this.particleRed * colorIntensity, this.particleGreen * colorIntensity, this.particleBlue * colorIntensity, 1f).endVertex()
            worldrender.pos(xPos - x3 * cos, yPos + y3, zPos - z3 * sin).tex(1.0f, 0.0f).color(this.particleRed * colorIntensity, this.particleGreen * colorIntensity, this.particleBlue * colorIntensity, 1f).endVertex()
            worldrender.pos(xPos - x4 * cos, yPos + y4, zPos - z4 * sin).tex(0.0f, 0.0f).color(this.particleRed * colorIntensity, this.particleGreen * colorIntensity, this.particleBlue * colorIntensity, 1f).endVertex()
        }

    }*/

    private fun renderQueued() {
    }

    /**
     * Called to update the entity's position/logic.
     */

    override fun tick() {
        this.prevPosX = this.posX
        this.prevPosY = this.posY
        this.prevPosZ = this.posZ

        if (this.age++ >= this.maxAge) this.setExpired()

        if (prevPosY == posY && motionY > 0) { // detect a collision while moving upwards (can't move up at all)
            this.motionY = -this.motionY
        }

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

    override fun getRenderType(): IParticleRenderType {
        return IParticleRenderType.CUSTOM
    }

    companion object {

        var queuedRenders: Queue<DeathParticles> = ArrayDeque()

        internal fun dispatchQueuedRenders() {
            // Client.minecraft.renderManager.bindTexture(StringNames.particleLarge)

            GLCore.glAlphaTest(true)
            // GLCore.glBlend(true)
            queuedRenders.forEach { p ->
                p.renderQueued()
            }
            // GLCore.glBlend(false)

            queuedRenders.clear()
        }
    }
}
