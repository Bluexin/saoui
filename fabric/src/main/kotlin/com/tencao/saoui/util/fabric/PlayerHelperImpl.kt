package com.tencao.saoui.util.fabric

import me.shedaniel.architectury.hooks.fabric.FakePlayers
import net.minecraft.world.entity.player.Player

object PlayerHelperImpl {


    fun isFakePlayer(player: Player): Boolean {
        return player is FakePlayers
    }
}