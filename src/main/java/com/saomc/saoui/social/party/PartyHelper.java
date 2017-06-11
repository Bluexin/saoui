package com.saomc.saoui.social.party;

import be.bluexin.saouintw.communication.CommandType;
import be.bluexin.saouintw.communication.Communicator;
import com.saomc.saoui.api.social.party.IParty;
import com.saomc.saoui.screens.menu.Categories;
import com.saomc.saoui.social.StaticPlayerHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentTranslation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Part of SAOUI
 *
 * @author Bluexin
 */
public class PartyHelper {
    private static PartyHelper instance;
    private final IParty party;
    private List<EntityPlayer> invited = new ArrayList<>();

    private PartyHelper(IParty party) {
        this.party = party;
    }

    public static void init(IParty party) {
        instance = new PartyHelper(party);
    }

    public static PartyHelper instance() {
        if (instance != null) return instance;
        else throw new IllegalStateException("PartyHelper isn't initialized!");
    }

    public void receiveInvite(EntityPlayer target, String... args) {
        if (party.isParty()) {
            final Minecraft mc = Minecraft.getMinecraft();
            final GuiScreen keepScreen = mc.currentScreen;
            final boolean ingameFocus = mc.inGameHasFocus;

            final String text = I18n.format("party.invitation.text", target.getDisplayNameString());
/*
            mc.displayGuiScreen(WindowView.viewConfirm(ConfigHandler._PARTY_INVITATION_TITLE, text, (element, action, data) -> {
                final Categories id = element.ID();

                if (id == Categories.CONFIRM) {
                    if (args.length > 0) {
                        for (String arg : args) {
                            party.addMember(StaticPlayerHelper.findOnlinePlayer(mc, arg));
                            party.addMember(mc.thePlayer);
                        }
                    } else party.dissolve(); // TODO: check when this happens... This shouldn't ever happen!
                    mc.thePlayer.addChatMessage(new TextComponentTranslation("ptJoin", target.getDisplayName())); // Might change that later

                    Communicator.send(CommandType.CONFIRM_INVITE_PARTY, target);
                } else Communicator.send(CommandType.CANCEL_INVITE_PARTY, target);

                mc.displayGuiScreen(keepScreen);

                if (ingameFocus) mc.setIngameFocus();
                else mc.setIngameNotInFocus();
            }));

            if (ingameFocus) mc.setIngameNotInFocus();*/
        }
    }

    public List<EntityPlayer> listMembers() {
        return party.getMembers();
    }

    public boolean isMember(String username) {
        return username.equals(StaticPlayerHelper.INSTANCE.getName(Minecraft.getMinecraft())) || hasParty() && party.getMembers().stream().anyMatch(player -> player.getName().equals(username));
    }

    private boolean isLeader(String username) {
        return username.equals(party.getLeader().getDisplayNameString()); // TODO: check with text formatters, not sure how this would work out
    }

    private boolean isLeader(EntityPlayer player) {
        return player.equals(party.getLeader());
    }

    private void addPlayer(EntityPlayer player) {
        if (this.party.addMember(player)) {
            final Minecraft mc = Minecraft.getMinecraft();
            mc.player.sendMessage(new TextComponentTranslation("ptJoin", player.getDisplayName()));
            if (this.party.getLeader().equals(mc.player)) {
                party.getMembers().stream().filter(pl -> !pl.equals(mc.player)).forEach(member -> Communicator.INSTANCE.send(CommandType.UPDATE_PARTY, member, '+' + player.getDisplayNameString()));
                party.getMembers().stream().filter(pl -> !pl.equals(mc.player)).forEach(member -> Communicator.INSTANCE.send(CommandType.UPDATE_PARTY, player, '+' + member.getDisplayNameString()));
            }
        }
    }

    private void removePlayer(EntityPlayer player) { // TODO: kick member
        if (this.party.removeMember(player)) {
            final Minecraft mc = Minecraft.getMinecraft();
            mc.player.sendMessage(new TextComponentTranslation("ptLeft", player.getDisplayName()));
            if (this.party.getLeader().equals(mc.player))
                party.getMembers().stream().filter(pl -> pl.equals(mc.player)).forEach(member -> Communicator.INSTANCE.send(CommandType.UPDATE_PARTY, member, '-' + player.getDisplayNameString()));
        }
    }

    public void receiveUpdate(EntityPlayer player, String[] args) {
        if (isLeader(player)) {
            final Minecraft mc = Minecraft.getMinecraft();
            for (String a : args) {
                if (a.charAt(0) == '+') addPlayer(StaticPlayerHelper.INSTANCE.findOnlinePlayer(mc, a.substring(1)));
                else if (a.charAt(0) == '-') removePlayer(StaticPlayerHelper.INSTANCE.findOnlinePlayer(mc, a.substring(1)));
            }
        }
    }

    public void invite(EntityPlayer player) {
        if (!party.isInParty(player)) {
            invited.add(player);
            final Minecraft mc = Minecraft.getMinecraft();
            Communicator.INSTANCE.send(CommandType.INVITE_TO_PARTY, player, hasParty() ? party.getLeader().getDisplayNameString() : StaticPlayerHelper.INSTANCE.getName(mc));
        }
    }

    public void sendDissolve(Minecraft mc) {
        if (hasParty()) {
            if (party.getLeader().equals(mc.player)) {
                party.getMembers().stream().filter(pl -> pl.equals(mc.player)).forEach(member -> Communicator.INSTANCE.send(CommandType.DISSOLVE_PARTY, member));
                mc.player.sendMessage(new TextComponentTranslation("ptDissolve"));
            } else {
                Communicator.INSTANCE.send(CommandType.DISSOLVE_PARTY, party.getLeader()); // aka leave PT
                mc.player.sendMessage(new TextComponentTranslation("ptLeave"));
            }
        }
    }

    public void receiveDissolve(EntityPlayer player) {
        final Minecraft mc = Minecraft.getMinecraft();
        if (party.getLeader().equals(mc.player)) removePlayer(player);
        else if (isLeader(player)) {/*
            final Window window = SAOCore.getWindow(mc);

            if (window != null && window.getTitle().equals(ConfigHandler._PARTY_INVITATION_TITLE) && window instanceof ConfirmGUI)
                ((ConfirmGUI) window).cancel();

            mc.thePlayer.addChatMessage(new TextComponentTranslation("ptLeave"));*/
        }
    }

    public void receiveConfirmation(EntityPlayer player, String... args) { // Keeping args for later (will be needed for auth/PT UUID system)
        final Minecraft mc = Minecraft.getMinecraft();
        if (party.getLeader().equals(mc.player) && !party.isInParty(player) && invited.contains(player)) {
            addPlayer(player);
            invited.remove(player);
        } else Communicator.INSTANCE.send(CommandType.DISSOLVE_PARTY, player);
    }

    public boolean hasParty() {
        return party.isParty();
    }

    public boolean shouldHighlight(Categories id) {
        return id.equals(Categories.DISSOLVE) ? hasParty() : id.equals(Categories.INVITE_LIST) && (!hasParty() || isLeader(StaticPlayerHelper.INSTANCE.getName(Minecraft.getMinecraft())));
    }

    public void clean() {
        this.party.dissolve();
    }
}
