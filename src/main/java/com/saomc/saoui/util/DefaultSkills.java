package com.saomc.saoui.util;

import com.saomc.saoui.api.entity.ISkill;
import com.saomc.saoui.api.screens.Actions;
import com.saomc.saoui.api.screens.IIcon;
import com.saomc.saoui.events.EventHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;

import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;

@SideOnly(Side.CLIENT)
public enum DefaultSkills implements ISkill {
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

    private final IIcon icon;
    private final BooleanSupplier shouldHighlight;
    private final BiConsumer<Minecraft, GuiInventory> action;
    private boolean showOnRing = true;

    DefaultSkills(IIcon icon, BooleanSupplier shouldHighlight, BiConsumer<Minecraft, GuiInventory> action) {
        this.icon = icon;
        this.shouldHighlight = shouldHighlight;
        this.action = action;
    }

    @Override
    public final String toString() {
        final String name = name();

        return I18n.format("skill" + name.charAt(0) + name.substring(1, name.length()).toLowerCase());
    }

    @Override
    public boolean shouldHighlight() {
        return shouldHighlight.getAsBoolean();
    }

    @Override
    public boolean shouldShowInRing() {
        return showOnRing;
    }

    @Override
    public void activate(Minecraft mc, GuiInventory parent, Actions action) {
        this.action.accept(mc, parent);
    }

    @Override
    public IIcon getIcon() {
        return icon;
    }

    @Override
    public void setShowOnRing(boolean showOnRing) {
        this.showOnRing = showOnRing;
    }

}
