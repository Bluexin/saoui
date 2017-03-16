package com.saomc.saoui.api.entity.rendering;

import be.bluexin.saomclib.capabilities.AbstractCapability;
import be.bluexin.saomclib.capabilities.AbstractEntityCapability;
import be.bluexin.saomclib.capabilities.Key;
import com.saomc.saoui.SAOCore;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;

/**
 * Part of saoui
 * <p>
 * This {@link net.minecraftforge.common.IExtendedEntityProperties} contains info for the SAOUI about color states, the amount of HP bars to render, etc.
 * It also contains info like custom offsets for hp bars to fix some renders (lookin at ya chicken).
 *
 * @author Bluexin
 */
public class RenderCapability extends AbstractEntityCapability {

    @Key
    private static final ResourceLocation KEY = new ResourceLocation(SAOCore.MODID, "renders");

    /**
     * Where this capability is getting it's customization settings from.
     */
    public ICustomizationProvider customizationProvider;

    /**
     * Where this capability is getting it's Color State data from.
     */
    public IColorStateHandler colorStateHandler;

    public RenderCapability() {

    }

    private static ICustomizationProvider getProvider(EntityLivingBase ent) {
        return ent instanceof ICustomizableEntity ? ((ICustomizableEntity) ent).getProvider() : new StaticCustomizationProvider(0.0D, 0.0D, 0.0D, 1.0D, 1); // TODO: implement with event or something
    }

    private static IColorStateHandler getColorState(EntityLivingBase ent) {
        return ent instanceof IColorStatedEntity ? ((IColorStatedEntity) ent).getColorState() : ent instanceof EntityPlayer ? new PlayerColorStateHandler((EntityPlayer) ent) : new MobColorStateHandler(ent); // TODO: implement with event or something
    }

    /**
     * Gets the capability for an entity.
     *
     * @param ent the entity to get the capability for
     * @return the capability
     */
    public static RenderCapability get(EntityLivingBase ent) {
        //noinspection ConstantConditions
        return (RenderCapability) ent.getExtendedProperties(KEY.toString());
    }

    /**
     * Sync the client player.
     * Not yet sure how to sync everything properly.
     *
     * @param player player to sync
     */
    public static void syncClient(EntityPlayer player) {// TODO: implement
//        PacketPipeline.sendTo(new SyncExtStats(player), (EntityPlayerMP) player);
    }

    @NotNull
    @Override
    public AbstractCapability setup(Entity ent) {
        this.customizationProvider = getProvider((EntityLivingBase) ent);
        this.colorStateHandler = getColorState((EntityLivingBase) ent);
        return super.setup(ent);
    }

    @Override
    public void saveNBTData(NBTTagCompound compound) {
        NBTTagCompound tag = new NBTTagCompound();
        customizationProvider.save(tag);
        colorStateHandler.save(tag);
        compound.setTag(KEY.toString(), tag);
    }

    @Override
    public void loadNBTData(NBTTagCompound compound) {
        NBTBase tag = compound.getTag(KEY.toString());
        customizationProvider.load((NBTTagCompound) tag);
        colorStateHandler.load((NBTTagCompound) tag);
    }
}
