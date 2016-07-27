package com.saomc.social.party;

import com.saomc.SAOCore;
import com.saomc.api.social.party.IParty;
import com.saomc.commands.Command;
import com.saomc.commands.CommandType;
import com.saomc.events.ConfigHandler;
import com.saomc.screens.buttons.ConfirmGUI;
import com.saomc.screens.menu.Categories;
import com.saomc.screens.window.Window;
import com.saomc.screens.window.WindowView;
import com.saomc.social.StaticPlayerHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;
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
    private List<String> invited = new ArrayList<>();

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

    public void receiveInvite(Minecraft mc, String username, String... args) {
        if (party.isParty()) {
            final GuiScreen keepScreen = mc.currentScreen;
            final boolean ingameFocus = mc.inGameHasFocus;

            final String text = I18n.format(ConfigHandler._PARTY_INVITATION_TEXT, username);

            mc.displayGuiScreen(WindowView.viewConfirm(ConfigHandler._PARTY_INVITATION_TITLE, text, (element, action, data) -> {
                final Categories id = element.ID();

                if (id == Categories.CONFIRM) {
                    if (args.length > 0) {
                        for (String arg : args) {
                            party.addMember(StaticPlayerHelper.findOnlinePlayer(mc, arg));
                            party.addMember(mc.thePlayer);
                        }
                    } else party.dissolve(); // TODO: check when this happens... This shouldn't ever happen!
                    mc.thePlayer.addChatMessage(new TextComponentString(I18n.format("ptJoin", username))); // Might change that later

                    new Command(CommandType.CONFIRM_INVITE_PARTY, username).send(mc);
                } else new Command(CommandType.CANCEL_INVITE_PARTY, username).send(mc);

                mc.displayGuiScreen(keepScreen);

                if (ingameFocus) mc.setIngameFocus();
                else mc.setIngameNotInFocus();
            }));

            if (ingameFocus) mc.setIngameNotInFocus();
        }
    }

    public List<EntityPlayer> listMembers() {
        return party.getMembers();
    }

    public boolean isMember(String username) {
        return username.equals(StaticPlayerHelper.getName(Minecraft.getMinecraft())) || hasParty() && Stream.of(party).anyMatch(member -> member.equals(username));
    }

    public boolean isLeader(String username) {
        return username.equals(party.getLeader().getDisplayNameString()); // TODO: check with text formatters, not sure how this would work out
    }

    public boolean isLeader(EntityPlayer player) {
        return player.equals(party.getLeader());
    }

    private void addPlayer(Minecraft mc, String username) {
        if (this.party.addMember(StaticPlayerHelper.findOnlinePlayer(mc, username))) {
            mc.thePlayer.addChatMessage(new TextComponentTranslation("ptJoin", username));
            if (this.party.getLeader().equals(mc.thePlayer)) {
                party.getMembers().stream().filter(pl -> !pl.equals(mc.thePlayer)).forEach(member -> new Command(CommandType.UPDATE_PARTY, member.getDisplayNameString(), '+' + username).send(mc));
                party.getMembers().stream().filter(pl -> !pl.equals(mc.thePlayer)).forEach(member -> new Command(CommandType.UPDATE_PARTY, username, '+' + member.getDisplayNameString()).send(mc));
            }
        }
    }

    private void removePlayer(Minecraft mc, String username) { // TODO: kick member
        if (this.party.removeMember(StaticPlayerHelper.findOnlinePlayer(mc, username))) {
            mc.thePlayer.addChatMessage(new TextComponentTranslation("ptLeft", username));
            if (this.party.getLeader().equals(mc.thePlayer))
                party.getMembers().stream().filter(pl -> pl.equals(mc.thePlayer)).forEach(member -> new Command(CommandType.UPDATE_PARTY, member.getDisplayNameString(), '-' + username).send(mc));
        }
    }

    public void receiveUpdate(Minecraft mc, String username, String[] args) {
        if (isLeader(username)) {
            for (String a : args) {
                if (a.charAt(0) == '+') addPlayer(mc, a.substring(1));
                else if (a.charAt(0) == '-') removePlayer(mc, a.substring(1));
            }
        }
    }

    public void invite(Minecraft mc, String username) {
        if (!isMember(username)) {
            invited.add(username);
            new Command(CommandType.INVITE_TO_PARTY, username, hasParty() ? party.getLeader().getDisplayNameString() : StaticPlayerHelper.getName(mc)).send(mc);
        }
    }

    public void sendDissolve(Minecraft mc) {
        if (hasParty()) {
            if (party.getLeader().equals(mc.thePlayer)) {
                party.getMembers().stream().filter(pl -> pl.equals(mc.thePlayer)).forEach(member -> new Command(CommandType.DISSOLVE_PARTY, member.getDisplayNameString()).send(mc));
                mc.thePlayer.addChatMessage(new TextComponentTranslation("ptDissolve"));
            } else {
                new Command(CommandType.DISSOLVE_PARTY, party.getLeader().getDisplayNameString()).send(mc); // aka leave PT
                mc.thePlayer.addChatMessage(new TextComponentTranslation("ptLeave"));
            }
        }
    }

    public void receiveDissolve(Minecraft mc, String username) {
        if (party.getLeader().equals(mc.thePlayer)) removePlayer(mc, username);
        else if (isLeader(username)) {
            final Window window = SAOCore.getWindow(mc);

            if (window != null && window.getTitle().equals(ConfigHandler._PARTY_INVITATION_TITLE) && window instanceof ConfirmGUI)
                ((ConfirmGUI) window).cancel();

            mc.thePlayer.addChatMessage(new TextComponentTranslation("ptLeave"));
        }
    }

    public void receiveConfirmation(Minecraft mc, String username, String... args) { // Keeping args for later (will be needed for auth/PT UUID system)
        if (party.getLeader().equals(mc.thePlayer) && !isMember(username) && invited.contains(username)) {
            addPlayer(mc, username);
            invited.remove(username);
        } else new Command(CommandType.DISSOLVE_PARTY, username).send(mc);
    }

    public boolean hasParty() {
        return party.isParty();
    }

    public boolean shouldHighlight(Categories id) {
        return id.equals(Categories.DISSOLVE) ? hasParty() : id.equals(Categories.INVITE_LIST) && (!hasParty() || isLeader(StaticPlayerHelper.getName(Minecraft.getMinecraft())));
    }

    public void clean() {
        this.party.dissolve();
    }
}
