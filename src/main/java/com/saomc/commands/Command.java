package com.saomc.commands;

import com.saomc.social.StaticPlayerHelper;
import com.saomc.util.OptionCore;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

import java.util.Arrays;
import java.util.MissingFormatArgumentException;

/**
 * Part of the SAOUI project.
 *
 * @author Bluexin
 */
public class Command {
    private final CommandType type;
    private final String from;
    private final String to;
    private final String[] args;

    private Command(String raw) {
        if (!raw.contains("$")) throw new MissingFormatArgumentException("<username> not found in \"" + raw + '"');
        if (!raw.contains(CommandType.PREFIX) || !raw.contains(CommandType.SUFFIX))
            throw new MissingFormatArgumentException("invalid command: \"" + raw + '"');
        this.from = raw.substring(raw.indexOf('$') + 1, raw.lastIndexOf('$'));
        this.type = CommandType.getCommand(raw);
        this.to = StaticPlayerHelper.getName(Minecraft.getMinecraft());
        this.args = getContent(raw);
    }

    public Command(CommandType type, String to, String... args) {
        this.type = type;
        this.to = to;
        this.from = StaticPlayerHelper.getName(Minecraft.getMinecraft());
        this.args = args;
    }

    public static boolean processCommand(String raw) {
        if (Minecraft.getMinecraft().thePlayer == null || !OptionCore.CLIENT_CHAT_PACKETS.getValue()) return false;
        if (raw.contains(CommandType.PREFIX) && raw.contains(CommandType.SUFFIX)) {
            final Command command;
            try {
                command = new Command(raw);
            } catch (MissingFormatArgumentException e) {
                return false;
            }
            if (command.type != null) {
                if (!command.from.equals(StaticPlayerHelper.getName(Minecraft.getMinecraft()))) command.activate();
                return true;
            }
        }
        return false;
    }

    public static String[] getContent(String data) {
        return !data.contains("{[") || !data.contains("]}") ? null : data.substring(data.indexOf("{[") + 2, data.indexOf("]}")).split(", ");
    }

    public CommandType getType() {
        return type;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String toChat() {
        final String format = I18n.format("commands.message.usage");
        final String cmd = format.substring(0, format.indexOf(' '));

        final String args = this.args != null ? Arrays.toString(this.args) : "[]";

        return cmd + ' ' + this.to + ' ' + this.type.toString() + " $" + this.from + "$ {" + args + '}';
    }

    public void send(Minecraft mc) {
        if (mc.thePlayer == null || !OptionCore.CLIENT_CHAT_PACKETS.getValue()) return;
        mc.thePlayer.sendChatMessage(this.toChat());
    }

    private void activate() {
        type.action(Minecraft.getMinecraft(), from, args);
    }
}
