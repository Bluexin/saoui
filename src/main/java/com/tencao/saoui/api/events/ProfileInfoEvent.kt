package com.tencao.saoui.api.events

import net.minecraft.entity.player.PlayerEntity
import net.minecraftforge.eventbus.api.Event

class ProfileInfoEvent(val player: PlayerEntity, val info: List<String> = listOf()) : Event()
