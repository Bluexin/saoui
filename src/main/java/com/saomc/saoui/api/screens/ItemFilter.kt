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