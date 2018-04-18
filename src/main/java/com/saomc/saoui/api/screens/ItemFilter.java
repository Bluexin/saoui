package com.saomc.saoui.api.screens;

import net.minecraft.item.ItemStack;

/**
 * This is used to send a specific set of items to be rendered as slots
 */
public interface ItemFilter {

    /**
     * Issues a check to see if an ItemStack matches a filter
     * Note that you will need to add your own filter when using
     * this.
     *
     * @param stack The item stack to check
     * @param equipped If the item is Equipped or not
     * @return Returns true if the filter passes
     */
    boolean invoke(ItemStack stack, boolean equipped);
}
