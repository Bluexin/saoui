package com.saomc.saoui.screens.menus

import com.saomc.saoui.api.elements.IconElement
import com.saomc.saoui.screens.CoreGUI
import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.math.Vec2d
import net.minecraft.client.Minecraft
import net.minecraft.inventory.IInventory

/*
class InventoryGui : CoreGUI<Unit>(Vec2d.ZERO) {

    val inventories: HashMap<IInventory, IconElement> = hashMapOf()

    override fun init(minecraft: Minecraft, width: Int, height: Int) {
        super.init(minecraft, width, height)
        Client.player.inventoryContainer.inventorySlots.forEach {
            //tlCategory()
        }
    }

    override fun initGui() {
    }

    fun setupSlot(slot: Slot){
        //inventories.putIfAbsent(slot.inventory, tlCategory(IconCore.ITEMS))
    }

}*/