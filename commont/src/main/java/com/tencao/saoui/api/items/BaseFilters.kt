/*
 * Copyright (C) 2016-2019 Arnaud 'Bluexin' Solé
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.tencao.saoui.api.items

import com.tencao.saoui.api.screens.IIcon
import com.tencao.saoui.screens.util.toIcon
import com.tencao.saoui.util.Client
import com.tencao.saoui.util.IconCore
import com.tencao.saoui.util.localize
import me.shedaniel.architectury.registry.ToolType
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.*
import net.minecraft.world.level.block.Blocks

/**
 * TODO: use [ItemTool.getToolClasses] for moar modded compat
 */
enum class BaseFilters(val filter: (ItemStack, Boolean) -> Boolean) : IItemFilter { // Todo: support for TConstruct

    EQUIPMENT({ _, _ -> false }) {
        override val icon: IIcon
            get() = IconCore.EQUIPMENT
        override val displayName: String
            get() = "sao.element.equipment".localize()
        override val isCategory: Boolean
            get() = true
    },

    ARMOR({ _, _ -> false }) {
        override val icon: IIcon
            // get() = IconCore.ARMOR
            get() = Items.IRON_HORSE_ARMOR.toIcon()
        override val category: IItemFilter
            get() = EQUIPMENT
        override val displayName: String
            get() = "sao.element.armor".localize()
        override val isCategory: Boolean
            get() = true
    },

    HELMET({ stack, _ ->
        Client.player!!.containerMenu.slots.first { it.index == 5 }.mayPlace(stack)
    }) {
        override val icon: IIcon
            get() = Items.CHAINMAIL_HELMET.toIcon()

        override val displayName: String
            get() = "sao.element.helmet".localize()

        override val category: IItemFilter
            get() = ARMOR

        override fun getValidSlots(): Set<Slot> {
            return IItemFilter.getPlayerSlots(5)
        }
    },

    CHESTPLATES({ stack, _ ->
        Client.player!!.containerMenu.slots.first { it.index == 6 }.mayPlace(stack)
    }) {
        override val icon: IIcon
            get() = Items.CHAINMAIL_CHESTPLATE.toIcon()

        override val displayName: String
            get() = "sao.element.chestplates".localize()

        override val category: IItemFilter
            get() = ARMOR

        override fun getValidSlots(): Set<Slot> {
            return IItemFilter.getPlayerSlots(6)
        }
    },

    LEGGINS({ stack, _ ->
        Client.player!!.inventoryMenu.slots.first { it.index == 7 }.mayPlace(stack)
    }) {
        override val icon: IIcon
            get() = Items.CHAINMAIL_LEGGINGS.toIcon()

        override val displayName: String
            get() = "sao.element.leggings".localize()

        override val category: IItemFilter
            get() = ARMOR

        override fun getValidSlots(): Set<Slot> {
            return IItemFilter.getPlayerSlots(7)
        }
    },

    BOOTS({ stack, _ ->
        Client.player!!.inventoryMenu.slots.first { it.index == 8 }.mayPlace(stack)
    }) {
        override val icon: IIcon
            get() = Items.CHAINMAIL_BOOTS.toIcon()

        override val displayName: String
            get() = "sao.element.boots".localize()

        override val category: IItemFilter
            get() = ARMOR

        override fun getValidSlots(): Set<Slot> {
            return IItemFilter.getPlayerSlots(8)
        }
    },


    SHIELDS({ stack, _ ->
        stack.item is ShieldItem || stack.useAnimation == UseAnim.BLOCK
    }) {
        override val icon: IIcon
            get() = Items.SHIELD.toIcon()

        override val displayName: String
            get() = "sao.element.shields".localize()

        override val category: IItemFilter
            get() = ARMOR

        override fun getValidSlots(): Set<Slot> {
            return IItemFilter.getPlayerSlots(45)
        }
    },



    WEAPONS({ _, _ -> false }) {
        override val icon: IIcon
            get() = Items.SPECTRAL_ARROW.toIcon()

        override val category: IItemFilter
            get() = EQUIPMENT

        override val displayName: String
            get() = "sao.element.weapons".localize()

        override val isCategory: Boolean
            get() = true
    },

    SWORDS({ stack, _ -> stack.item is SwordItem || (stack.item is TieredItem && (stack.item as TieredItem).tier.attackDamageBonus > 0f) }) {
        override val icon: IIcon
            get() = Items.IRON_SWORD.toIcon()

        override val displayName: String
            get() = "sao.element.swords".localize()

        override val category: IItemFilter
            get() = WEAPONS
    },

    BOWS({ stack, _ -> stack.item is BowItem || stack.item.getUseAnimation(stack) == UseAnim.BOW  }) {
        override val icon: IIcon
            get() = Items.BOW.toIcon()

        override val displayName: String
            get() = "sao.element.bows".localize()

        override val category: IItemFilter
            get() = WEAPONS
    },

    TOOLS({ _, _ -> false }) {
        override val icon: IIcon
            get() = Items.IRON_HOE.toIcon()

        override val category: IItemFilter
            get() = EQUIPMENT

        override val displayName: String
            get() = "sao.element.tools".localize()

        override val isCategory: Boolean
            get() = true
    },

    PICKAXES({ stack, _ -> stack.item is PickaxeItem || ToolType.PICKAXE.fabricTag.get().contains(stack.item) }) {
        override val icon: IIcon
            get() = Items.IRON_PICKAXE.toIcon()

        override val displayName: String
            get() = "sao.element.pickaxe".localize()

        override val category: IItemFilter
            get() = TOOLS
    },

    AXES({ stack, _ -> stack.item is AxeItem || ToolType.AXE.fabricTag.get().contains(stack.item) }) {
        override val icon: IIcon
            get() = Items.IRON_AXE.toIcon()

        override val displayName: String
            get() = "sao.element.axe".localize()

        override val category: IItemFilter
            get() = TOOLS
    },

    SHOVELS({ stack, _ -> stack.item is ShovelItem || ToolType.SHOVEL.fabricTag.get().contains(stack.item) }) {
        override val icon: IIcon
            get() = Items.IRON_SHOVEL.toIcon()

        override val displayName: String
            get() = "sao.element.shovel".localize()

        override val category: IItemFilter
            get() = TOOLS
    },

    COMPATTOOLS({ stack, _ ->
        val item = stack.item
        values().filter { it.category == TOOLS && it != COMPATTOOLS }.none { it.invoke(stack) } &&
            (item is DiggerItem || item is HoeItem || item is ShearsItem || ToolType.HOE.fabricTag.get().contains(stack.item))
    }) {
        override val icon: IIcon
            get() = Items.SHEARS.toIcon()

        override val displayName: String
            get() = "sao.element.compattools".localize()

        override val category: IItemFilter
            get() = TOOLS
    },
/*
    //TODO Replace with new api
    ACCESSORY({ stack, _ -> baublesLoaded && stack.item is IBauble }) {
        override val icon: IIcon
            get() = Items.GHAST_TEAR.toIcon()

        override val displayName: String
            get() = "sao.element.accessory".localize()

        override val category: IItemFilter
            get() = EQUIPMENT
    },*/

    ITEMS({ _, _ -> false }) {
        override val displayName: String
            get() = "sao.element.items".localize()
        override val isCategory: Boolean
            get() = true
    },

    CONSUMABLES({ stack, _ ->
        val action = stack.useAnimation
        action == UseAnim.DRINK || action == UseAnim.EAT
    }) {
        override val icon: IIcon
            get() = Items.APPLE.toIcon()

        override val displayName: String
            get() = "sao.element.consumables".localize()

        override val category: IItemFilter
            get() = ITEMS
    },

    BLOCKS({ stack, _ -> stack.item is BlockItem }) {
        override val icon: IIcon
            get() = Blocks.COBBLESTONE.toIcon()
        override val displayName: String
            get() = "sao.element.blocks".localize()

        override val category: IItemFilter
            get() = ITEMS
    },

    /**
     * Default fallback filter
     */
    MATERIALS({ stack, _ -> ItemFilterRegister.findFilter(stack) == MATERIALS }) {
        override val icon: IIcon
            get() = Items.IRON_INGOT.toIcon()

        override val displayName: String
            get() = "sao.element.materials".localize()

        override val category: IItemFilter
            get() = ITEMS
    };

    override fun invoke(stack: ItemStack, equipped: Boolean) = filter(stack, equipped)

    companion object {

    }
}
