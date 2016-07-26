package com.saomc.screens.buttons;

import com.saomc.screens.*;
import com.saomc.screens.menu.Categories;
import com.saomc.screens.window.Window;
import com.saomc.util.IconCore;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ConfirmGUI extends Window {

    private final TextGUI textText;
    private final ContainerGUI buttonBox;
    private final ActionHandler actionHandler;

    public ConfirmGUI(ParentElement gui, int xPos, int yPos, int w, int h, String title, String text, ActionHandler handler) {
        super(gui, xPos, yPos, w, h, title);
        elements.add(textText = new TextGUI(this, 0, 0, text, width));
        elements.add(buttonBox = new ContainerGUI(this, 0, 0, width, 40));
        buttonBox.elements.add(new IconGUI(buttonBox, Categories.CONFIRM, width / 4 - 10, 10, IconCore.CONFIRM));
        buttonBox.elements.add(new IconGUI(buttonBox, Categories.CANCEL, width * 3 / 4 - 10, 10, IconCore.CANCEL));
        actionHandler = handler;
    }

    public final String getText() {
        return textText.getText();
    }

    public final void setText(String text) {
        textText.setText(text);
    }

    @Override
    protected int getSize() {
        return Math.max(super.getSize() - 20, 60);
    }

    @Override
    public void actionPerformed(Elements element, Actions action, int data) {
        if (actionHandler != null) actionHandler.actionPerformed(element, action, data);
        else super.actionPerformed(element, action, data);
    }

    public final void confirm() {
        actionPerformed(buttonBox.elements.get(0), Actions.LEFT_RELEASED, 0);
    }

    public final void cancel() {
        actionPerformed(buttonBox.elements.get(1), Actions.LEFT_RELEASED, 0);
    }

    @Override
    protected int getBoxSize(boolean bottom) {
        return bottom ? 40 : super.getBoxSize(bottom);
    }

}
