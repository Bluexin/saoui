package com.saomc.saoui.util

import be.bluexin.saomclib.party.PlayerInfo
import com.mojang.authlib.minecraft.MinecraftProfileTexture
import com.saomc.saoui.GLCore
import com.saomc.saoui.api.screens.IIcon
import com.teamwizardry.librarianlib.features.kotlin.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.resources.DefaultPlayerSkin
import net.minecraft.util.ResourceLocation

class PlayerIcon(val player: PlayerInfo): IIcon {

    private val texture = getSkin()

    private fun getSkin(): ResourceLocation{
        return if (player.isOnline) {
            Minecraft().skinManager.loadSkinFromCache(player.gameProfile).get(MinecraftProfileTexture.Type.SKIN)?.let {
                Minecraft().skinManager.loadSkin(it, MinecraftProfileTexture.Type.SKIN)
            } ?: DefaultPlayerSkin.getDefaultSkin(player.uuid)
        } else DefaultPlayerSkin.getDefaultSkin(player.uuid)
    }

    override fun glDraw(x: Int, y: Int, z: Float) {
        GLCore.color(1f, 1f, 1f, 1f)
        GLCore.glBlend(true)
        GLCore.depth(true)
        GlStateManager.enableBlendProfile(GlStateManager.Profile.PLAYER_SKIN)
        GLCore.glBindTexture(texture)
        GLCore.glTexturedRectV2(x.toDouble(), y.toDouble(), width = 16.0, height = 16.0, srcWidth = 32.0, srcHeight = 32.0, srcX = 32.0, srcY = 31.0)
        GlStateManager.disableBlendProfile(GlStateManager.Profile.PLAYER_SKIN)
        GLCore.glBlend(false)
    }

}