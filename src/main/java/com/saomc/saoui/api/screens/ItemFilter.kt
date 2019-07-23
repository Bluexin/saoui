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

package com.saomc.saoui.api.screens

import net.minecraft.item.ItemStack

/**
 * This is used to send a specific set of items to be rendered as slots
 */
@FunctionalInterface
interface ItemFilter : (ItemStack, Boolean) -> Boolean, (ItemStack) -> Boolean {

    /**
     * Issues a check to see if an ItemStack matches a filter
     * Note that you will need to add your own filter when using
     * this.
     *
     * @param stack The item stack to check
     * @param equipped If the item is Equipped or not
     * @return Returns true if the filter passes
     */
    override operator fun invoke(stack: ItemStack, equipped: Boolean): Boolean

    override fun invoke(p1: ItemStack) = this(p1, false)


    companion object {
        @Suppress("FunctionName")
        fun ItemFilter(filter: (ItemStack, Boolean) -> Boolean) = object : ItemFilter {
            override fun invoke(stack: ItemStack, equipped: Boolean) = filter(stack, equipped)
        }

        @Suppress("FunctionName")
        fun ItemFilter(filter: (ItemStack) -> Boolean) = object : ItemFilter {
            override fun invoke(stack: ItemStack, equipped: Boolean) = filter(stack)
        }
    }
}