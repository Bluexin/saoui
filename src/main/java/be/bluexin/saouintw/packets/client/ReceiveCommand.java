package be.bluexin.saouintw.packets.client;

import be.bluexin.saouintw.communication.CommandType;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.UUID;
import java.util.stream.Stream;

/**
 * Part of saouintw, the networking mod for the SAO UI
 *
 * @author Bluexin
 */
public class ReceiveCommand implements IMessage {
    private CommandType cmd;
    private UUID sender;
    private String[] args;

    public ReceiveCommand() {

    }

    public ReceiveCommand(CommandType cmd, EntityPlayer sender, String... args) {
        this.cmd = cmd;
        this.sender = sender.getUniqueID();
        this.args = args;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.cmd = CommandType.values()[buf.readInt()];
        this.sender = UUID.fromString(ByteBufUtils.readUTF8String(buf));
        this.args = ByteBufUtils.readUTF8String(buf).split(" ");
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.cmd.ordinal());
        ByteBufUtils.writeUTF8String(buf, this.sender.toString());
        final StringBuilder sb = new StringBuilder();
        Stream.of(args).forEach(a -> sb.append(a).append(" "));
        ByteBufUtils.writeUTF8String(buf, sb.toString().substring(0, sb.length() - 1));
    }

    public static class Handler extends be.bluexin.saomclib.packets.AbstractClientPacketHandler<ReceiveCommand> {
        @Override
        public IMessage handleClientPacket(EntityPlayer player, ReceiveCommand message, MessageContext ctx, IThreadListener iThreadListener) {
            iThreadListener.addScheduledTask(() -> message.cmd.action(Minecraft.getMinecraft().world.getPlayerEntityByUUID(message.sender), message.args));
            return null;
        }
    }
}
