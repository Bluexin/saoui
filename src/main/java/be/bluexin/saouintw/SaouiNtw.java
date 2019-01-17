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

package be.bluexin.saouintw;

import be.bluexin.saomclib.packets.PacketPipeline;
import be.bluexin.saouintw.communication.Communicator;
import be.bluexin.saouintw.packets.client.ReceiveCommand;
import be.bluexin.saouintw.packets.server.SendCommand;
import be.bluexin.saouintw.proxy.CommonProxy;
import com.saomc.saoui.SAOCore;
import com.saomc.saoui.api.entity.rendering.RenderCapability;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkCheckHandler;
import net.minecraftforge.fml.relauncher.Side;

import java.util.Map;

/**
 * Part of saouintw, the networking mod for the SAO UI
 *
 * @author Bluexin
 */
@Mod(modid = SAOCore.MODID + "ntw", name = SAOCore.NAME + " networking", version = SAOCore.VERSION, acceptableSaveVersions = "*")
public class SaouiNtw {

    @SidedProxy(clientSide = "be.bluexin.saouintw.proxy.ClientProxy", serverSide = "be.bluexin.saouintw.proxy.CommonProxy")
    public static CommonProxy proxy;

    private static void initPackets() {
        PacketPipeline.INSTANCE.registerMessage(ReceiveCommand.class, ReceiveCommand.Handler.class);
        PacketPipeline.INSTANCE.registerMessage(SendCommand.class, SendCommand.Handler.class);
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        MinecraftForge.EVENT_BUS.register(new EventHandler());
        RenderCapability.registerCapability();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
        initPackets();
    }

    @NetworkCheckHandler
    public boolean checkOtherSide(Map<String, String> remoteVersions, Side side) {
        if (side.equals(Side.SERVER)) {
            Communicator.INSTANCE.setSupportsPackets(SAOCore.VERSION.equals(remoteVersions.get(SAOCore.MODID + "ntw")));
            SAOCore.INSTANCE.getLOGGER().info("Connected to a server " + (Communicator.INSTANCE.getSupportsPackets() ? "with" : "without") + " support for Packets.");
        }

        return true;
    }
}
