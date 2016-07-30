package com.saomc.commands;

import com.saomc.events.FriendsHandler;
import com.saomc.social.party.PartyHelper;
import com.saomc.util.TriConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.stream.Stream;

@SideOnly(Side.CLIENT)
public enum CommandType {

    INVITE_TO_PARTY((mc, target, args) -> PartyHelper.instance().receiveInvite(mc, target, args)),
    DISSOLVE_PARTY((mc, target, args) -> PartyHelper.instance().receiveDissolve(mc, target)),
    UPDATE_PARTY((mc, target, args) -> PartyHelper.instance().receiveUpdate(mc, target, args)),

    CONFIRM_INVITE_PARTY((mc, target, args) -> PartyHelper.instance().receiveConfirmation(mc, target, args)),
    CANCEL_INVITE_PARTY((mc, target, args) -> mc.thePlayer.addChatMessage(new TextComponentTranslation("ptDecline", target))),

    ADD_FRIEND_REQUEST((mc, target, args) -> FriendsHandler.instance().addFriendRequest(mc, target)),

    ACCEPT_ADD_FRIEND((mc, target, args) -> FriendsHandler.instance().acceptAddFriend(target)),
    CANCEL_ADD_FRIEND((mc, target, args) -> FriendsHandler.instance().cancelAddFriend(target));

    public static final String PREFIX = "[SAOUI ";
    public static final String SUFFIX = "]";
    private final TriConsumer<Minecraft, EntityPlayer, String[]> action;

    CommandType(TriConsumer<Minecraft, EntityPlayer, String[]> action) {
        this.action = action;
    }

    static CommandType getCommand(String id) {
        return Stream.of(values()).filter(t -> id.contains(t.name())).findAny().orElse(null);
    }

    @Override
    public final String toString() {
        return (PREFIX + name() + SUFFIX);
    }

    public final String key() {
        return "saouiCommand" + this.name().replace("_", "");
    }

    public void action(Minecraft mc, EntityPlayer target, String[] args) {
        this.action.accept(mc, target, args);
    }

}
