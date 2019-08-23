package com.saomc.saoui.neo.screens

import com.saomc.saoui.api.elements.neo.NeoIconElement
import com.teamwizardry.librarianlib.features.math.Vec2d
import net.minecraft.inventory.IInventory
import net.minecraft.inventory.Slot

class NeoInventoryGui : NeoGui<Unit>(Vec2d.ZERO) {

    val inventories: HashMap<IInventory, NeoIconElement> = hashMapOf()

    override fun initGui() {
        mc.player.inventoryContainer.inventorySlots.forEach {
            //tlCategory()
        }
    }

    fun setupSlot(slot: Slot){
        //inventories.putIfAbsent(slot.inventory, tlCategory(IconCore.ITEMS))
    }
}