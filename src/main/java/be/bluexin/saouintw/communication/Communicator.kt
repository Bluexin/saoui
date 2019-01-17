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

package be.bluexin.saouintw.communication

import be.bluexin.saomclib.packets.PacketPipeline
import be.bluexin.saouintw.packets.server.SendCommand
import net.minecraft.client.Minecraft
import net.minecraft.entity.player.EntityPlayer

/**
 * Part of saoui

 * @author Bluexin
 */
object Communicator {

    var supportsPackets: Boolean = false

    fun send(cmd: CommandType, target: EntityPlayer, vararg args: String) =
            if (supportsPackets) PacketPipeline.sendToServer(SendCommand(cmd, target, *args))
            else Command(cmd, target, *args).send(Minecraft.getMinecraft())
}
