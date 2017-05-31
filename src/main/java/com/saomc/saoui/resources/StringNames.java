package com.saomc.saoui.resources;

import com.saomc.saoui.SAOCore;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class StringNames {

    public static final ResourceLocation gui = new ResourceLocation(SAOCore.MODID, "textures/guiedt.png");
    public static final ResourceLocation guiCustom = new ResourceLocation(SAOCore.MODID, "textures/guicustom.png");
    public static final ResourceLocation slot = new ResourceLocation(SAOCore.MODID, "textures/slot.png");
    public static final ResourceLocation icons = new ResourceLocation(SAOCore.MODID, "textures/icons.png");
    public static final ResourceLocation iconsCustom = new ResourceLocation(SAOCore.MODID, "textures/iconscustom.png");
    public static final ResourceLocation effects = new ResourceLocation(SAOCore.MODID, "textures/gui.png");
    public static final ResourceLocation effectsCustom = new ResourceLocation(SAOCore.MODID, "textures/guicustom.png");
    public static final ResourceLocation entities = new ResourceLocation(SAOCore.MODID, "textures/entities.png");
    public static final ResourceLocation entitiesCustom = new ResourceLocation(SAOCore.MODID, "textures/entitiescustom.png");
    public static final ResourceLocation particleLarge = new ResourceLocation(SAOCore.MODID, "textures/particlelarge.png");

    private StringNames() {
    }

}
