package com.saomc.social.party;

import com.saomc.api.screens.IIcon;
import com.saomc.screens.ParentElement;
import com.saomc.screens.buttons.ButtonState;
import com.saomc.screens.buttons.StateHandler;
import com.saomc.screens.menu.Categories;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PartyHandler extends ButtonState {

    private PartyHandler(ParentElement gui, Categories saoID, int xPos, int yPos, int w, int h, String string, IIcon icon) {
        super(gui, saoID, xPos, yPos, w, h, string, icon, new SAOPartyStateHandler(saoID));
    }

    public PartyHandler(ParentElement gui, Categories saoID, int xPos, int yPos, int w, String string, IIcon icon) {
        this(gui, saoID, xPos, yPos, w, 20, string, icon);
    }

    public PartyHandler(ParentElement gui, Categories saoID, int xPos, int yPos, String string, IIcon icon) {
        this(gui, saoID, xPos, yPos, 100, string, icon);
    }

    private static final class SAOPartyStateHandler implements StateHandler {

        private final Categories id;

        private SAOPartyStateHandler(Categories id) {

            this.id = id;
        }

        @Override
        public boolean isStateEnabled(Minecraft mc, ButtonState button) {
            return PartyHelper.instance().shouldHighlight(id);
        }

    }

}
