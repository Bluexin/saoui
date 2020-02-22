package com.saomc.saoui.util

import com.teamwizardry.librarianlib.features.kotlin.Minecraft
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.ContainerWorkbench

class ContainerWorkbench(): ContainerWorkbench(Minecraft().player.inventory, Minecraft().world, Minecraft().player.position) {

    override fun canInteractWith(playerIn: EntityPlayer): Boolean {
        return true
    }
}