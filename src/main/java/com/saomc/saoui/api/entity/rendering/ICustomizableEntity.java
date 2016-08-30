package com.saomc.saoui.api.entity.rendering;

/**
 * Part of saoui
 * <p>
 * Implementing this marks the object as having an instance of {@link ICustomizationProvider} to supply.
 * It (currently) only has an effect on subclasses of {@link net.minecraft.entity.EntityLivingBase}, when generating
 * the {@link RenderCapability} for them.
 *
 * @author Bluexin
 */
@FunctionalInterface
public interface ICustomizableEntity {

    /**
     * Gets the instance of {@link ICustomizationProvider} the capability has to use.
     *
     * @return customizer instance
     */
    ICustomizationProvider getProvider();
}
