package be.bluexin.mcui.screens.menus

import com.tencao.saomclib.utils.math.Vec2d
import be.bluexin.mcui.api.elements.IconElement
import be.bluexin.mcui.screens.CoreGUI
import net.minecraft.inventory.IInventory
import net.minecraft.inventory.Slot

class InventoryGui : CoreGUI<Unit>(Vec2d.ZERO) {

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
