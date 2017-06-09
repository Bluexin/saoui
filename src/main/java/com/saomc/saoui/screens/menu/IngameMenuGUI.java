package com.saomc.saoui.screens.menu;

import com.saomc.saoui.SoundCore;
import com.saomc.saoui.api.screens.GuiSelection;
import com.saomc.saoui.elements.ElementBuilder;
import com.saomc.saoui.screens.window.ScreenGUI;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class IngameMenuGUI extends ScreenGUI {

    private int flowY;
    private int flowX, jumpX;
    private boolean playedSound;

    public IngameMenuGUI(GuiInventory vanillaGUI) {
        super(GuiSelection.IngameMenuGUI);
    }

    @Override
    protected void init() {
        super.init();

        flowY = -height;
    }

    @Override
    public void drawScreen(int cursorX, int cursorY, float partialTicks) {
        if (!playedSound) {
            SoundCore.play(mc, SoundCore.ORB_DROPDOWN);
            playedSound = true;
        }

        super.drawScreen(cursorX, cursorY, partialTicks);
    }

    @Override
    public int getX(boolean relative) {
        return super.getX(relative) + width * 2 / 5 + (flowX - jumpX) / 2;
    }

    @Override
    public int getY(boolean relative) {
        return super.getY(relative) + flowY;
    }

    @Override
    public void updateScreen() {
        super.updateScreen();

        if (flowY < height / 2) flowY = (flowY + height / 2 - 32) / 2;

        flowX /= 2;
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        SoundCore.play(mc, SoundCore.DIALOG_CLOSE);
        ElementBuilder.Companion.getInstance().resetElements(GuiSelection.IngameMenuGUI);
        ElementBuilder.Companion.getInstance().cleanItemFilters();
    }

/*
    private void moveX(final int mode, final MenuGUI menu) {
        final int value = menu.x > 0 ? menu.x + menu.width : menu.x;

        jumpX += mode * value;
        flowX += mode * value;
    }

    private void openMenu(final Categories id, final MenuGUI menu) {
        moveX(+1, menu);

        menus.add(new Entry<Categories, MenuGUI>() {

            @Override
            public MenuGUI setValue(MenuGUI none) {
                return null;
            }

            @Override
            public MenuGUI getValue() {
                return menu;
            }

            @Override
            public Categories getKey() {
                return id;
            }

        });

        elements.add(menu);
    }*/

}
