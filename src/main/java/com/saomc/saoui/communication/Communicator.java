package com.saomc.saoui.communication;

import be.bluexin.saouintw.packets.PacketPipeline;
import be.bluexin.saouintw.packets.server.SendCommand;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Part of saoui
 *
 * @author Bluexin
 */
public class Communicator {

    public static boolean supportsPackets;

    public static void send(CommandType cmd, EntityPlayer target, String... args) {
        if (supportsPackets) PacketPipeline.sendToServer(new SendCommand(cmd, target, args));
        else new Command(cmd, target, args).send(Minecraft.getMinecraft());
    }
}
