package be.bluexin.saouintw;

import be.bluexin.saomclib.capabilities.CapabilitiesHandler;
import be.bluexin.saomclib.packets.PacketPipeline;
import be.bluexin.saouintw.communication.Communicator;
import be.bluexin.saouintw.packets.client.ReceiveCommand;
import be.bluexin.saouintw.packets.server.SendCommand;
import be.bluexin.saouintw.proxy.CommonProxy;
import com.saomc.saoui.SAOCore;
import com.saomc.saoui.api.entity.rendering.RenderCapability;
import com.saomc.saoui.util.LogCore;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkCheckHandler;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.common.MinecraftForge;

import java.util.Map;

/**
 * Part of saouintw, the networking mod for the SAO UI
 *
 * @author Bluexin
 */
@Mod(modid = SAOCore.MODID + "ntw", name = SAOCore.NAME + " networking", version = SAOCore.VERSION, acceptableSaveVersions = "*")
public class SaouiNtw {

    @SidedProxy(clientSide = "be.bluexin.saouintw.proxy.ClientProxy", serverSide = "be.bluexin.saouintw.proxy.CommonProxy")
    public static CommonProxy proxy; // Yeah I know people don' like hardcoded stuff. I don't care.

    private static void initPackets() {
        PacketPipeline.INSTANCE.registerMessage(ReceiveCommand.class, ReceiveCommand.Handler.class);
        PacketPipeline.INSTANCE.registerMessage(SendCommand.class, SendCommand.Handler.class);
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        MinecraftForge.EVENT_BUS.register(new EventHandler());
        CapabilitiesHandler.INSTANCE.registerEntityCapability(RenderCapability.class, (ent) -> ent instanceof EntityLivingBase);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
        initPackets();
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
