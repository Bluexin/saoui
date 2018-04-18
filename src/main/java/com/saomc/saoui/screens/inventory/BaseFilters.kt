package com.saomc.saoui.screens.inventory

import baubles.api.IBauble
import com.saomc.saoui.api.screens.ItemFilter
import com.teamwizardry.librarianlib.features.kotlin.isNotEmpty
import net.minecraft.block.BlockPumpkin
import net.minecraft.item.*
import net.minecraftforge.fml.common.Loader

object BaseFilters { // Todo: support for TConstruct

    val EQUIPMENT = ItemFilter { stack, _ ->
        val item = stack.item
        item is ItemArmor || item is ItemBlock && item.block is BlockPumpkin
    }

    val WEAPONS = ItemFilter { stack, _ -> stack.item is ItemSword }

    // Use EnumAction.BOW here?
    val BOWS = ItemFilter { stack, _ -> stack.item is ItemBow }

    val PICKAXES = ItemFilter { stack, _ -> stack.item is ItemPickaxe }

    val AXES = ItemFilter { stack, _ -> stack.item is ItemAxe }

    val SHOVELS = ItemFilter { stack, _ -> stack.item is ItemSpade }

    val COMPATTOOLS = ItemFilter { stack, equipped ->
        val item = stack.item
        item is ItemTool || BOWS(stack, equipped) || WEAPONS(stack, equipped)
    }

    val ACCESSORY = ItemFilter { stack, _ -> baublesLoaded && stack.item is IBauble }

    val CONSUMABLES = ItemFilter { stack, _ ->
        val action = stack.itemUseAction
        action == EnumAction.DRINK || action == EnumAction.EAT
    }

    val ITEMS = ItemFilter { stack, equipped -> stack.isNotEmpty && (!equipped || !EQUIPMENT(stack, equipped)) }

    val baublesLoaded by lazy { Loader.isModLoaded("baubles") }
}
