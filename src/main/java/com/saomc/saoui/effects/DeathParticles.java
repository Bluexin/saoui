package com.saomc.saoui.effects;

import com.saomc.saoui.GLCore;
import com.saomc.saoui.resources.StringNames;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayDeque;
import java.util.Queue;


@SideOnly(Side.CLIENT)
public class DeathParticles extends Particle {

    public static Queue<DeathParticles> queuedRenders = new ArrayDeque<>();

    private float ParticleScale;
    private float time;
    private float particleX;
    private float particleY;
    private float particleZ;
    private float f0;
    private float f1;
    private float rotationY; // Rotation around Y axis
    private float speedRotationY; // Rotation speed around Y axis

    public DeathParticles(World world, double xCoord, double yCoord, double zCoord, float redValue, float greenValue, float blueValue) {
        this(world, xCoord, yCoord, zCoord, redValue, greenValue, blueValue, 1.0F);
    }

    private DeathParticles(World world, double xCoord, double yCoord, double zCoord, float redValue, float greenValue, float blueVale, float scale) {
        super(world, xCoord, yCoord, zCoord, 0.0D, 0.0D, 0.0D);
        this.motionX = (double) ((float) (Math.random() * 2.0D - 1.0D) * 0.05F);
        this.motionY = (double) ((float) (Math.random() * 2.0D - 1.0D) * 0.05F);
        this.motionZ = (double) ((float) (Math.random() * 2.0D - 1.0D) * 0.05F);
        this.rotationY = rand.nextFloat() * 2;
        this.speedRotationY = (rand.nextFloat() + 2.0F) / (rand.nextBoolean() ? 16.0F : -16.0F);
        this.particleRed = redValue;
        this.particleGreen = greenValue;
        this.particleBlue = blueVale;
        this.particleScale *= scale;
        this.ParticleScale = this.particleScale;
        this.particleMaxAge = (int) (8.0D / (Math.random() * 0.8D + 0.2D));
        this.particleMaxAge = (int) ((float) this.particleMaxAge * scale);
    }

    static void dispatchQueuedRenders(Tessellator tessellator) {
        RenderDispatcher.particleFxCount = 0;

        Minecraft.getMinecraft().renderEngine.bindTexture(StringNames.particleLarge);

        GLCore.glAlphaTest(true);
        GLCore.glBlend(true);
        GLCore.begin();
        queuedRenders.forEach(p -> p.renderQueued(tessellator));
        GLCore.draw();
        GLCore.glBlend(false);

        queuedRenders.clear();
    }

    @Override
    public void renderParticle(VertexBuffer worldrender, Entity entity, float time, float x, float y, float z, float f0, float f1) {
        this.time = time;
        this.particleX = x;
        this.particleY = y;
        this.particleZ = z;
        this.f0 = f0;
        this.f1 = f1;

        queuedRenders.add(this);
    }

    private void renderQueued(Tessellator tessellator) {
        float particle = ((float) this.particleAge + time) / (float) this.particleMaxAge * 32.0F;

        if (particle < 0.0F) particle = 0.0F;

        if (particle > 1.0F) particle = 1.0F;

        this.particleScale = this.ParticleScale * particle;
        float scale = 0.1F * this.particleScale;
        float xPos = (float) (this.prevPosX + (this.posX - this.prevPosX) * (double) time - interpPosX);
        float yPos = (float) (this.prevPosY + (this.posY - this.prevPosY) * (double) time - interpPosY);
        float zPos = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * (double) time - interpPosZ);
        float colorIntensity = 1.0F;

        final double x1, x2, x3, x4;
        final double y1, y2, y3, y4;
        final double z1, z2, z3, z4;

        x1 = -(this.particleX + f0) * scale;
        y1 = -this.particleY * scale;
        z1 = -(this.particleZ + f1) * scale;
        x2 = (f0 - this.particleX) * scale;
        y2 = this.particleY * scale;
        z2 = (f1 - this.particleZ) * scale;
        x3 = (this.particleX + f0) * scale;
        y3 = this.particleY * scale;
        z3 = (this.particleZ + f1) * scale;
        x4 = (this.particleX - f0) * scale;
        y4 = -this.particleY * scale;
        z4 = (this.particleZ - f1) * scale;
        EnumFacing e = Minecraft.getMinecraft().player.getHorizontalFacing();
        boolean q = e.equals(EnumFacing.NORTH) || e.equals(EnumFacing.SOUTH);
        final double a = (q ? rotationY < 1.5F && rotationY > 0.5F ? rotationY - 1.0F : rotationY + 1.0F : rotationY) * Math.PI;
        final double cos = Math.cos(a);
        final double sin = Math.sin(a);

        if (a < Math.PI) {
            GLCore.addVertex(xPos + x1 * cos, yPos + y1, zPos + z1 * sin, 0D, 1D, this.particleRed * colorIntensity, this.particleGreen * colorIntensity, this.particleBlue * colorIntensity, 1);
            GLCore.addVertex(xPos + x2 * cos, yPos + y2, zPos + z2 * sin, 1D, 1D, this.particleRed * colorIntensity, this.particleGreen * colorIntensity, this.particleBlue * colorIntensity, 1);
            GLCore.addVertex(xPos + x3 * cos, yPos + y3, zPos + z3 * sin, 1D, 0D, this.particleRed * colorIntensity, this.particleGreen * colorIntensity, this.particleBlue * colorIntensity, 1);
            GLCore.addVertex(xPos + x4 * cos, yPos + y4, zPos + z4 * sin, 0D, 0D, this.particleRed * colorIntensity, this.particleGreen * colorIntensity, this.particleBlue * colorIntensity, 1);
        } else {
            GLCore.addVertex(xPos - x1 * cos, yPos + y1, zPos - z1 * sin, 0D, 1D, this.particleRed * colorIntensity, this.particleGreen * colorIntensity, this.particleBlue * colorIntensity, 1);
            GLCore.addVertex(xPos - x2 * cos, yPos + y2, zPos - z2 * sin, 1D, 1D, this.particleRed * colorIntensity, this.particleGreen * colorIntensity, this.particleBlue * colorIntensity, 1);
            GLCore.addVertex(xPos - x3 * cos, yPos + y3, zPos - z3 * sin, 1D, 0D, this.particleRed * colorIntensity, this.particleGreen * colorIntensity, this.particleBlue * colorIntensity, 1);
            GLCore.addVertex(xPos - x4 * cos, yPos + y4, zPos - z4 * sin, 0D, 0D, this.particleRed * colorIntensity, this.particleGreen * colorIntensity, this.particleBlue * colorIntensity, 1);
        }

    }

    /**
     * Called to update the entity's position/logic.
     */

    @Override
    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        if (this.particleAge++ >= this.particleMaxAge) this.isExpired = true;

        this.motionY += 0.004D;
        this.move(this.motionX, this.motionY, this.motionZ);
        this.motionX *= 0.8999999761581421D;
        this.motionY *= 0.8999999761581421D;
        this.motionZ *= 0.8999999761581421D;
        this.rotationY += this.speedRotationY;
        this.rotationY = this.rotationY % 2.0F;

        if (this.isCollided) {
            this.motionX *= 0.699999988079071D;
            this.motionZ *= 0.699999988079071D;
        }
    }

}
