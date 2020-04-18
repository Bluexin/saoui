package com.saomc.saoui.elements.controllers

import com.saomc.saoui.api.items.IItemFilter
import com.saomc.saoui.elements.IconLabelElement

class ItemFilterController(val filter: IItemFilter, controller: IController): Controller(IconLabelElement(filter.icon, filter.displayName, controller), controller) {

    init {
        updateInventory()
    }

    fun updateInventory(){
        if (!filter.isCategory) {
            elements.clear()
            //TODO Readd
            //if (Minecraft().player != null)
                //itemList(Minecraft().player.inventoryContainer, filter)
        }
    }
}