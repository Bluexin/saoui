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

    public void receiveInvite(Minecraft mc, EntityPlayer target, String... args) {
        if (party.isParty()) {
            final GuiScreen keepScreen = mc.currentScreen;
            final boolean ingameFocus = mc.inGameHasFocus;

            final String text = I18n.format(ConfigHandler._PARTY_INVITATION_TEXT, target.getDisplayNameString());

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

                    new Command(CommandType.CONFIRM_INVITE_PARTY, target).send(mc);
                } else new Command(CommandType.CANCEL_INVITE_PARTY, target).send(mc);

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

    private void addPlayer(Minecraft mc, EntityPlayer player) {
        if (this.party.addMember(player)) {
            mc.thePlayer.addChatMessage(new TextComponentTranslation("ptJoin", player.getDisplayName()));
            if (this.party.getLeader().equals(mc.thePlayer)) {
                party.getMembers().stream().filter(pl -> !pl.equals(mc.thePlayer)).forEach(member -> new Command(CommandType.UPDATE_PARTY, member, '+' + player.getDisplayNameString()).send(mc));
                party.getMembers().stream().filter(pl -> !pl.equals(mc.thePlayer)).forEach(member -> new Command(CommandType.UPDATE_PARTY, player, '+' + member.getDisplayNameString()).send(mc));
            }
        }
    }

    private void removePlayer(Minecraft mc, EntityPlayer player) { // TODO: kick member
        if (this.party.removeMember(player)) {
            mc.thePlayer.addChatMessage(new TextComponentTranslation("ptLeft", player.getDisplayName()));
            if (this.party.getLeader().equals(mc.thePlayer))
                party.getMembers().stream().filter(pl -> pl.equals(mc.thePlayer)).forEach(member -> new Command(CommandType.UPDATE_PARTY, member, '-' + player.getDisplayNameString()).send(mc));
        }
    }

    public void receiveUpdate(Minecraft mc, EntityPlayer player, String[] args) {
        if (isLeader(player)) {
            for (String a : args) {
                if (a.charAt(0) == '+') addPlayer(mc, StaticPlayerHelper.findOnlinePlayer(mc, a.substring(1)));
                else if (a.charAt(0) == '-') removePlayer(mc, StaticPlayerHelper.findOnlinePlayer(mc, a.substring(1)));
            }
        }
    }

    public void invite(Minecraft mc, EntityPlayer player) {
        if (!party.isInParty(player)) {
            invited.add(player);
            new Command(CommandType.INVITE_TO_PARTY, player, hasParty() ? party.getLeader().getDisplayNameString() : StaticPlayerHelper.getName(mc)).send(mc);
        }
    }

    public void sendDissolve(Minecraft mc) {
        if (hasParty()) {
            if (party.getLeader().equals(mc.thePlayer)) {
                party.getMembers().stream().filter(pl -> pl.equals(mc.thePlayer)).forEach(member -> new Command(CommandType.DISSOLVE_PARTY, member).send(mc));
                mc.thePlayer.addChatMessage(new TextComponentTranslation("ptDissolve"));
            } else {
                new Command(CommandType.DISSOLVE_PARTY, party.getLeader()).send(mc); // aka leave PT
                mc.thePlayer.addChatMessage(new TextComponentTranslation("ptLeave"));
            }
        }
    }

    public void receiveDissolve(Minecraft mc, EntityPlayer player) {
        if (party.getLeader().equals(mc.thePlayer)) removePlayer(mc, player);
        else if (isLeader(player)) {
            final Window window = SAOCore.getWindow(mc);

            if (window != null && window.getTitle().equals(ConfigHandler._PARTY_INVITATION_TITLE) && window instanceof ConfirmGUI)
                ((ConfirmGUI) window).cancel();

            mc.thePlayer.addChatMessage(new TextComponentTranslation("ptLeave"));
        }
    }

    public void receiveConfirmation(Minecraft mc, EntityPlayer player, String... args) { // Keeping args for later (will be needed for auth/PT UUID system)
        if (party.getLeader().equals(mc.thePlayer) && !party.isInParty(player) && invited.contains(player)) {
            addPlayer(mc, player);
            invited.remove(player);
        } else new Command(CommandType.DISSOLVE_PARTY, player).send(mc);
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
