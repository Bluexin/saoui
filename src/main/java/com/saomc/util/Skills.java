package com.saomc.util;

import com.saomc.events.EventHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;

@SideOnly(Side.CLIENT)
public enum Skills {
    SPRINTING(IconCore.SPRINTING, () -> EventHandler.IS_SPRINTING, (mc, parent) -> EventHandler.IS_SPRINTING = !EventHandler.IS_SPRINTING),
    SNEAKING(IconCore.SNEAKING, () -> EventHandler.IS_SNEAKING, (mc, parent) -> EventHandler.IS_SNEAKING = !EventHandler.IS_SNEAKING),
    CRAFTING(IconCore.CRAFTING, () -> false, (mc, parent) -> {
        if (parent != null) mc.displayGuiScreen(parent);
        else {
            mc.displayGuiScreen(null);

            final int invKeyCode = mc.gameSettings.keyBindInventory.getKeyCode();

            KeyBinding.setKeyBindState(invKeyCode, true);
            KeyBinding.onTick(invKeyCode);
        }
    });

    public final IconCore icon;
    private final BooleanSupplier shouldHighlight;
    private final BiConsumer<Minecraft, GuiInventory> action;

    Skills(IconCore iconCore, BooleanSupplier shouldHighlight, BiConsumer<Minecraft, GuiInventory> action) {
        this.icon = iconCore;
        this.shouldHighlight = shouldHighlight;
        this.action = action;
    }

    public final String toString() {
        final String name = name();

        return I18n.translateToLocal("skill" + name.charAt(0) + name.substring(1, name.length()).toLowerCase());
    }

    /**
     * Whether this skill's button should highlight or not.
     *
     * @return whether it should be highlighted
     */
    public boolean shouldHighlight() {
        return shouldHighlight.getAsBoolean();
    } // Doing it this way might come in handy when building an API

    /**
     * Activate this skill.
     *
     * @param mc     The Minecraft instance
     * @param parent The parent gui
     */
    public void activate(Minecraft mc, GuiInventory parent) {
        this.action.accept(mc, parent);
    }

}
