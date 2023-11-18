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

package com.tencao.saoui.util;

/*
@SuppressWarnings("unused") // TODO: Use this class again
public enum DefaultSkills implements ISkill {
    SPRINTING(IconCore.SPRINTING, EventHandler.INSTANCE::getIS_SPRINTING, (mc, parent) -> EventHandler.INSTANCE.setIS_SPRINTING(!EventHandler.INSTANCE.getIS_SPRINTING())),
    SNEAKING(IconCore.SNEAKING, EventHandler.INSTANCE::getIS_SNEAKING, (mc, parent) -> EventHandler.INSTANCE.setIS_SNEAKING(!EventHandler.INSTANCE.getIS_SNEAKING())),
    CRAFTING(IconCore.CRAFTING, () -> false, (mc, parent) -> {
        if (parent != null) mc.setScreen(parent);
        else {
            mc.setScreen(null);

            final InputMappings.Input invKeyCode = mc.gameSettings.keyBindInventory.getKey();

            KeyBinding.setKeyBindState(invKeyCode, true);
            KeyBinding.onTick(invKeyCode);
        }
    });

    private final IIcon icon;
    private final BooleanSupplier shouldHighlight;
    private final BiConsumer<Minecraft, InventoryScreen> action;
    private boolean showOnRing = true;

    DefaultSkills(IIcon icon, BooleanSupplier shouldHighlight, BiConsumer<Minecraft, InventoryScreen> action) {
        this.icon = icon;
        this.shouldHighlight = shouldHighlight;
        this.action = action;
    }

    @Override
    public final @NotNull String toString() {
        final String name = name();

        return "skill".translate(name.charAt(0) + name.substring(1).toLowerCase());
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
    public void activate(@NotNull Minecraft mc, @NotNull InventoryScreen parent, @NotNull Actions action) {
        this.action.accept(mc, parent);
    }

    @NotNull
    @Override
    public IIcon getIcon() {
        return icon;
    }

    @Override
    public void setShowOnRing(boolean showOnRing) {
        this.showOnRing = showOnRing;
    }

    @Override
    public boolean visible() {
        return true;
    }
}*/
