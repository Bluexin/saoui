package com.saomc.saoui.screens.inventory

import baubles.api.BaublesApi
import baubles.api.IBauble
import com.saomc.saoui.api.screens.ItemFilter
import com.saomc.saoui.util.isNotEmpty
import cpw.mods.fml.common.Loader
import net.minecraft.block.BlockPumpkin
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.IInventory
import net.minecraft.item.*

/**
 * TODO: use [ItemTool.getToolClasses] for moar modded compat
 */
enum class BaseFilters(val filter: (ItemStack, Boolean) -> Boolean) : ItemFilter { // Todo: support for TConstruct

    EQUIPMENT({ stack, _ ->
        val item = stack.item
        item is ItemArmor || item is ItemBlock && item.blockInstance is BlockPumpkin
    }),

    SWORDS({ stack, _ -> stack.item is ItemSword }),

    // Use EnumAction.BOW here?
    BOWS({ stack, _ -> stack.item is ItemBow }),

    WEAPONS({ stack, equipped -> SWORDS(stack, equipped) || BOWS(stack, equipped) }),

    PICKAXES({ stack, _ -> stack.item is ItemPickaxe }),

    AXES({ stack, _ -> stack.item is ItemAxe }),

    SHOVELS({ stack, _ -> stack.item is ItemSpade }),

    COMPATTOOLS({ stack, _ ->
        val item = stack.item
        item is ItemTool || item is ItemHoe || item is ItemShears
    }),

    ACCESSORY({ stack, _ -> baublesLoaded && stack.item is IBauble }),

    CONSUMABLES({ stack, _ ->
        val action = stack.itemUseAction
        action == EnumAction.drink || action == EnumAction.eat
    }),

    ITEMS({ stack, equipped ->
        stack.isNotEmpty &&
                !EQUIPMENT(stack, equipped) && !SWORDS(stack, equipped) && !BOWS(stack, equipped) && !WEAPONS(stack, equipped) &&
                !ACCESSORY(stack, equipped) && !PICKAXES(stack, equipped) && !AXES(stack, equipped) && !SHOVELS(stack, equipped) &&
                !COMPATTOOLS(stack, equipped) && !ACCESSORY(stack, equipped)
    });

    override fun invoke(stack: ItemStack, equipped: Boolean) = filter(stack, equipped)

    companion object {
        val baublesLoaded by lazy { Loader.isModLoaded("baubles") }

        fun getBaubles(player: EntityPlayer): IInventory? {
            return if (!baublesLoaded) {
                null
            } else {
                BaublesApi.getBaubles(player)
            }
        }
    }
}