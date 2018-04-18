package com.saomc.saoui.screens.inventory;

import baubles.api.BaublesApi;
import baubles.api.IBauble;
import com.saomc.saoui.api.screens.ItemFilter;
import net.minecraft.block.BlockPumpkin;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.*;
import net.minecraftforge.fml.common.Loader;

public enum InventoryCore implements ItemFilter { // Todo: support for TConstruct

    EQUIPMENT((stack, state) -> {
        final Item item = stack.getItem();

        return (item instanceof ItemArmor) || ((item instanceof ItemBlock) && (((ItemBlock) item).getBlock() instanceof BlockPumpkin));
    }),

    WEAPONS((stack, state) -> stack.getItem() instanceof ItemSword),

    BOWS((stack, state) -> stack.getItem() instanceof ItemBow),

    PICKAXE((stack, state) -> stack.getItem() instanceof ItemPickaxe),

    AXE((stack, state) -> stack.getItem() instanceof ItemAxe),

    SHOVEL((stack, state) -> stack.getItem() instanceof ItemSpade),

    COMPATTOOLS((stack, state) -> {
        final Item item = stack.getItem();

        return ((item instanceof ItemTool) || (item instanceof ItemBow) || (item instanceof ItemSword));
    }),

    ACCESSORY((stack, state) -> isBaublesLoaded() && stack.getItem() instanceof IBauble),

    CONSUMABLES((stack, state) -> {
        final Item item = stack.getItem();

        return (
                (item instanceof ItemExpBottle) ||
                        (item instanceof ItemPotion) ||
                        (item instanceof ItemFood)
        );
    }),

    ITEMS((stack, state) -> !state || (!EQUIPMENT.invoke(stack, state)));

    private final ItemFilter itemFilter;

    InventoryCore(ItemFilter filter) {
        itemFilter = filter;
    }

    public static IInventory getBaubles(EntityPlayer player) {
        if (!isBaublesLoaded()) {
            return null;
        } else {
            return BaublesApi.getBaubles(player);
        }
    }

    public static boolean isBaublesLoaded() {
        return Loader.isModLoaded("baubles");
    }

    @Override
    public final boolean invoke(ItemStack stack, boolean equipped) {
        return itemFilter.filter(stack, equipped);
    }

    @FunctionalInterface
    private interface ItemFilter {
        boolean filter(ItemStack stack, boolean state);
    }

}
