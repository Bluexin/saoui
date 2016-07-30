package be.bluexin.saouintw.packets;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Part of saouintw, the networking mod for the SAO UI
 *
 * @author Bluexin
 */
public abstract class AbstractServerPacketHandler<T extends IMessage> extends AbstractPacketHandler<T> {
    @Override
    public final IMessage handleClientPacket(EntityPlayer player, T message, MessageContext ctx) {
        return null;
    }
}
