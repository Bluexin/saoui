package com.saomc.saoui.util

import net.minecraft.entity.Entity
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.MathHelper

val Entity.horizontalFacing
    get() = /*EnumFacing.*/getHorizontal(MathHelper.floor_double((this.rotationYaw * 4.0F / 360.0F) + 0.5) and 3)

fun /*EnumFacing.*/getHorizontal(id: Int): EnumFacing? {
    return when (id) { // S-W-N-E
        0 -> EnumFacing.SOUTH
        1 -> EnumFacing.WEST
        2 -> EnumFacing.NORTH
        3 -> EnumFacing.EAST
        else -> null
    }
}

val ItemStack?.isNotEmpty get() = this != null && this.item != null