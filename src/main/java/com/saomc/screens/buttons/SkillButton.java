package com.saomc.screens.buttons;

import com.saomc.api.entity.ISkill;
import com.saomc.api.screens.Actions;
import com.saomc.screens.ParentElement;
import com.saomc.screens.menu.Categories;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;

/**
 * Part of SAOUI
 *
 * @author Bluexn
 */
public class SkillButton extends ButtonGUI {
    private final ISkill skill;

    public SkillButton(ParentElement gui, int xPos, int yPos, ISkill skill) {
        super(gui, Categories.SKILL, xPos, yPos, skill.toString(), skill.getIcon(), skill.shouldHighlight());
        this.skill = skill;
    }

    public void action(Minecraft mc, GuiInventory parent, Actions action) {
        this.skill.activate(mc, parent, action);
        this.highlight = skill.shouldHighlight();
    }
}
