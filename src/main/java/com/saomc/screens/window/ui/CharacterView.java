package com.saomc.screens.window.ui;

import com.saomc.GLCore;
import com.saomc.api.entity.ISkill;
import com.saomc.api.screens.Actions;
import com.saomc.resources.StringNames;
import com.saomc.screens.Elements;
import com.saomc.screens.ParentElement;
import com.saomc.screens.menu.Slots;
import com.saomc.util.ColorUtil;
import com.saomc.util.OptionCore;
import com.saomc.util.SkillList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Objects;

@SideOnly(Side.CLIENT)
public class CharacterView extends Elements {

    public static boolean IS_VIEWING = false;

    private final EntityPlayer character;

    private int clickIndex;

    public CharacterView(ParentElement gui, int xPos, int yPos, int w, int h, EntityPlayer player) {
        super(gui, xPos, yPos, w, h);
        character = player;
    }

    private void drawCharacter(int x, int y, int size, int cursorX, int cursorY) {
        final float mouseX = (float) x - cursorX;
        final float mouseY = (float) y - size * 1.67F - cursorY;
        EntityLivingBase tmp = (EntityLivingBase) character.getRidingEntity();

        IS_VIEWING = true;
        if (character.isRiding() && OptionCore.MOUNT_STAT_VIEW.getValue())
            GuiInventory.drawEntityOnScreen(x, y, size, mouseX, mouseY, tmp);
        else GuiInventory.drawEntityOnScreen(x, y, size, mouseX, mouseY, character);
        IS_VIEWING = false;

        GLCore.glRescaleNormal(true);
        GLCore.glTexture2D(true);
        GLCore.glBlend(true);

        GLCore.tryBlendFuncSeparate(770, 771, 1, 0);
    }

    @Override
    public void draw(Minecraft mc, int cursorX, int cursorY) {
        super.draw(mc, cursorX, cursorY);

        clickIndex = -1;

        if (visibility > 0) {
            GLCore.glBindTexture(OptionCore.SAO_UI.getValue() ? StringNames.gui : StringNames.guiCustom);
            GLCore.glColorRGBA(ColorUtil.DEFAULT_COLOR.multiplyAlpha(visibility));

            int left = getX(false) + width / 2;
            int top = getY(false) + height * 13 / 16;

            final int size = width * height / 550;

            final int shadowY = size / 2 + Math.max(Math.min((cursorY - top) / 12, 0), -size / 2 + 2);

            GLCore.glTexturedRect(left - size / 2, (top - shadowY / 2), size, shadowY, 200, 85, 56, 30);

            drawCharacter(left, top, size, cursorX, cursorY);

            left = getX(false) + width / 2;
            top = getY(false) + height / 2;

            final int width2 = (width / 2) - 14;
            final int height2 = (height / 2) - 14;

            ISkill[] skills = SkillList.instance().stream().filter(ISkill::shouldShowInRing).toArray(ISkill[]::new);

            for (int angle = 0; angle < 12; angle++) {
                final int x = (int) (left + Math.sin(Math.toRadians(angle * 30)) * width2);
                final int y = (int) (top + Math.cos(Math.toRadians(angle * 30)) * height2);

                final boolean hovered = ((cursorX >= x - 10) && (cursorY >= y - 10) && (cursorX <= x + 10) && (cursorY <= y + 10)) || (SkillList.instance().isRingShown() && angle < skills.length && skills[angle].shouldHighlight());

                GLCore.glBindTexture(OptionCore.SAO_UI.getValue() ? StringNames.gui : StringNames.guiCustom);
                GLCore.glColorRGBA((hovered ? ColorUtil.HOVER_COLOR : ColorUtil.DEFAULT_FONT_COLOR).multiplyAlpha(visibility));

                if (SkillList.instance().isRingShown()) {
                    if (angle < skills.length) {
                        GLCore.glTexturedRect(x - 10, y - 10, 169, 25, 20, 20);
                        skills[angle].getIcon().glDraw(x - 8, y - 8);
                        if (hovered) clickIndex = angle;
                    } else GLCore.glTexturedRect(x - 10, y - 10, 0, 25, 20, 20);
                } else {
                    if ((angle + 4 < 9) || (angle + 4 >= 12)) {
                        final int index = (angle + 4 >= 12 ? (angle - 8) % 9 : (angle + 4) % 9);
                        final Slot slot = character.inventoryContainer.getSlotFromInventory(character.inventory, index);

                        if (slot != null && slot.getHasStack() && slot.getStack().getItem() != null) {
                            GLCore.glTexturedRect(x - 10, y - 10, 169, 25, 20, 20);
                            GLCore.glColorRGBA((hovered ? ColorUtil.HOVER_FONT_COLOR : ColorUtil.DEFAULT_COLOR).multiplyAlpha(visibility));
                            Slots.getIcon(slot.getStack()).glDraw(x - 8, y - 8);
                        } else GLCore.glTexturedRect(x - 10, y - 10, 0, 25, 20, 20);

                        if (hovered) clickIndex = index;
                    } else GLCore.glTexturedRect(x - 10, y - 10, 0, 25, 20, 20);
                }
            }
        }
    }

    @Override
    public boolean keyTyped(Minecraft mc, char ch, int key) {
        if (Objects.equals(character, mc.thePlayer)) for (int i = 0; i < 9; i++)
            if (key == mc.gameSettings.keyBindsHotbar[i].getKeyCode()) {
                character.inventory.currentItem = i;
                return true;
            }

        return super.keyTyped(mc, ch, key);
    }

    @Override
    public boolean mousePressed(Minecraft mc, int cursorX, int cursorY, int button) {
        if (clickIndex >= 0 && mc.thePlayer.equals(character)) {
            if (SkillList.instance().isRingShown())
                SkillList.instance().hitInSkillRing(clickIndex, mc, null, Actions.getAction(button, true));
            else if (button == 0) character.inventory.currentItem = clickIndex;
            return true;
        }

        return super.mousePressed(mc, cursorX, cursorY, button);
    }

}
