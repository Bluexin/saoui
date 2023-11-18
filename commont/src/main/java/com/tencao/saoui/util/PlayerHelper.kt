package com.tencao.saoui.util

import com.mojang.authlib.GameProfile
import dev.architectury.injectables.annotations.ExpectPlatform
import net.minecraft.client.Minecraft
import net.minecraft.world.entity.player.Player

object PlayerHelper {

    @ExpectPlatform
    fun isFakePlayer(player: Player): Boolean{
        throw AssertionError()
    }
}

val GameProfile.isOnline: Boolean
    get() =
        (Minecraft.getInstance().currentServer?.playerList?.any { it.contents.contentEquals(this.name, true) }
            ?: Minecraft.getInstance().level?.getPlayerByUUID(this.id)) != (null
            ?: false)

