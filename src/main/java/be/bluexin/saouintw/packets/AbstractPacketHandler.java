package be.bluexin.saouintw.packets;

import be.bluexin.saouintw.SaouiNtw;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Part of saouintw, the networking mod for the SAO UI
 *
 * @author Bluexin
 */
public abstract class AbstractPacketHandler<T extends IMessage> implements IMessageHandler<T, IMessage> {
    @SideOnly(Side.CLIENT)
    public abstract IMessage handleClientPacket(EntityPlayer player, T message, MessageContext ctx);

    public abstract IMessage handleServerPacket(EntityPlayer player, T message, MessageContext ctx);

    @Override
    public IMessage onMessage(T message, MessageContext ctx) {
        if (ctx.side.isClient()) {
            return handleClientPacket(SaouiNtw.proxy.getPlayerEntity(ctx), message, ctx);
        } else {
            return handleServerPacket(SaouiNtw.proxy.getPlayerEntity(ctx), message, ctx);
        }
    }
}
