package be.bluexin.saouintw;

import com.saomc.saoui.api.entity.rendering.PlayerColorStateHandler;
import com.saomc.saoui.api.entity.rendering.RenderCapability;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Part of saoui
 *
 * @author Bluexin
 */
class EventHandler {

    @SubscribeEvent
    public void attachCapabilities(AttachCapabilitiesEvent.Entity event) {
        //noinspection ConstantConditions
        if (event.getEntity() instanceof EntityLivingBase && !event.getEntity().hasCapability(RenderCapability.RENDER_CAPABILITY, null))
            RenderCapability.register(event);
    }

    @SubscribeEvent
    public void playerRespawn(net.minecraftforge.event.entity.player.PlayerEvent.Clone event) {
        if (event.isWasDeath()) { // TODO: config to "cleanse" a player's criminal record?
            //noinspection ConstantConditions
            RenderCapability.RENDER_CAPABILITY.readNBT(RenderCapability.get(event.getEntityPlayer()), null, RenderCapability.RENDER_CAPABILITY.writeNBT(RenderCapability.get(event.getOriginal()), null));
        }
    }

    @SubscribeEvent
    public void livingTick(LivingEvent.LivingUpdateEvent e) {
        //noinspection ConstantConditions
        if (e.getEntityLiving().hasCapability(RenderCapability.RENDER_CAPABILITY, null))
            RenderCapability.get(e.getEntityLiving()).colorStateHandler.tick();
    }


    /*
    The events below don't work properly on clients.
    Currently, no sync is made.
     */

    @SubscribeEvent
    public void attackEntity(AttackEntityEvent e) {
        /*
        this one might be flawed though:
        public static boolean onPlayerAttackTarget(EntityPlayer player, Entity target) {
            if (MinecraftForge.EVENT_BUS.post(new AttackEntityEvent(player, target))) return false;
            ItemStack stack = player.getHeldItemMainhand();
            if (stack != null && stack.getItem().onLeftClickEntity(stack, player, target)) return false;
            return true;
        }
         */

        if (e.getTarget() instanceof EntityPlayer)
            ((PlayerColorStateHandler) RenderCapability.get(e.getEntityLiving()).colorStateHandler).hit((EntityPlayer) e.getTarget());
    }

    @SubscribeEvent
    public void attackEntity(LivingAttackEvent e) {
        if (e.getEntityLiving() instanceof EntityPlayer && e.getSource().getEntity() instanceof EntityPlayer)
            System.out.println(e.getEntityLiving() + " attacked by " + e.getSource().getEntity());
    }

    @SubscribeEvent
    public void pk(LivingDeathEvent e) {
        if (e.getEntityLiving() instanceof EntityPlayer && e.getSource().getEntity() instanceof EntityPlayer)
            ((PlayerColorStateHandler) RenderCapability.get(e.getEntityLiving()).colorStateHandler).kill((EntityPlayer) e.getSource().getEntity());
    }
}
