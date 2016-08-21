package be.bluexin.saouintw.packets;

import be.bluexin.saouintw.packets.client.ReceiveCommand;
import be.bluexin.saouintw.packets.server.SendCommand;
import com.saomc.saoui.SAOCore;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Part of saouintw, the networking mod for the SAO UI
 *
 * @author Bluexin
 */
public class PacketPipeline {

    private static final SimpleNetworkWrapper ntw = NetworkRegistry.INSTANCE.newSimpleChannel(SAOCore.MODID + "ntw");
    private static byte packetId = 0;

    public static void init() {
        registerMessage(ReceiveCommand.Handler.class, ReceiveCommand.class);
        registerMessage(SendCommand.Handler.class, SendCommand.class);
    }

    private static <REQ extends IMessage, REPLY extends IMessage> void registerMessage(Class<? extends IMessageHandler<REQ, REPLY>> handlerClass, Class<REQ> messageClass) {
        Side side = AbstractClientPacketHandler.class.isAssignableFrom(handlerClass) ? Side.CLIENT : Side.SERVER;
        ntw.registerMessage(handlerClass, messageClass, packetId++, side);
    }

    public static void sendTo(IMessage message, EntityPlayerMP player) {
        if (player.connection != null) ntw.sendTo(message, player);
    }

    public static void sendToAll(IMessage message) {
        ntw.sendToAll(message);
    }

    public static void sendToAllAround(IMessage message, TargetPoint point) {
        ntw.sendToAllAround(message, point);
    }

    public static void sendToAllAround(IMessage message, int dimension, double x, double y, double z, double range) {
        sendToAllAround(message, new TargetPoint(dimension, x, y, z, range));
    }

    public static void sendToAllAround(IMessage message, EntityPlayer player, double range) {
        sendToAllAround(message, player.worldObj.provider.getDimension(), player.posX, player.posY, player.posZ, range);
    }

    public static void sendToDimension(IMessage message, int dimensionId) {
        ntw.sendToDimension(message, dimensionId);
    }

    public static void sendToServer(IMessage message) {
        ntw.sendToServer(message);
    }
}
