package com.saomc.saoui.api.entity.rendering;

import com.saomc.saoui.SAOCore;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import java.lang.ref.WeakReference;

import static com.saomc.saoui.api.entity.rendering.ColorState.*;

/**
 * Part of saoui
 *
 * @author Bluexin
 */
public class PlayerColorStateHandler implements IColorStateHandler {

    private static final String KEY = new ResourceLocation(SAOCore.MODID, "playerColorHandler").toString();

    /**
     * How long a player's state will persist.
     * <p>
     * Example:
     * player A (innocent) hits player B (innocent)
     * player A gets VIOLENT status for set amount of ticks
     * player A (violent) hits player B (innocent)
     * player A's VIOLENT status will be <b>reset</b> to set amount of ticks
     * player A (violent) kills player B (innocent)
     * player A gets KILLER status for set amount of ticks
     * player A (killer) kills player B (innocent) again
     * player A's KILLER status duration will be <b>extended</b> by set amount of ticks
     * player A (killer) hits player B (innocent)
     * player A's status doesn't change
     */
    private static final long TICKS_PER_STATE = 12000;

    private final WeakReference<EntityPlayer> thePlayer;
    private long ticksForRedemption = 0;
    private ColorState currentState = INNOCENT;

    public PlayerColorStateHandler(EntityPlayer thePlayer) {
        this.thePlayer = new WeakReference<>(thePlayer);
    }

    /**
     * @return the color state the entity should be showing.
     */
    @Override
    public ColorState getColorState() {
        return this.currentState;
    }

    /**
     * Called every tick.
     * Use this to handle anything special.
     */
    @Override
    public void tick() {
        if (ticksForRedemption > 0) {
            if (--ticksForRedemption == 0) {
                if (currentState == VIOLENT) currentState = INNOCENT;
                else {
                    currentState = VIOLENT;
                    ticksForRedemption = TICKS_PER_STATE;
                }
            }
        }
    }

    /**
     * Called when the holder of this state handler hits another player.
     *
     * @param target the player hit
     */
    public void hit(EntityPlayer target) {
        ColorState targetState = RenderCapability.get(target).colorStateHandler.getColorState();
        if (targetState != KILLER && targetState != VIOLENT && this.currentState != KILLER) {
            this.currentState = VIOLENT;
            this.ticksForRedemption = TICKS_PER_STATE;
        }
    }

    /**
     * Called when the holder of this state handler kills another player.
     *
     * @param target the player killed
     */
    public void kill(EntityPlayer target) {
        ColorState targetState = RenderCapability.get(target).colorStateHandler.getColorState();
        if (targetState != KILLER && targetState != VIOLENT) {
            if (this.currentState == KILLER) this.ticksForRedemption += TICKS_PER_STATE;
            else {
                this.currentState = KILLER;
                this.ticksForRedemption = TICKS_PER_STATE;
            }
        }
    }

    /**
     * Save any data to NBT format.
     * The implementation has to create his own sub-tag and to behave properly.
     * This will be used for sync between client and server, and saving on world shutting down.
     *
     * @param tag the NBT tag to save to
     */
    @Override
    public void save(NBTTagCompound tag) {
        NBTTagCompound atag = new NBTTagCompound();
        atag.setLong("ticksForRedemption", this.ticksForRedemption);
        atag.setInteger("state", this.currentState.ordinal());
        tag.setTag(KEY, atag);
    }

    /**
     * Load any data from NBT format.
     * The implementation has to retrieve his own sub-tag and to behave properly.
     * This will be used for sync between client and server, and loading on world starting up.
     *
     * @param tag the NBT tag to save to
     */
    @Override
    public void load(NBTTagCompound tag) {
        NBTTagCompound atag = tag.getCompoundTag(KEY);
        this.ticksForRedemption = atag.getLong("ticksForRedemption");
        this.currentState = ColorState.values()[atag.getInteger("state")];
    }
}
