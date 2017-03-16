package be.bluexin.saouintw

import com.saomc.saoui.api.entity.rendering.PlayerColorStateHandler
import com.saomc.saoui.api.entity.rendering.RenderCapability
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import net.minecraft.entity.player.EntityPlayer
import net.minecraftforge.event.entity.living.LivingAttackEvent
import net.minecraftforge.event.entity.living.LivingDeathEvent
import net.minecraftforge.event.entity.living.LivingEvent
import net.minecraftforge.event.entity.player.AttackEntityEvent

/**
 * Part of saoui

 * @author Bluexin
 */
internal class EventHandler {

    @SubscribeEvent
    fun livingTick(e: LivingEvent.LivingUpdateEvent) {
        RenderCapability.get(e.entityLiving)?.colorStateHandler?.tick()
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
