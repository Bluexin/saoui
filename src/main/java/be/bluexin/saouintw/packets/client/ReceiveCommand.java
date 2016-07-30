package be.bluexin.saouintw.packets.client;

import be.bluexin.saouintw.packets.AbstractClientPacketHandler;
import com.saomc.communication.CommandType;
import com.saomc.social.StaticPlayerHelper;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.stream.Stream;

/**
 * Part of saouintw, the networking mod for the SAO UI
 *
 * @author Bluexin
 */
public class ReceiveCommand implements IMessage {
    private CommandType cmd;
    private String sender;
    private String[] args;

    public ReceiveCommand() {

    }

    public ReceiveCommand(CommandType cmd, EntityPlayer sender, String... args) {
        this.cmd = cmd;
        this.sender = sender.getDisplayNameString();
        this.args = args;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.cmd = CommandType.values()[buf.readInt()];
        this.sender = ByteBufUtils.readUTF8String(buf);
        this.args = ByteBufUtils.readUTF8String(buf).split(" ");
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.cmd.ordinal());
        ByteBufUtils.writeUTF8String(buf, this.sender);
        final StringBuilder sb = new StringBuilder();
        Stream.of(args).forEach(a -> sb.append(a).append(" "));
        ByteBufUtils.writeUTF8String(buf, sb.toString().substring(0, sb.length() - 1));
    }

    public static class Handler extends AbstractClientPacketHandler<ReceiveCommand> {
        @Override
        public IMessage handleClientPacket(EntityPlayer player, ReceiveCommand message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> message.cmd.action(StaticPlayerHelper.findOnlinePlayer(Minecraft.getMinecraft(), message.sender), message.args));
            return null;
        }
    }
}
