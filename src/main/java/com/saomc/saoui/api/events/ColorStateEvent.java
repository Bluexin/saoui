package com.saomc.saoui.api.events;

import com.saomc.saoui.api.entity.rendering.IColorStateHandler;
import com.saomc.saoui.api.entity.rendering.ICustomizationProvider;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.common.eventhandler.Event;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ColorStateEvent extends Event {

    public ColorStateEvent(EntityLivingBase entityLivingBase){
        theEnt = entityLivingBase;
    }

    private final EntityLivingBase theEnt;

    private IColorStateHandler state = null;

    private ICustomizationProvider provider = null;

    public void setProvider(@Nonnull ICustomizationProvider provider) {
        this.provider = provider;
    }

    public void setState(@Nonnull IColorStateHandler state) {
        this.state = state;
    }

    @Nonnull
    public EntityLivingBase getEntity() {
        return theEnt;
    }

    @Nullable
    public IColorStateHandler getState() {
        return state;
    }

    @Nullable
    public ICustomizationProvider getProvider() {
        return provider;
    }
}
