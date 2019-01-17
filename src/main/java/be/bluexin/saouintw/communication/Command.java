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

package be.bluexin.saouintw.communication;

import com.saomc.saoui.config.OptionCore;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;

import java.util.Arrays;
import java.util.MissingFormatArgumentException;

/**
 * Part of the SAOUI project.
 *
 * @author Bluexin
 */
public class Command {
    private final CommandType type;
    private final EntityPlayer from;
    private final EntityPlayer to;
    private final String[] args;

    private Command(String raw) {
        if (!raw.contains("$")) throw new MissingFormatArgumentException("<username> not found in \"" + raw + '"');
        if (!raw.contains(CommandType.PREFIX) || !raw.contains(CommandType.SUFFIX))
            throw new MissingFormatArgumentException("invalid command: \"" + raw + '"');
        this.from = Minecraft.getMinecraft().world.getPlayerEntityByName(raw.substring(raw.indexOf('$') + 1, raw.lastIndexOf('$')));
        this.type = CommandType.getCommand(raw);
        this.to = Minecraft.getMinecraft().player;
        this.args = getContent(raw);
    }

    Command(CommandType type, EntityPlayer to, String... args) {
        this.type = type;
        this.to = to;
        this.from = Minecraft.getMinecraft().player;
        this.args = args;
    }

    public static boolean processCommand(String raw) {
        if (Minecraft.getMinecraft().player == null || !OptionCore.CLIENT_CHAT_PACKETS.isEnabled()) return false;
        if (raw.contains(CommandType.PREFIX) && raw.contains(CommandType.SUFFIX)) {
            final Command command;
            try {
                command = new Command(raw);
            } catch (MissingFormatArgumentException e) {
                return false;
            }
            if (command.type != null) {
                if (!command.from.equals(Minecraft.getMinecraft().player)) command.activate();
                return true;
            }
        }
        return false;
    }

    private static String[] getContent(String data) {
        return !data.contains("{[") || !data.contains("]}") ? null : data.substring(data.indexOf("{[") + 2, data.indexOf("]}")).split(", ");
    }

    private String toChat() {
        final String format = I18n.format("communication.message.usage");
        final String cmd = format.substring(0, format.indexOf(' '));

        final String args = this.args != null ? Arrays.toString(this.args) : "[]";

        return cmd + ' ' + this.to + ' ' + this.type.toString() + " $" + this.from + "$ {" + args + '}';
    }

    void send(Minecraft mc) {
        if (mc.player == null || !OptionCore.CLIENT_CHAT_PACKETS.isEnabled()) return;
        mc.player.sendChatMessage(this.toChat());
    }

    private void activate() {
        type.action(from, args);
    }
}
