package com.tencao.saoui.api.events;

import com.tencao.saoui.api.entity.rendering.IColorStateHandler;
import com.tencao.saoui.api.entity.rendering.ICustomizationProvider;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.eventbus.api.Event;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ColorStateEvent extends Event {

    public ColorStateEvent(LivingEntity entityLivingBase){
        theEnt = entityLivingBase;
    }

    private final LivingEntity theEnt;

    private IColorStateHandler state = null;

    private ICustomizationProvider provider = null;

    public void setProvider(@Nonnull ICustomizationProvider provider) {
        this.provider = provider;
    }

    public void setState(@Nonnull IColorStateHandler state) {
        this.state = state;
    }

    @Nonnull
    public LivingEntity getEntity() {
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
