package com.tencao.saoui.api.events

import com.tencao.saoui.util.compat.Player
import net.minecraftforge.eventbus.api.Event


class ProfileInfoEvent(val player: Player, val info: List<String> = listOf()) : Event()
