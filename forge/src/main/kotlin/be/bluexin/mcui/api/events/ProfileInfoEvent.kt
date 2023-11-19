package be.bluexin.mcui.api.events

import net.minecraft.world.entity.player.Player
import net.minecraftforge.eventbus.api.Event

class ProfileInfoEvent(val player: Player, val info: List<String> = listOf()) : Event()
