package com.saomc.saoui.resources;

import com.saomc.saoui.SAOCore;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public final class StringNames {

    public static final ResourceLocation gui = new ResourceLocation(SAOCore.MODID, "textures/guiEDT.png");
    public static final ResourceLocation guiCustom = new ResourceLocation(SAOCore.MODID, "textures/guiCustom.png");
    public static final ResourceLocation slot = new ResourceLocation(SAOCore.MODID, "textures/slot.png");
    public static final ResourceLocation icons = new ResourceLocation(SAOCore.MODID, "textures/icons.png");
    public static final ResourceLocation iconsCustom = new ResourceLocation(SAOCore.MODID, "textures/iconsCustom.png");
    public static final ResourceLocation effects = new ResourceLocation(SAOCore.MODID, "textures/gui.png");
    public static final ResourceLocation effectsCustom = new ResourceLocation(SAOCore.MODID, "textures/guiCustom.png");
    public static final ResourceLocation entities = new ResourceLocation(SAOCore.MODID, "textures/entities.png");
    public static final ResourceLocation entitiesCustom = new ResourceLocation(SAOCore.MODID, "textures/entitiesCustom.png");
    public static final ResourceLocation particleLarge = new ResourceLocation(SAOCore.MODID, "textures/particleLarge.png");

    private StringNames() {
    }

}
