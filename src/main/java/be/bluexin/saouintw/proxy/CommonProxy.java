package be.bluexin.saouintw.proxy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Part of saouintw, the networking mod for the SAO UI
 *
 * @author Bluexin
 */
public class CommonProxy {

    public EntityPlayer getPlayerEntity(MessageContext ctx) {
        return ctx.getServerHandler().playerEntity;
    }

}
