package com.tencao.saoui.effects.particles

import net.minecraft.client.particle.IParticleRenderType
import net.minecraft.client.particle.SpriteTexturedParticle
import net.minecraft.client.renderer.LightTexture
import net.minecraft.client.world.ClientWorld

class DeathParticle(world: ClientWorld, x: Double, y: Double, z: Double, r: Float, g: Float, b: Float) : SpriteTexturedParticle(world, x, y, z, 0.0, 0.0, 0.0) {

    private var rotationY: Float = 0.toFloat() // Rotation around Y axis
    private val rotSpeed: Float // Rotation speed around Y axis

    init {
        this.motionX = randomMotion
        this.motionY = randomMotion
        this.motionZ = randomMotion
        this.rotSpeed = (Math.random().toFloat() - 0.5f) * 0.1f
        this.particleAngle = Math.random().toFloat() * (Math.PI.toFloat())
        this.rotationY = rand.nextFloat() * 2
        // this.speedRotationY = (rand.nextFloat() + 2.0f) / if (rand.nextBoolean()) 16.0f else -16.0f
        this.particleRed = r
        this.particleGreen = g
        this.particleBlue = b
        this.maxAge = (20.0 / (Math.random() * 0.8 + 0.2)).toInt()
        this.maxAge = (this.maxAge.toFloat() * 1.0f).toInt()
        this.canCollide = true
    }

    // ---- methods used by TexturedParticle.renderParticle() method to find out how to render your particle
    //  the base method just renders a quad, rotated to directly face the player

    // ---- methods used by TexturedParticle.renderParticle() method to find out how to render your particle
    //  the base method just renders a quad, rotated to directly face the player
    // can be used to change the skylight+blocklight brightness of the rendered Particle.
    override fun getBrightnessForRender(partialTick: Float): Int {
        val BLOCK_LIGHT = 15 // maximum brightness
        val SKY_LIGHT = 15 // maximum brightness
        return LightTexture.packLight(BLOCK_LIGHT, SKY_LIGHT)

        // if you want the brightness to be the local illumination (from block light and sky light) you can just use
        //  the Particle.getBrightnessForRender() base method, which contains:
        //    BlockPos blockPos = new BlockPos(this.posX, this.posY, this.posZ);
        //    return this.world.isBlockLoaded(blockPos) ? WorldRenderer.getCombinedLight(this.world, blockPos) : 0;
    }

    // Choose the appropriate render type for your particles:
    // There are several useful predefined types:
    // PARTICLE_SHEET_TRANSLUCENT semi-transparent (translucent) particles
    // PARTICLE_SHEET_OPAQUE    opaque particles
    // TERRAIN_SHEET            particles drawn from block or item textures
    // PARTICLE_SHEET_LIT       appears to be the same as OPAQUE.  Not sure of the difference.  In previous versions of minecraft,
    //                          "lit" particles changed brightness depending on world lighting i.e. block light + sky light
    override fun getRenderType(): IParticleRenderType {
        return IParticleRenderType.PARTICLE_SHEET_LIT
    }

    /*
    override fun renderParticle(buffer: IVertexBuilder, renderInfo: ActiveRenderInfo, partialTicks: Float) {
        super.renderParticle(buffer, renderInfo, partialTicks)
        /*
        val vector3d = renderInfo.projectedView
        val xPos = (MathHelper.lerp(partialTicks.toDouble(), prevPosX, posX) - vector3d.getX()).toFloat()
        val yPos = (MathHelper.lerp(partialTicks.toDouble(), prevPosY, posY) - vector3d.getY()).toFloat()
        val zPos = (MathHelper.lerp(partialTicks.toDouble(), prevPosZ, posZ) - vector3d.getZ()).toFloat()
        val quaternion: Quaternion
        if (particleAngle == 0.0f) {
            quaternion = renderInfo.rotation
        } else {
            quaternion = Quaternion(renderInfo.rotation)
            val f3 = MathHelper.lerp(partialTicks, prevParticleAngle, particleAngle)
            quaternion.multiply(Vector3f.ZP.rotation(f3))
        }

        val vector3f1 = Vector3f(-1.0f, -1.0f, 0.0f)
        vector3f1.transform(quaternion)
        val avector3f = arrayOf(
            Vector3f(-1.0f, -1.0f, 0.0f),
            Vector3f(-1.0f, 1.0f, 0.0f),
            Vector3f(1.0f, 1.0f, 0.0f),
            Vector3f(1.0f, -1.0f, 0.0f)
        )
        val f4 = getScale(partialTicks)

        for (i in 0..3) {
            val vector3f = avector3f[i]
            vector3f.transform(quaternion)
            vector3f.mul(f4)
            vector3f.add(xPos, yPos, zPos)
        }
        val colorIntensity = 1.0f

        val e = Client.player!!.horizontalFacing
        val q = e == Direction.NORTH || e == Direction.SOUTH
        val a = (if (q) if (rotationY < 1.5f && rotationY > 0.5f) rotationY - 1.0f else rotationY + 1.0f else rotationY) * Math.PI
        val cos = cos(a)
        val sin = sin(a)

        val j = getBrightnessForRender(partialTicks)
        // Client.textureManager.bindTexture(StringNames.particleLarge)

        if (a < Math.PI) {
            buffer.pos(xPos * cos, yPos.toDouble(), zPos * sin).tex(minU, maxV).color(this.particleRed * colorIntensity, this.particleGreen * colorIntensity, this.particleBlue * colorIntensity, 1f).lightmap(j).endVertex()
            buffer.pos(xPos * cos, yPos.toDouble(), zPos * sin).tex(maxU, maxV).color(this.particleRed * colorIntensity, this.particleGreen * colorIntensity, this.particleBlue * colorIntensity, 1f).lightmap(j).endVertex()
            buffer.pos(xPos * cos, yPos.toDouble(), zPos * sin).tex(maxU, minV).color(this.particleRed * colorIntensity, this.particleGreen * colorIntensity, this.particleBlue * colorIntensity, 1f).lightmap(j).endVertex()
            buffer.pos(xPos * cos, yPos.toDouble(), zPos * sin).tex(minU, minV).color(this.particleRed * colorIntensity, this.particleGreen * colorIntensity, this.particleBlue * colorIntensity, 1f).lightmap(j).endVertex()
        } else {
            buffer.pos(xPos * cos, yPos.toDouble(), zPos * sin).tex(minU, maxV).color(this.particleRed * colorIntensity, this.particleGreen * colorIntensity, this.particleBlue * colorIntensity, 1f).lightmap(j).endVertex()
            buffer.pos(xPos * cos, yPos.toDouble(), zPos * sin).tex(maxU, maxV).color(this.particleRed * colorIntensity, this.particleGreen * colorIntensity, this.particleBlue * colorIntensity, 1f).lightmap(j).endVertex()
            buffer.pos(xPos * cos, yPos.toDouble(), zPos * sin).tex(maxU, minV).color(this.particleRed * colorIntensity, this.particleGreen * colorIntensity, this.particleBlue * colorIntensity, 1f).lightmap(j).endVertex()
            buffer.pos(xPos * cos, yPos.toDouble(), zPos * sin).tex(minU, minV).color(this.particleRed * colorIntensity, this.particleGreen * colorIntensity, this.particleBlue * colorIntensity, 1f).lightmap(j).endVertex()
        }*/
    }*/

    override fun tick() {
        this.prevPosX = this.posX
        this.prevPosY = this.posY
        this.prevPosZ = this.posZ

        if (this.age++ >= this.maxAge) this.setExpired()

        this.motionY += 0.004
        this.move(this.motionX, this.motionY, this.motionZ)
        this.motionX *= 0.8999999761581421
        this.motionY *= 0.8999999761581421
        this.motionZ *= 0.8999999761581421

        prevParticleAngle = particleAngle
        particleAngle += Math.PI.toFloat() * rotSpeed * 2.0f

        if (this.onGround) {
            this.motionX *= 0.699999988079071
            this.motionZ *= 0.699999988079071
            particleAngle = 0.0f
            prevParticleAngle = particleAngle
        }
    }

    companion object {
        val randomMotion get() = ((Math.random() * 2.0 - 1.0).toFloat() * 0.05f).toDouble()
    }
}
