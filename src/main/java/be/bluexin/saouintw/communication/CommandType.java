package be.bluexin.saouintw.communication;

import com.saomc.saoui.social.StaticPlayerHelper;
import com.saomc.saoui.social.friends.FriendsHandler;
import com.saomc.saoui.social.party.PartyHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentTranslation;

import java.util.function.BiConsumer;
import java.util.stream.Stream;

public enum CommandType {

    INVITE_TO_PARTY((target, args) -> PartyHelper.instance().receiveInvite(target, args)),
    DISSOLVE_PARTY((target, args) -> PartyHelper.instance().receiveDissolve(target)),
    UPDATE_PARTY((target, args) -> PartyHelper.instance().receiveUpdate(target, args)),

    CONFIRM_INVITE_PARTY((target, args) -> PartyHelper.instance().receiveConfirmation(target, args)),
    CANCEL_INVITE_PARTY((target, args) -> StaticPlayerHelper.thePlayer().addChatComponentMessage(new ChatComponentTranslation("ptDecline", target.getDisplayName()))),

    ADD_FRIEND_REQUEST((target, args) -> FriendsHandler.instance().addFriendRequest(target)),

    ACCEPT_ADD_FRIEND((target, args) -> FriendsHandler.instance().acceptAddFriend(target)),
    CANCEL_ADD_FRIEND((target, args) -> FriendsHandler.instance().cancelAddFriend(target));

    public static final String PREFIX = "[SAOUI ";
    public static final String SUFFIX = "]";
    private final BiConsumer<EntityPlayer, String[]> action;

    CommandType(BiConsumer<EntityPlayer, String[]> action) {
        this.action = action;
    }

    public static CommandType getCommand(String id) {
        return Stream.of(values()).filter(t -> id.contains(t.name())).findAny().orElse(null);
    }

    @Override
    public final String toString() {
        return (PREFIX + name() + SUFFIX);
    }

    public final String key() {
        return "saouiCommand" + this.name().replace("_", "");
    }

    @SideOnly(Side.CLIENT)
    public void action(EntityPlayer target, String[] args) {
        this.action.accept(target, args);
    }

}
