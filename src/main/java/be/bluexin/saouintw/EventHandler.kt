package be.bluexin.saouintw

import com.saomc.saoui.api.entity.rendering.PlayerColorStateHandler
import com.saomc.saoui.api.entity.rendering.RenderCapability
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraftforge.event.AttachCapabilitiesEvent
import net.minecraftforge.event.entity.living.LivingAttackEvent
import net.minecraftforge.event.entity.living.LivingDeathEvent
import net.minecraftforge.event.entity.living.LivingEvent
import net.minecraftforge.event.entity.player.AttackEntityEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

/**
 * Part of saoui

 * @author Bluexin
 */
internal class EventHandler {

    @SubscribeEvent
    fun attachCapabilities(event: AttachCapabilitiesEvent.Entity) {
        if (event.entity is EntityLivingBase && !event.entity.hasCapability(RenderCapability.RENDER_CAPABILITY, null))
            RenderCapability.register(event)
    }

    @SubscribeEvent
    fun playerRespawn(event: net.minecraftforge.event.entity.player.PlayerEvent.Clone) {
        // TODO: config to "cleanse" a player's criminal record?
        if (event.isWasDeath) RenderCapability.RENDER_CAPABILITY.readNBT(RenderCapability.get(event.entityPlayer), null, RenderCapability.RENDER_CAPABILITY.writeNBT(RenderCapability.get(event.original), null))
    }

    @SubscribeEvent
    fun livingTick(e: LivingEvent.LivingUpdateEvent) {
        if (e.entityLiving.hasCapability(RenderCapability.RENDER_CAPABILITY, null))
            RenderCapability.get(e.entityLiving).colorStateHandler.tick()
    }


    /*
    The events below don't work properly on clients.
    Currently, no sync is made.
     */

    @SubscribeEvent
    fun attackEntity(e: AttackEntityEvent) {
        /*
        this one might be flawed though:
        public static boolean onPlayerAttackTarget(EntityPlayer player, Entity target) {
            if (MinecraftForge.EVENT_BUS.post(new AttackEntityEvent(player, target))) return false;
            ItemStack stack = player.getHeldItemMainhand();
            if (stack != null && stack.getItem().onLeftClickEntity(stack, player, target)) return false;
            return true;
        }

        But it seems to be called both on client and server // TODO: check other clients
         */

        if (e.target is EntityPlayer)
            (RenderCapability.get(e.entityLiving).colorStateHandler as PlayerColorStateHandler).hit(e.target as EntityPlayer)
    }

    @SubscribeEvent
    fun attackEntity(e: LivingAttackEvent) {
        if (e.entityLiving is EntityPlayer && e.source.entity is EntityPlayer)
            println(e.entityLiving.toString() + " attacked by " + e.source.entity)
    }

    @SubscribeEvent
    fun pk(e: LivingDeathEvent) {
        if (e.entityLiving is EntityPlayer && e.source.entity is EntityPlayer)
            (RenderCapability.get(e.entityLiving).colorStateHandler as PlayerColorStateHandler).kill(e.source.entity as EntityPlayer?)
    }
}
