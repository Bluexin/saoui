package com.saomc.screens.buttons;

import com.saomc.screens.ParentElement;
import com.saomc.screens.menu.Categories;
import com.saomc.util.IconCore;
import com.saomc.util.OptionCore;

import java.util.stream.Stream;

/**
 * Part of SAOUI
 *
 * @author Bluexin
 */
public class OptionButton extends ButtonGUI {
    private final OptionCore option;

    public OptionButton(ParentElement gui, int xPos, int yPos, OptionCore option) {
        super(gui, option.isCategory ? Categories.OPT_CAT : Categories.OPTION, xPos, yPos, option.toString(), IconCore.OPTION);
        this.highlight = option.getValue();
        this.option = option;
    }

    public OptionCore getOption() {
        return this.option;
    }

    public void action() {
        if (this.option.isRestricted()) {
            if (!option.getValue()) {
                OptionCore category = option.getCategory();
                Stream.of(OptionCore.values()).filter(opt -> opt.category == category).filter(OptionCore::getValue).forEachOrdered(OptionCore::flip);
                this.highlight = this.option.flip();
            } else this.highlight = this.option.flip();
        } else this.highlight = this.option.flip();
    }
}
