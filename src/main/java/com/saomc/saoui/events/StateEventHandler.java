package com.saomc.saoui.events;

import com.saomc.saoui.colorstates.ColorState;
import com.saomc.saoui.colorstates.ColorStateHandler;
import com.saomc.saoui.util.OptionCore;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.List;

import static com.saomc.saoui.events.EventCore.mc;

/**
 * This is purely for the ColorStateHandler
 */
public class StateEventHandler {

    private static int ticks = 0;

    static void checkTicks(TickEvent.RenderTickEvent e) {
        if (!OptionCore.DISABLE_TICKS.getValue() && mc.theWorld != null && e.phase.equals(TickEvent.Phase.END)) {
            if (ticks >= 10) {
                checkRadius();
                resetState();
                ticks = 0;
            } else ++ticks;

        }
    }

    static void checkRadius() {
        List<Entity> entities = mc.theWorld.getEntitiesWithinAABBExcludingEntity(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().expand(20.0D, 20.0D, 20.0D));
        entities.removeIf(ent -> !(ent != null && ent instanceof EntityLivingBase && ent.worldObj.isRemote));
        entities.stream().filter(ent -> ((EntityLivingBase) ent).getHealth() <= 0 && OptionCore.PARTICLES.getValue()).forEach(ent -> RenderHandler.deadHandlers.add((EntityLivingBase) ent));
        entities.stream().filter(ent -> mc.thePlayer.canEntityBeSeen(ent) && ColorStateHandler.getInstance().getSavedState((EntityLivingBase) ent) == ColorState.VIOLENT).forEach(ent -> ColorStateHandler.getInstance().set((EntityLivingBase) ent, ColorState.KILLER, true));
    }

    static void resetState() {
        if (OptionCore.AGGRO_SYSTEM.getValue()) ColorStateHandler.getInstance().updateKeeper();
        else if (!ColorStateHandler.getInstance().isEmpty()) ColorStateHandler.getInstance().clean();
    }

    static void genStateMaps(EntityEvent.EntityConstructing e) {
        if (e.getEntity() instanceof EntityLivingBase)
            if (ColorStateHandler.getInstance().getDefault((EntityLivingBase) e.getEntity()) == null && !(e.getEntity() instanceof EntityPlayer))
                ColorStateHandler.getInstance().genDefaultState((EntityLivingBase) e.getEntity());
    }

    public static void getColor(EntityLivingBase entity) {
        ColorStateHandler.getInstance().stateColor(entity);
    }

}
