package be.bluexin.saouintw.packets.server;

import be.bluexin.saomclib.packets.PacketPipeline;
import be.bluexin.saouintw.communication.CommandType;
import be.bluexin.saouintw.packets.client.ReceiveCommand;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.stream.Stream;

/**
 * Part of saoui
 *
 * @author Bluexin
 */
public class SendCommand implements IMessage {
    private CommandType cmd;
    private UUID target;
    private String[] args;

    public SendCommand() {

    }

    public SendCommand(CommandType cmd, EntityPlayer target, String... args) {
        this.cmd = cmd;
        this.target = target.getUniqueID();
        this.args = args;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.cmd = CommandType.values()[buf.readInt()];
        this.target = UUID.fromString(ByteBufUtils.readUTF8String(buf));
        this.args = ByteBufUtils.readUTF8String(buf).split(" ");
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.cmd.ordinal());
        ByteBufUtils.writeUTF8String(buf, this.target.toString());
        final StringBuilder sb = new StringBuilder();
        Stream.of(args).forEach(a -> sb.append(a).append(" "));
        ByteBufUtils.writeUTF8String(buf, sb.toString().substring(0, sb.length() - 1));
    }

    public static class Handler extends be.bluexin.saomclib.packets.AbstractServerPacketHandler<SendCommand> {
        @Nullable
        @Override
        public IMessage handleServerPacket(EntityPlayer player, SendCommand message, MessageContext ctx, IThreadListener iThreadListener) {
            PacketPipeline.INSTANCE.sendTo(new ReceiveCommand(message.cmd, player, message.args), (EntityPlayerMP) player.world.getPlayerEntityByUUID(message.target));
            return null;
        }
    }
}
