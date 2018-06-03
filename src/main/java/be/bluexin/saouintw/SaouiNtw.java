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
