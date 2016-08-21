package be.bluexin.saouintw;

import be.bluexin.saouintw.packets.PacketPipeline;
import be.bluexin.saouintw.proxy.CommonProxy;
import com.saomc.saoui.SAOCore;
import com.saomc.saoui.communication.Communicator;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkCheckHandler;
import net.minecraftforge.fml.relauncher.Side;

import java.util.Map;

/**
 * Part of saouintw, the networking mod for the SAO UI
 *
 * @author Bluexin
 */
@Mod(modid = SAOCore.MODID + "ntw", name = SAOCore.NAME + " networking", version = SAOCore.VERSION)
// Dependency on SAOUI should only be on client
public class SaouiNtw {

    @SidedProxy(clientSide = "be.bluexin.saouintw.proxy.ClientProxy", serverSide = "be.bluexin.saouintw.proxy.CommonProxy")
    public static CommonProxy proxy; // Yeah I know people don' like hardcoded stuff. I don't care.

    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
        PacketPipeline.init();
    }

    @NetworkCheckHandler()
    public boolean checkOtherSide(Map<String, String> remoteVersions, Side side) {
        if (side.equals(Side.SERVER)) {
            Communicator.supportsPackets = SAOCore.VERSION.equals(remoteVersions.get(SAOCore.MODID + "ntw"));
            System.out.println("Connected to a server " + (Communicator.supportsPackets ? "with" : "without") + " support for Packets.");
        }

        return true;
    }
}
