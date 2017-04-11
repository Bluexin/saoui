package com.saomc.saoui.social;

import com.saomc.saoui.config.OptionCore;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Part of SAOUI
 *
 * @author Bluexin
 */
public class StaticPlayerHelper {
    private static final float HEALTH_ANIMATION_FACTOR = 0.075F;
    private static final float HEALTH_FRAME_FACTOR = HEALTH_ANIMATION_FACTOR * HEALTH_ANIMATION_FACTOR * 0x40 * 0x64;
    private static Map<UUID, Float> healthSmooth = new HashMap<>();
    private static Map<UUID, Float> hungerSmooth = new HashMap<>();

    public static List<EntityPlayer> listOnlinePlayers(Minecraft mc, double range) {
        //noinspection ConstantConditions -> how could 'p' be null?
        return mc.world.getPlayers(EntityPlayer.class, p -> mc.player.getDistanceToEntity(p) <= range);
    }

    private static List<EntityPlayer> listOnlinePlayers(Minecraft mc) {
        return mc.world.playerEntities;
    }

    public static EntityPlayer findOnlinePlayer(Minecraft mc, String username) {
        return mc.world.getPlayerEntityByName(username);
    }

    private static boolean[] isOnline(Minecraft mc, String[] names) { // TODO: update a boolean[] upon player join server? (/!\ client-side)
        final List<EntityPlayer> players = listOnlinePlayers(mc);
        final boolean[] online = new boolean[names.length];

        for (int i = 0; i < names.length; i++) {
            final int index = i;
            online[i] = players.stream().anyMatch(player -> getName(player).equals(names[index]));
        }

        return online;
    }

    public static boolean isOnline(Minecraft mc, String name) {
        return isOnline(mc, new String[]{name})[0];
    }

    public static String getName(EntityPlayer player) {
        return player == null ? "" : player.getDisplayNameString();
    }

    public static String getName(Minecraft mc) {
        return getName(mc.player);
    }

    public static String unformatName(String name) {
        int index = name.indexOf("�");

        while (index != -1) {
            if (index + 1 < name.length()) name = name.replace(name.substring(index, index + 2), "");
            else name = name.replace("�", "");

            index = name.indexOf("�");
        }

        return name;
    }

    public static float getHealth(final Minecraft mc, final Entity entity, final float time) { // FIXME: this seems to break if called many times in a single render frame
        if (OptionCore.SMOOTH_HEALTH.isEnabled()) {
            final float healthReal;
            final UUID uuid = entity.getUniqueID();

            if (entity instanceof EntityLivingBase) healthReal = ((EntityLivingBase) entity).getHealth();
            else healthReal = entity.isDead ? 0F : 1F;

            if (healthSmooth.containsKey(uuid)) {
                float healthValue = healthSmooth.get(uuid);
                if (healthValue > healthReal) {
                    healthValue = healthReal;
                    healthSmooth.put(uuid, healthReal);
                }

                if ((healthReal <= 0) && (entity instanceof EntityLivingBase)) {
                    final float value = (float) (18 - ((EntityLivingBase) entity).deathTime) / 18;

                    if (value <= 0) healthSmooth.remove(uuid);

                    return healthValue * value;
                } else if (Math.round(healthValue * 10) != Math.round(healthReal * 10))
                    healthValue = healthValue + (healthReal - healthValue) * (gameTimeDelay(mc, time) * HEALTH_ANIMATION_FACTOR);
                else healthValue = healthReal;

                healthSmooth.put(uuid, healthValue);
                return healthValue;
            } else {
                healthSmooth.put(uuid, healthReal);
                return healthReal;
            }
        } else
            return (entity instanceof EntityLivingBase ? ((EntityLivingBase) entity).getHealth() : (entity.isDead ? 0F : 1F));
    }

    public static float getMaxHealth(final Entity entity) {
        return entity instanceof EntityLivingBase ? ((EntityLivingBase) entity).getMaxHealth() : 1F;
    }

    public static float getHungerFract(final Minecraft mc, final Entity entity, final float time) {
        if (!(entity instanceof EntityPlayer)) return 1.0F;
        EntityPlayer player = (EntityPlayer) entity;
        final float hungerReal;
        if (OptionCore.SMOOTH_HEALTH.isEnabled()) {
            final UUID uuid = entity.getUniqueID();

            hungerReal = player.getFoodStats().getFoodLevel();

            if (hungerSmooth.containsKey(uuid)) {
                float hungerValue = hungerSmooth.get(uuid);
                if (hungerValue > hungerReal) {
                    hungerValue = hungerReal;
                    hungerSmooth.put(uuid, hungerReal);
                }

                if (hungerReal <= 0) {
                    final float value = (float) (18 - player.deathTime) / 18;

                    if (value <= 0) hungerSmooth.remove(uuid);

                    return hungerValue * value;
                } else if (Math.round(hungerValue * 10) != Math.round(hungerReal * 10))
                    hungerValue = hungerValue + (hungerReal - hungerValue) * (gameTimeDelay(mc, time) * HEALTH_ANIMATION_FACTOR);
                else hungerValue = hungerReal;

                hungerSmooth.put(uuid, hungerValue);
                return hungerValue / 20.0F;
            } else {
                hungerSmooth.put(uuid, hungerReal);
                return hungerReal / 20.0F;
            }
        } else return player.getFoodStats().getFoodLevel() / 20.0F;
    }

    private static float gameTimeDelay(Minecraft mc, float time) {
        return time >= 0F ? time : HEALTH_FRAME_FACTOR / gameFPS(mc);
    }

    public static boolean isCreative(EntityPlayer player) { // TODO: test this!
        return player.capabilities.isCreativeMode;
    }

    private static int gameFPS(Minecraft mc) {
        return mc.getLimitFramerate();
    }

    public static EntityPlayer thePlayer() {
        return Minecraft.getMinecraft().player;
    }
}
