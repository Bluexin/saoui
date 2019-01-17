/*
 * Copyright (C) 2016-2019 Arnaud 'Bluexin' Solé
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.saomc.saoui.social.party

import be.bluexin.saomclib.party.IParty
import be.bluexin.saouintw.communication.CommandType
import be.bluexin.saouintw.communication.Communicator
import com.saomc.saoui.social.StaticPlayerHelper
import net.minecraft.client.Minecraft
import net.minecraft.client.resources.I18n
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.text.TextComponentTranslation
import java.util.*

/**
 * Part of SAOUI

 * @author Bluexin
 */
@Deprecated("Use SAOMCLib's party system instead.")
class PartyHelper private constructor(private val party: IParty) {
    private val invited = ArrayList<EntityPlayer>()

    fun receiveInvite(target: EntityPlayer, vararg args: String) {
        if (party.isParty) {
            val mc = Minecraft.getMinecraft()
            val keepScreen = mc.currentScreen
            val ingameFocus = mc.inGameHasFocus

            val text = I18n.format("party.invitation.text", target.displayNameString)
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

    fun listMembers(): Sequence<EntityPlayer> {
        return party.members
    }

    fun isMember(username: String): Boolean {
        return username == StaticPlayerHelper.getName(Minecraft.getMinecraft()) || hasParty() && party.members.any { player -> player.name == username }
    }

    private fun isLeader(username: String): Boolean {
        return username == party.leader?.displayNameString // TODO: check with text formatters, not sure how this would work out
    }

    private fun isLeader(player: EntityPlayer): Boolean {
        return player == party.leader
    }

    private fun addPlayer(player: EntityPlayer) {
        if (this.party.addMember(player)) {
            val mc = Minecraft.getMinecraft()
            mc.player.sendMessage(TextComponentTranslation("ptJoin", player.displayName))
            if (this.party.leader == mc.player) {
                party.members.filter { pl -> pl != mc.player }.forEach { member -> Communicator.send(CommandType.UPDATE_PARTY, member, '+' + player.displayNameString) }
                party.members.filter { pl -> pl != mc.player }.forEach { member -> Communicator.send(CommandType.UPDATE_PARTY, player, '+' + member.displayNameString) }
            }
        }
    }

    private fun removePlayer(player: EntityPlayer) { // TODO: kick member
        if (this.party.removeMember(player)) {
            val mc = Minecraft.getMinecraft()
            mc.player.sendMessage(TextComponentTranslation("ptLeft", player.displayName))
            if (this.party.leader == mc.player)
                party.members.filter { pl -> pl == mc.player }.forEach { member -> Communicator.send(CommandType.UPDATE_PARTY, member, '-' + player.displayNameString) }
        }
    }

    fun receiveUpdate(player: EntityPlayer, args: Array<String>) {
        if (isLeader(player)) {
            val mc = Minecraft.getMinecraft()
            for (a in args) {
                if (a[0] == '+')
                    addPlayer(StaticPlayerHelper.findOnlinePlayer(mc, a.substring(1))!!)
                else if (a[0] == '-') removePlayer(StaticPlayerHelper.findOnlinePlayer(mc, a.substring(1))!!)
            }
        }
    }

    fun invite(player: EntityPlayer) {
        if (!party.isMember(player)) {
            invited.add(player)
            val mc = Minecraft.getMinecraft()
            Communicator.send(CommandType.INVITE_TO_PARTY, player, if (hasParty()) party.leader!!.displayNameString else StaticPlayerHelper.getName(mc))
        }
    }

    fun sendDissolve(mc: Minecraft) {
        if (hasParty()) {
            if (party.leader == mc.player) {
                party.members.filter { pl -> pl == mc.player }.forEach { member -> Communicator.send(CommandType.DISSOLVE_PARTY, member) }
                mc.player.sendMessage(TextComponentTranslation("ptDissolve"))
            } else {
                Communicator.send(CommandType.DISSOLVE_PARTY, party.leader!!) // aka leave PT
                mc.player.sendMessage(TextComponentTranslation("ptLeave"))
            }
        }
    }

    fun receiveDissolve(player: EntityPlayer) {
        val mc = Minecraft.getMinecraft()
        if (party.leader == mc.player)
            removePlayer(player)
        else if (isLeader(player)) {/*
            final Window window = SAOCore.getWindow(mc);

            if (window != null && window.getTitle().equals(ConfigHandler._PARTY_INVITATION_TITLE) && window instanceof ConfirmGUI)
                ((ConfirmGUI) window).cancel();

            mc.thePlayer.addChatMessage(new TextComponentTranslation("ptLeave"));*/
        }
    }

    fun receiveConfirmation(player: EntityPlayer, vararg args: String) { // Keeping args for later (will be needed for auth/PT UUID system)
        val mc = Minecraft.getMinecraft()
        if (party.leader == mc.player && !party.isMember(player) && invited.contains(player)) {
            addPlayer(player)
            invited.remove(player)
        } else
            Communicator.send(CommandType.DISSOLVE_PARTY, player)
    }

    fun hasParty(): Boolean {
        return party.isParty
    }

    fun clean() {
        this.party.dissolve()
    }

    companion object {
        private var instance: PartyHelper? = null

        fun init(party: IParty): PartyHelper {
            instance = PartyHelper(party)
            return instance!!
        }

        fun instance(): PartyHelper {
            return if (instance != null)
                instance!!
            else
                throw IllegalStateException("PartyHelper isn't initialized!")
        }
    }
}
