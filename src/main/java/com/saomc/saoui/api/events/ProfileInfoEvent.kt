package com.saomc.saoui.api.events

import net.minecraft.entity.player.EntityPlayer
import net.minecraftforge.fml.common.eventhandler.Event

class ProfileInfoEvent(val player: EntityPlayer, val info: List<String> = listOf()): Event()