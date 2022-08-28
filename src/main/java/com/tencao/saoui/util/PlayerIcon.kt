package com.tencao.saoui.util

import com.mojang.authlib.minecraft.MinecraftProfileTexture
import com.tencao.saomclib.Client
import com.tencao.saomclib.party.PlayerInfo
import com.tencao.saoui.api.screens.IIcon
import net.minecraft.client.resources.DefaultPlayerSkin
import net.minecraft.util.ResourceLocation

class PlayerIcon(val player: PlayerInfo) : IIcon {

    private val texture = getSkin()

    private fun getSkin(): ResourceLocation {
        return if (player.isOnline) {
            Client.minecraft.skinManager.loadSkinFromCache(player.gameProfile)[MinecraftProfileTexture.Type.SKIN]?.let {
                Client.minecraft.skinManager.loadSkin(it, MinecraftProfileTexture.Type.SKIN)
            } ?: DefaultPlayerSkin.getDefaultSkin(player.uuid)
        } else DefaultPlayerSkin.getDefaultSkin(player.uuid)
    }

    override fun glDraw(x: Int, y: Int, z: Float) {
        // TODO Fix
        /*
        GLCore.color(1f, 1f, 1f, 1f)
        GLCore.glBlend(true)
        GLCore.depth(true)
        GlStateManager.enableBlendProfile(GlStateManager.Profile.PLAYER_SKIN)
        GLCore.glBindTexture(texture)
        GLCore.glTexturedRectV2(x.toDouble(), y.toDouble(), width = 16f, height = 16f, srcWidth = 32f, srcHeight = 32f, srcX = 32f, srcY = 31f)
        GlStateManager.disableBlendProfile(GlStateManager.Profile.PLAYER_SKIN)
        GLCore.glBlend(false)*/
    }
}
