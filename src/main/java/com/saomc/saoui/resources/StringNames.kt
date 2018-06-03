package com.saomc.saoui.resources

import com.saomc.saoui.SAOCore
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

@SideOnly(Side.CLIENT)
object StringNames {

    val gui = ResourceLocation(SAOCore.MODID, "textures/guiedt.png")
    val slot = ResourceLocation(SAOCore.MODID, "textures/slot.png")
    val entities = ResourceLocation(SAOCore.MODID, "textures/entities.png")
    val entitiesCustom = ResourceLocation(SAOCore.MODID, "textures/entitiescustom.png")
    val particleLarge = ResourceLocation(SAOCore.MODID, "textures/particlelarge.png")
}
