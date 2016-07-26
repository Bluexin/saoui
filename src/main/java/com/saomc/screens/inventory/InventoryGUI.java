package com.saomc.screens.inventory;

import com.saomc.screens.Elements;
import com.saomc.screens.ListGUI;
import com.saomc.screens.ParentElement;
import com.saomc.screens.menu.EmptySlot;
import com.saomc.screens.menu.Slots;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class InventoryGUI extends ListGUI {

    public final Container slots;
    public final InventoryCore filter;

    private boolean opened;

    public InventoryGUI(ParentElement gui, int xPos, int yPos, Container containerSlots, InventoryCore inventory) {
        super(gui, xPos, yPos);
        slots = containerSlots;
        filter = inventory;
        opened = false;
    }

    @Override
    public void update(Minecraft mc) {
        if (!opened) {
            mc.thePlayer.openContainer = slots;
            opened = true;
        }

        super.update(mc);

        for (int i = 0; i < slots.inventorySlots.size(); i++) {
            final Slot slot = slots.getSlot(i);

            if (slot != null) {
                boolean state = equipped(slot.slotNumber);

                final ItemStack stack = slot.getStack();
                boolean found = false;

                for (int j = elements.size() - 1; j >= 0; j--) {
                    if (j >= elements.size()) continue;

                    if (elements.get(j) instanceof Slots) {
                        final Slots gui = (Slots) elements.get(j);

                        if (gui.getSlotNumber() == slot.slotNumber) {
                            gui.refreshSlot(slot);

                            if (!gui.removed()) {
                                if (filter.isFine(gui.getStack(), state)) found = true;
                                else gui.remove();
                            }
                        }
                    }
                }

                if (!found && stack != null && filter.isFine(stack, state)) {
                    if (state) elements.add(0, new Slots(this, 0, getOffset(elements.size()), slot));
                    else elements.add(new Slots(this, 0, getOffset(elements.size()), slot));
                }
            }
        }

        if (elements.isEmpty()) elements.add(new EmptySlot(this, 0, getOffset(elements.size())));
        else {
            final Slots slot = (Slots) elements.get(elements.size() - 1);
            if (slot.getSlotNumber() == -1) slot.remove();
        }

        slots.detectAndSendChanges();
    }

    private boolean equipped(int number) {
        final boolean state;

        if (filter.equals(InventoryCore.EQUIPMENT)) state = (number >= 5) && (number < 9);
        else state = (number >= 36) && (number < 45);

        return state;
    }

    @Override
    protected void update(Minecraft mc, int index, Elements element) {
        super.update(mc, index, element);

        if (element instanceof Slots) {
            final Slots slot = (Slots) element;

            slot.highlight = equipped(slot.getSlotNumber());
        }
    }

    public void handleMouseClick(Minecraft mc, Slot slot, int slotNumber, int flag, ClickType method) {
        if (slot != null) slotNumber = slot.slotNumber;

        mc.playerController.windowClick(slots.windowId, slotNumber, flag, method, mc.thePlayer);
    }

    @Override
    public void close(Minecraft mc) {
        super.close(mc);

        if (mc.thePlayer != null) slots.onContainerClosed(mc.thePlayer);
    }

}
