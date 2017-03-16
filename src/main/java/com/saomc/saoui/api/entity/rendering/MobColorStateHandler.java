package com.saomc.saoui.api.entity.rendering;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.passive.IAnimals;

import java.lang.ref.WeakReference;
import java.util.Objects;

import static com.saomc.saoui.api.entity.rendering.ColorState.*;

/**
 * Part of saoui
 * <p>
 * Default implementation for mobs.
 * This will be the most common implementation for mobs and NPCs.
 *
 * @author Bluexin
 */
public class MobColorStateHandler implements IColorStateHandler {

    private final WeakReference<EntityLivingBase> theEnt;

    /**
     * Caches value when it can (ie the value will never change again)
     */
    private ColorState cached = null;

    MobColorStateHandler(EntityLivingBase entity) {
        this.theEnt = new WeakReference<>(entity);
    }

    @SideOnly(Side.CLIENT)
    private ColorState getColor() {
        if (this.cached != null) return cached;
        EntityLivingBase entity = theEnt.get();
        if (entity == null) return cached = INVALID;
//        if (!entity.isNonBoss()) return cached = BOSS;
        if (entity instanceof EntityWolf && ((EntityWolf) entity).isAngry()) return KILLER;
        if (entity instanceof EntityTameable && ((EntityTameable) entity).isTamed())
            return Objects.equals(((EntityTameable) entity).getOwner(), Minecraft.getMinecraft().thePlayer) ? INNOCENT : VIOLENT;
        if (entity instanceof IMob)
            return entity.canEntityBeSeen(Minecraft.getMinecraft().thePlayer) ? KILLER : VIOLENT;
        if (entity instanceof IAnimals) return cached = INNOCENT;
        if (entity instanceof IEntityOwnable) return cached = VIOLENT;
        return cached = INVALID;
    }

    /**
     * @return the color state the entity should be showing.
     */
    @Override
    @SideOnly(Side.CLIENT)
    public ColorState getColorState() {
        return getColor();
    }
}
