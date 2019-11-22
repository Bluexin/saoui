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

package com.saomc.saoui.api.items

import baubles.api.BaublesApi
import baubles.api.IBauble
import com.saomc.saoui.api.screens.IIcon
import com.saomc.saoui.util.IconCore
import com.teamwizardry.librarianlib.features.kotlin.Minecraft
import com.teamwizardry.librarianlib.features.kotlin.toolClasses
import net.minecraft.block.BlockPumpkin
import net.minecraft.client.resources.I18n
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.EntityEquipmentSlot
import net.minecraft.inventory.IInventory
import net.minecraft.inventory.Slot
import net.minecraft.item.*
import net.minecraftforge.fml.common.Loader

/**
 * TODO: use [ItemTool.getToolClasses] for moar modded compat
 */
enum class BaseFilters(val filter: (ItemStack, Boolean) -> Boolean) : IItemFilter { // Todo: support for TConstruct

    EQUIPMENT({ _, _ -> false }) {
        override val icon: IIcon
            get() = IconCore.EQUIPMENT
        override val displayName: String
            get() = I18n.format("sao.element.equipment")
        override val isCategory: Boolean
            get() = true
    },

    ARMOR({  _, _ -> false }) {
        override val icon: IIcon
            get() = IconCore.ARMOR
        override val category: IItemFilter
            get() = EQUIPMENT
        override val displayName: String
            get() = I18n.format("sao.element.armor")
        override val isCategory: Boolean
            get() = true
    },

    HELMET({ stack, _ ->
        val item = stack.item
        if (item is ItemArmor) {
            val equipSlot = item.getEquipmentSlot(stack) ?: item.armorType
            equipSlot == EntityEquipmentSlot.HEAD
        }
        else item is ItemBlock && item.block is BlockPumpkin
    }) {
        override val icon: IIcon
            get() = IconCore.ARMOR

        override val displayName: String
            get() = I18n.format("sao.element.helmet")

        override val category: IItemFilter
            get() = ARMOR

        override fun getValidSlots(): Set<Slot> {
            return IItemFilter.getPlayerSlots(Minecraft().player.inventory, 35)
        }
    },

    CHESTPLATES({ stack, _ ->
        val item = stack.item
        if (item is ItemArmor) {
            val equipSlot = item.getEquipmentSlot(stack) ?: item.armorType
            equipSlot == EntityEquipmentSlot.CHEST
        }
        else item is ItemBlock && item.block is BlockPumpkin
    }) {
        override val icon: IIcon
            get() = IconCore.ARMOR

        override val displayName: String
            get() = I18n.format("sao.element.chestplates")

        override val category: IItemFilter
            get() = ARMOR

        override fun getValidSlots(): Set<Slot> {
            return IItemFilter.getPlayerSlots(Minecraft().player.inventory, 36)
        }
    },

    LEGGINS({ stack, _ ->
        val item = stack.item
        if (item is ItemArmor) {
            val equipSlot = item.getEquipmentSlot(stack) ?: item.armorType
            equipSlot == EntityEquipmentSlot.LEGS
        }
        else item is ItemBlock && item.block is BlockPumpkin
    }) {
        override val icon: IIcon
            get() = IconCore.ARMOR

        override val displayName: String
            get() = I18n.format("sao.element.leggings")

        override val category: IItemFilter
            get() = ARMOR

        override fun getValidSlots(): Set<Slot> {
            return IItemFilter.getPlayerSlots(Minecraft().player.inventory, 37)
        }
    },

    BOOTS({ stack, _ ->
        val item = stack.item
        if (item is ItemArmor) {
            val equipSlot = item.getEquipmentSlot(stack) ?: item.armorType
            equipSlot == EntityEquipmentSlot.FEET
        }
        else item is ItemBlock && item.block is BlockPumpkin
    }) {
        override val icon: IIcon
            get() = IconCore.ARMOR

        override val displayName: String
            get() = I18n.format("sao.element.boots")

        override val category: IItemFilter
            get() = ARMOR

        override fun getValidSlots(): Set<Slot> {
            return IItemFilter.getPlayerSlots(Minecraft().player.inventory, 38)
        }
    },

    SHIELDS({ stack, _ -> stack.item.isShield(stack, null) ||
            stack.item is com.teamwizardry.librarianlib.features.base.item.IShieldItem
    }) {
        override val icon: IIcon
            get() = IconCore.ARMOR

        override val displayName: String
            get() = I18n.format("sao.element.shields")

        override val category: IItemFilter
            get() = ARMOR
    },


    WEAPONS({ _, _ -> false }) {
        override val icon: IIcon
            get() = IconCore.EQUIPMENT

        override val category: IItemFilter
            get() = EQUIPMENT

        override val displayName: String
            get() = I18n.format("sao.element.weapons")

        override val isCategory: Boolean
            get() = true
    },

    SWORDS({ stack, _ -> stack.item is ItemSword || stack.toolClasses.contains("sword")}) {
        override val icon: IIcon
            get() = IconCore.EQUIPMENT

        override val displayName: String
            get() = I18n.format("sao.element.swords")

        override val category: IItemFilter
            get() = WEAPONS
    },

    BOWS({ stack, _ -> stack.item is ItemBow || stack.item.getItemUseAction(stack) == EnumAction.BOW || stack.toolClasses.contains("bow")}) {
        override val icon: IIcon
            get() = IconCore.EQUIPMENT

        override val displayName: String
            get() = I18n.format("sao.element.bows")

        override val category: IItemFilter
            get() = WEAPONS
    },

    TOOLS({ _, _ -> false }) {
        override val icon: IIcon
            get() = IconCore.EQUIPMENT

        override val category: IItemFilter
            get() = EQUIPMENT

        override val displayName: String
            get() = I18n.format("sao.element.tools")

        override val isCategory: Boolean
            get() = true
    },

    PICKAXES({ stack, _ -> stack.item is ItemPickaxe || stack.toolClasses.contains("pickaxe")}) {
        override val icon: IIcon
            get() = IconCore.EQUIPMENT

        override val displayName: String
            get() = I18n.format("sao.element.pickaxe")

        override val category: IItemFilter
            get() = TOOLS
    },

    AXES({ stack, _ -> stack.item is ItemAxe || stack.toolClasses.contains("axe")}) {
        override val icon: IIcon
            get() = IconCore.EQUIPMENT

        override val displayName: String
            get() = I18n.format("sao.element.axe")

        override val category: IItemFilter
            get() = TOOLS
    },

    SHOVELS({ stack, _ -> stack.item is ItemSpade}) {
        override val icon: IIcon
            get() = IconCore.EQUIPMENT

        override val displayName: String
            get() = I18n.format("sao.element.shovel")

        override val category: IItemFilter
            get() = TOOLS
    },

    COMPATTOOLS({ stack, _ ->
        val item = stack.item
        values().filter { it.category == TOOLS && it != COMPATTOOLS }.none { it.invoke(stack) } &&
                (item is ItemTool || item is ItemHoe || item is ItemShears)
    }) {
        override val icon: IIcon
            get() = IconCore.EQUIPMENT

        override val displayName: String
            get() = I18n.format("sao.element.compattools")

        override val category: IItemFilter
            get() = TOOLS
    },

    ACCESSORY({ stack, _ -> baublesLoaded && stack.item is IBauble }) {
        override val icon: IIcon
            get() = IconCore.ACCESSORY

        override val displayName: String
            get() = I18n.format("sao.element.accessory")

        override val category: IItemFilter
            get() = EQUIPMENT
    },


    ITEMS({ _, _ -> false }) {
        override val displayName: String
            get() = I18n.format("sao.element.items")
        override val isCategory: Boolean
            get() = true
    },

    CONSUMABLES({ stack, _ ->
        val action = stack.itemUseAction
        action == EnumAction.DRINK || action == EnumAction.EAT }) {
        override val displayName: String
            get() = I18n.format("sao.element.consumables")

        override val category: IItemFilter
            get() = ITEMS
    },

    BLOCKS({ stack, _ -> stack.item is ItemBlock}) {
        override val displayName: String
            get() = I18n.format("sao.element.blocks")

        override val category: IItemFilter
            get() = ITEMS
    },

    /**
     * Default fallback filter
     */
    MATERIALS({ stack, _ -> ItemFilterRegister.findFilter(stack) == MATERIALS}) {
        override val displayName: String
            get() = I18n.format("sao.element.materials")

        override val category: IItemFilter
            get() = ITEMS
    };

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