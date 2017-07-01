package com.saomc.saoui.resources

import com.saomc.saoui.SAOCore
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

@SideOnly(Side.CLIENT)
object StringNames {

    val gui = ResourceLocation(SAOCore.MODID, "textures/guiet.png")
    val guiCustom = ResourceLocation(SAOCore.MODID, "textures/guicustom.png")
    val slot = ResourceLocation(SAOCore.MODID, "textures/slot.png")
    val icons = ResourceLocation(SAOCore.MODID, "textures/icons.png")
    val iconsCustom = ResourceLocation(SAOCore.MODID, "textures/iconscustom.png")
    val effects = ResourceLocation(SAOCore.MODID, "textures/gui.png")
    val effectsCustom = ResourceLocation(SAOCore.MODID, "textures/guicustom.png")
    val entities = ResourceLocation(SAOCore.MODID, "textures/entities.png")
    val entitiesCustom = ResourceLocation(SAOCore.MODID, "textures/entitiescustom.png")
    val particleLarge = ResourceLocation(SAOCore.MODID, "textures/particlelarge.png")

}
