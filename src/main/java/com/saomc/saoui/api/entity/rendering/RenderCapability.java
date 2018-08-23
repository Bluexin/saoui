package com.saomc.saoui.api.entity.rendering;

import be.bluexin.saomclib.capabilities.*;
import com.saomc.saoui.SAOCore;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;

/**
 * Part of saoui
 * <p>
 * This {@link Capability} contains info for the SAOUI about color states, the amount of HP bars to render, etc.
 * It also contains info like custom offsets for hp bars to fix some renders (lookin at ya chicken).
 *
 * @author Bluexin
 */
public class RenderCapability extends AbstractEntityCapability {

    /**
     * Unique instance for the capability (for registering).
     */
    @CapabilityInject(RenderCapability.class)
    public static final Capability<RenderCapability> RENDER_CAPABILITY = null;

    @Key
    public static final ResourceLocation KEY = new ResourceLocation(SAOCore.MODID, "renders");

    /**
     * Where this capability is getting it's customization settings from.
     */
    public /*lateinit*/ ICustomizationProvider customizationProvider;

    /**
     * Where this capability is getting it's Color State data from.
     */
    public /*lateinit*/ IColorStateHandler colorStateHandler;

    @NotNull
    @Override
    public AbstractCapability setup(@NotNull Object param) {
        super.setup(param);
        EntityLivingBase ent = (EntityLivingBase) param;
        this.customizationProvider = getProvider(ent);
        this.colorStateHandler = getColorState(ent);
        return this;
    }

    /**
     * Register capability to registry.
     * This should only be called by the SAOUI!
     */
    public static void registerCapability() {
        CapabilitiesHandler.INSTANCE.registerEntityCapability(RenderCapability.class, new RenderCapability.Storage(), (it) -> it instanceof EntityLivingBase);
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
        return CapabilitiesExtendedPropertyKt.getCapability(ent, RENDER_CAPABILITY, null);
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

    private static class Storage implements Capability.IStorage<RenderCapability> {
        private Storage() {
        }

        @Override
        public NBTBase writeNBT(Capability<RenderCapability> capability, RenderCapability instance, EnumFacing side) {
            NBTTagCompound tag = new NBTTagCompound();
            instance.customizationProvider.save(tag);
            instance.colorStateHandler.save(tag);
            return tag;
        }

        @Override
        public void readNBT(Capability<RenderCapability> capability, RenderCapability instance, EnumFacing side, NBTBase nbt) {
            instance.customizationProvider.load((NBTTagCompound) nbt);
            instance.colorStateHandler.load((NBTTagCompound) nbt);

//            EntityLivingBase entitylivingbase = instance.theEnt.get();

            /*if (entitylivingbase != null && !entitylivingbase.worldObj.isRemote)
                RenderCapability.syncClient((EntityPlayer) instance.theEnt.get());*/

        }
    }
}
