package com.tencao.saoui.util.forge

import net.minecraft.world.entity.player.Player
import net.minecraftforge.common.util.FakePlayer

object PlayerHelperImpl {

    fun isFakePlayer(player: Player): Boolean{
        return player is FakePlayer
    }
}