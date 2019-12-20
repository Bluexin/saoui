package com.saomc.saoui.screens.menus

import com.saomc.saoui.api.elements.IconElement
import com.saomc.saoui.screens.CoreGUI
import com.teamwizardry.librarianlib.features.math.Vec2d
import net.minecraft.inventory.IInventory
import net.minecraft.inventory.Slot

class InventoryGui : CoreGUI<Unit>(Vec2d.ZERO) {

    val inventories: HashMap<IInventory, IconElement> = hashMapOf()

    override fun initGui() {
        mc.player.inventoryContainer.inventorySlots.forEach {
            //tlCategory()
        }
    }

    fun setupSlot(slot: Slot){
        //inventories.putIfAbsent(slot.inventory, tlCategory(IconCore.ITEMS))
    }
}