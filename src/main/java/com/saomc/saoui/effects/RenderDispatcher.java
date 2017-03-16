package com.saomc.saoui.effects;

import com.saomc.saoui.GLCore;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.profiler.Profiler;
import org.lwjgl.opengl.GL11;

/**
 * This code was original created by <Vazkii> and has been modified to our needs
 * All credit goes to him
 */
@SideOnly(Side.CLIENT)
public final class RenderDispatcher {

    public static int particleFxCount = 0;

    public static void dispatch() {
        Tessellator tessellator = Tessellator.instance;

        Profiler profiler = Minecraft.getMinecraft().mcProfiler;

        //GL11.glPushAttrib(GL11.GL_LIGHTING);
        GLCore.glBlend(true);
        GLCore.blendFunc(GL11.GL_ONE, GL11.GL_ONE);

        profiler.startSection("death particle");
        DeathParticles.dispatchQueuedRenders(tessellator);
        profiler.endSection();
        GLCore.blendFunc(GL11.GL_ONE, GL11.GL_ZERO);
        GLCore.glBlend(false);
        //GL11.glPopAttrib();
    }
}
