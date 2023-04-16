package com.tencao.saoui.util

import com.mojang.authlib.minecraft.MinecraftProfileTexture
import com.tencao.saomclib.Client
import com.tencao.saomclib.party.PlayerInfo
import com.tencao.saoui.api.screens.IIcon
import net.minecraft.client.renderer.GlStateManager
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
        com.tencao.saoui.GLCore.color(1f, 1f, 1f, 1f)
        com.tencao.saoui.GLCore.glBlend(true)
        com.tencao.saoui.GLCore.depth(true)
        GlStateManager.enableBlendProfile(GlStateManager.Profile.PLAYER_SKIN)
        com.tencao.saoui.GLCore.glBindTexture(texture)
        com.tencao.saoui.GLCore.glTexturedRectV2(x.toDouble(), y.toDouble(), width = 16.0, height = 16.0, srcWidth = 32.0, srcHeight = 32.0, srcX = 32.0, srcY = 31.0)
        GlStateManager.disableBlendProfile(GlStateManager.Profile.PLAYER_SKIN)
        com.tencao.saoui.GLCore.glBlend(false)
    }
}
