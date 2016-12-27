package com.saomc.saoui.communication

import be.bluexin.saouintw.packets.PacketPipeline
import be.bluexin.saouintw.packets.server.SendCommand
import net.minecraft.client.Minecraft
import net.minecraft.entity.player.EntityPlayer

/**
 * Part of saoui

 * @author Bluexin
 */
object Communicator {

    var supportsPackets: Boolean = false

    fun send(cmd: CommandType, target: EntityPlayer, vararg args: String) = if (supportsPackets)
        PacketPipeline.sendToServer(SendCommand(cmd, target, *args))
    else Command(cmd, target, *args).send(Minecraft.getMinecraft())
}
