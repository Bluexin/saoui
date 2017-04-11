package be.bluexin.saouintw;

import java.util.Map;

import com.saomc.saoui.SAOCore;
import com.saomc.saoui.api.entity.rendering.RenderCapability;
import com.saomc.saoui.communication.Communicator;
import com.saomc.saoui.util.LogCore;

import be.bluexin.saouintw.packets.PacketPipeline;
import be.bluexin.saouintw.proxy.CommonProxy;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkCheckHandler;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Part of saouintw, the networking mod for the SAO UI
 *
 * @author Bluexin
 */
@Mod(modid = SAOCore.MODID + "ntw", name = SAOCore.NAME + " networking", version = SAOCore.VERSION)
public class SaouiNtw {

    @SidedProxy(clientSide = "be.bluexin.saouintw.proxy.ClientProxy", serverSide = "be.bluexin.saouintw.proxy.CommonProxy")
    public static CommonProxy proxy; // Yeah I know people don' like hardcoded stuff. I don't care.

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        MinecraftForge.EVENT_BUS.register(new EventHandler());
        RenderCapability.registerCapability();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
        PacketPipeline.init();
    }

    @NetworkCheckHandler
    public boolean checkOtherSide(Map<String, String> remoteVersions, Side side) {
        if (side.equals(Side.SERVER)) {
            Communicator.INSTANCE.setSupportsPackets(SAOCore.VERSION.equals(remoteVersions.get(SAOCore.MODID + "ntw")));
            LogCore.logInfo("Connected to a server " + (Communicator.INSTANCE.getSupportsPackets() ? "with" : "without") + " support for Packets.");
        }

        return true;
    }
}
