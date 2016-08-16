package com.saomc.colorstates;

import com.saomc.util.OptionCore;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.UUID;
import java.util.WeakHashMap;

@SideOnly(Side.CLIENT)
public class ColorStateHandler {

    private static ColorStateHandler ref;
    WeakHashMap<Class, ColorState> defaultStates = new WeakHashMap<>();
    WeakHashMap<Integer, ColorState> colorStates = new WeakHashMap<>();
    WeakHashMap<UUID, ColorState> playerStates = new WeakHashMap<>();
    WeakHashMap<Integer, Integer> stateKeeper = new WeakHashMap<>();
    WeakHashMap<UUID, Integer> playerKeeper = new WeakHashMap<>();

    private ColorStateHandler() {
        // nill
    }

    @SideOnly(Side.CLIENT)
    public static synchronized ColorStateHandler getInstance() {
        if (ref == null)
            // Only return one instance
            ref = new ColorStateHandler();
        return ref;
    }

    public Object clone()
            throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
        // Cloning not supported
    }

    public synchronized ColorState getDefault(EntityLivingBase entity) {
        return defaultStates.get(entity.getClass());
    }

    // For debug only
    public synchronized boolean hasState(EntityLivingBase entity) {
        if (entity instanceof EntityPlayer) return (playerStates.get(entity.getUniqueID()) != null);
        else return (colorStates.get(entity.getEntityId()) != null);
    }

    public synchronized void remove(EntityLivingBase entity) {
        colorStates.remove(entity.getEntityId());
        stateKeeper.remove(entity.getEntityId());
    }

    public synchronized void remove(EntityPlayer entity) {
        playerStates.remove(entity.getUniqueID());
        playerKeeper.remove(entity.getUniqueID());
    }

    public synchronized boolean isEmpty() {
        return colorStates.isEmpty() && stateKeeper.isEmpty() && playerKeeper.isEmpty() && playerStates.isEmpty();
    }

    public void reset(int entity) {
        colorStates.remove(entity);
        stateKeeper.remove(entity);
    }

    public void reset(UUID entity) {
        playerStates.remove(entity);
        playerKeeper.remove(entity);
    }

    public void clean() {
        stateKeeper.clear();
        colorStates.clear();
        playerKeeper.clear();
        playerStates.clear();
        defaultStates.clear();
    }

    public synchronized void stateColor(EntityLivingBase entity) {
        if (!OptionCore.AGGRO_SYSTEM.getValue()) {
            if (defaultStates.get(entity.getClass()) != null) {
                ColorState state = defaultStates.get(entity.getClass());
                state.glColor();
            } else genDefaultState(entity);
        }
        if (entity instanceof EntityPlayer) {
            if (playerStates.get(entity.getUniqueID()) != null) {
                ColorState state = playerStates.get(entity.getUniqueID());
                state.glColor();
            } else if (defaultStates.get(entity.getClass()) != null) {
                ColorState state = defaultStates.get(entity.getClass());
                state.glColor();
            } else genDefaultState(entity);

        } else {
            if (colorStates.get(entity.getEntityId()) != null) {
                ColorState state = colorStates.get(entity.getEntityId());
                state.glColor();
            } else if (defaultStates.get(entity.getClass()) != null) {
                ColorState state = defaultStates.get(entity.getClass());
                state.glColor();
            } else genDefaultState(entity);
        }
    }


    /**
     * This is used to dynamically update the Entities State.
     *
     * @Param entity = Entity you wish to update
     * @Param newState = The State in which will be set
     * @Param event = if this is done via an event or not.
     * Event is temporary, and depending if it has being changed from the default already, it will persist for either 300 ticks, to 600 ticks.
     * If you pass a false for event, this will permanently update the state for the entire Entity class.
     */
    public synchronized void set(EntityLivingBase entity, ColorState newState, boolean event) {
        ColorState defaultState = defaultStates.get(entity.getClass());
        if (!event) {
            if (defaultStates.get(entity.getClass()) != null) {
                defaultStates.replace(entity.getClass(), defaultState, newState);
                if (OptionCore.DEBUG_MODE.getValue()) System.out.print("WARNING - DEFAULT STATE WAS CHANGED" + "\n");
            } else {
                defaultStates.putIfAbsent(entity.getClass(), newState);
                if (OptionCore.DEBUG_MODE.getValue()) System.out.print("WARNING - DEFAULT STATE WAS CHANGED" + "\n");
            }
        } else if (!(entity instanceof EntityPlayer)) {
            stateKeeper.put(entity.getEntityId(), 1200);
            colorStates.put(entity.getEntityId(), newState);
        } else {
            playerKeeper.put(entity.getUniqueID(), 12000);
            playerStates.replace(entity.getUniqueID(), newState);
        }
    }

    public synchronized void genDefaultState(EntityLivingBase entity) {
        if (getDefault(entity) == null) {
            Minecraft mc = Minecraft.getMinecraft();
            ColorState state = ColorState.getColorState(mc, entity);
            defaultStates.put(entity.getClass(), state);
            if (entity instanceof EntityPlayer) {
                playerStates.putIfAbsent(entity.getUniqueID(), state);
                if (OptionCore.DEBUG_MODE.getValue())
                    if (OptionCore.DEBUG_MODE.getValue()) System.out.print(entity.getName() + " added to map" + "\n");
            } else {
                colorStates.putIfAbsent(entity.getEntityId(), state);
                if (OptionCore.DEBUG_MODE.getValue())
                    if (OptionCore.DEBUG_MODE.getValue()) System.out.print(entity.getName() + " added to map" + "\n");
            }
        }
    }

    public synchronized ColorState getSavedState(EntityLivingBase entity) {
        if (entity instanceof EntityPlayer) {
            if (playerKeeper.get(entity.getUniqueID()) != null)
                return playerStates.get(entity.getUniqueID());
            else {
                return defaultStates.get(entity.getClass());
            }
        } else {
            if (stateKeeper.get(entity.getEntityId()) != null)
                return colorStates.get(entity.getEntityId());
            else {
                return defaultStates.get(entity.getClass());
            }
        }
    }

    public synchronized void updateKeeper() {
        if (!OptionCore.AGGRO_SYSTEM.getValue()) {
            clean();
        }
        if (!stateKeeper.isEmpty())
            stateKeeper.forEach((uuid, ticks) -> {
                --ticks;
                if (ticks == 0) {
                    reset(uuid);
                } else {
                    stateKeeper.put(uuid, ticks);
                }
            });
        if (!playerKeeper.isEmpty())
            playerKeeper.forEach((uuid, ticks) -> {
                --ticks;
                if (ticks == 0) {
                    reset(uuid);
                } else {
                    playerKeeper.put(uuid, ticks);
                }
            });
    }
}
