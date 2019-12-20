package com.saomc.saoui.api.elements

import com.saomc.saoui.api.items.IItemFilter
import com.saomc.saoui.screens.util.itemList
import com.teamwizardry.librarianlib.features.kotlin.Minecraft
import com.teamwizardry.librarianlib.features.math.Vec2d

class ItemFilterElement(val filter: IItemFilter): IconLabelElement(filter.icon, filter.displayName, Vec2d.ZERO) {

    fun updateInventory(){
        if (!filter.isCategory) {
            elements.clear()
            if (Minecraft().player != null)
                itemList(Minecraft().player.inventoryContainer, filter)
        }
    }

}