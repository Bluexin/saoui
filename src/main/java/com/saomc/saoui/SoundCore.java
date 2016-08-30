package com.saomc.saoui;

import com.saomc.saoui.util.OptionCore;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;

public class SoundCore {

    public static final String CONFIRM = "sao.confirm";
    public static final String DIALOG_CLOSE = "sao.dialog.close";
    public static final String MENU_POPUP = "sao.menu.popup";
    public static final String MESSAGE = "sao.message";
    public static final String ORB_DROPDOWN = "sao.orb.dropdown";
    public static final String PARTICLES_DEATH = "sao.particles.death";
    public static final String LOW_HEALTH = "sao.low.health";

    private static ResourceLocation getResource(String name) {
        return new ResourceLocation(SAOCore.MODID, name);
    }

    public static boolean isSfxPlaying(String name) {
        return Minecraft.getMinecraft().getSoundHandler().isSoundPlaying(create(getResource(name)));
    }

    public static void playFromEntity(Entity entity, String name) {
        if (entity != null) {
            playAtEntity(entity, name);
        }
    }

    public static void playAtEntity(Entity entity, String name) {
        final Minecraft mc = Minecraft.getMinecraft();

        if (mc.theWorld != null && mc.theWorld.isRemote) {
            play(mc.getSoundHandler(), name, (float) entity.posX, (float) entity.posY, (float) entity.posZ);
        }
    }

    public static void play(Minecraft mc, String name) {
        if (mc != null && mc.theWorld != null && mc.theWorld.isRemote) {
            play(mc.getSoundHandler(), name);
        }
    }

    public static void play(SoundHandler handler, String name) {
        if ((OptionCore.SOUND_EFFECTS.getValue()) && (handler != null)) {
            handler.playSound(create(getResource(name)));
        }
    }

    private static void play(SoundHandler handler, String name, float x, float y, float z) {
        if ((OptionCore.SOUND_EFFECTS.getValue()) && (handler != null)) {
            handler.playSound(create(getResource(name), x, y, z));
        }
    }

    public static void resumeSounds(Minecraft mc) {
        mc.getSoundHandler().resumeSounds();
    }

    //Helper functions - 1.8.8 mirror
    public static PositionedSoundRecord create(ResourceLocation soundResource) {
        return new PositionedSoundRecord(soundResource, SoundCategory.MASTER, 1.0F, 1.0F, false, 0, ISound.AttenuationType.NONE, 0.0F, 0.0F, 0.0F);
    }

    public static PositionedSoundRecord create(ResourceLocation soundResource, float xPosition, float yPosition, float zPosition) {
        return new PositionedSoundRecord(soundResource, SoundCategory.MASTER, 4.0F, 1.0F, false, 0, ISound.AttenuationType.LINEAR, xPosition, yPosition, zPosition);
    }
}
