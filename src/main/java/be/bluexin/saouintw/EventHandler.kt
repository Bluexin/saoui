/*
 * Copyright (C) 2016-2019 Arnaud 'Bluexin' Solé
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package be.bluexin.saouintw

import com.saomc.saoui.api.entity.rendering.PlayerColorStateHandler
import com.saomc.saoui.api.entity.rendering.RenderCapability
import net.minecraft.entity.Entity
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
    fun attachCapabilities(event: AttachCapabilitiesEvent<Entity>) {
        if (event.`object` is EntityLivingBase && !event.`object`.hasCapability(RenderCapability.RENDER_CAPABILITY, null))
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
        if (e.entityLiving is EntityPlayer && e.source.trueSource is EntityPlayer)
            println(e.entityLiving.toString() + " attacked by " + e.source.trueSource)
    }

    @SubscribeEvent
    fun pk(e: LivingDeathEvent) {
        if (e.entityLiving is EntityPlayer && e.source.trueSource is EntityPlayer)
            (RenderCapability.get(e.entityLiving).colorStateHandler as PlayerColorStateHandler).kill(e.source.trueSource as EntityPlayer?)
    }
}
