package com.saomc.commands;

import com.saomc.events.FriendsHandler;
import com.saomc.social.party.PartyHelper;
import com.saomc.util.TriConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.stream.Stream;

@SideOnly(Side.CLIENT)
public enum CommandType {

    INVITE_TO_PARTY((mc, username, args) -> PartyHelper.instance().receiveInvite(mc, username, args)),
    DISSOLVE_PARTY((mc, username, args) -> PartyHelper.instance().receiveDissolve(mc, username)),
    UPDATE_PARTY((mc, username, args) -> PartyHelper.instance().receiveUpdate(mc, username, args)),

    CONFIRM_INVITE_PARTY((mc, username, args) -> PartyHelper.instance().receiveConfirmation(mc, username, args)),
    CANCEL_INVITE_PARTY((mc, username, args) -> mc.thePlayer.addChatMessage(new TextComponentString(I18n.translateToLocalFormatted("ptDecline", username)))),

    ADD_FRIEND_REQUEST((mc, username, args) -> FriendsHandler.instance().addFriendRequest(mc, username)),

    ACCEPT_ADD_FRIEND((mc, username, args) -> FriendsHandler.instance().acceptAddFriend(username)),
    CANCEL_ADD_FRIEND((mc, username, args) -> FriendsHandler.instance().cancelAddFriend(username));

    public static final String PREFIX = "[SAOUI ";
    public static final String SUFFIX = "]";
    private final TriConsumer<Minecraft, String, String[]> action;

    CommandType(TriConsumer<Minecraft, String, String[]> action) {
        this.action = action;
    }

    static CommandType getCommand(String id) {
        return Stream.of(values()).filter(t -> id.contains(t.name())).findAny().orElse(null);
    }

    public final String toString() {
        return (PREFIX + name() + SUFFIX);
    }

    public final String key() {
        return "saouiCommand" + this.name().replace("_", "");
    }

    public void action(Minecraft mc, String username, String[] args) {
        this.action.accept(mc, username, args);
    }

}
