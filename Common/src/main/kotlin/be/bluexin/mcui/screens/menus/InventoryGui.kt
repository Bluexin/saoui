package be.bluexin.mcui.screens.menus

import org.joml.Vector2d
import be.bluexin.mcui.api.elements.IconElement
import be.bluexin.mcui.screens.CoreGUI
import net.minecraft.inventory.IInventory
import net.minecraft.inventory.Slot

class InventoryGui : CoreGUI<Unit>(Vector2d.ZERO) {

    val inventories: HashMap<IInventory, IconElement> = hashMapOf()

    override fun initGui() {
        mc.player.inventoryContainer.inventorySlots.forEach {
            // tlCategory()
        }
    }

    fun setupSlot(slot: Slot) {
        // inventories.putIfAbsent(slot.inventory, tlCategory(IconCore.ITEMS))
    }
}
