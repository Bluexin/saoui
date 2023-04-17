package be.bluexin.mcui.util

import com.mojang.authlib.minecraft.MinecraftProfileTexture
import be.bluexin.mcui.util.Client
import com.tencao.saomclib.party.PlayerInfo
import be.bluexin.mcui.api.screens.IIcon
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.resources.DefaultPlayerSkin
import net.minecraft.resources.ResourceLocation

class PlayerIcon(val player: PlayerInfo) : IIcon {

    private val texture = getSkin()

    private fun getSkin(): ResourceLocation {
        return if (player.isOnline) {
            Client.mc.skinManager.loadSkinFromCache(player.gameProfile)[MinecraftProfileTexture.Type.SKIN]?.let {
                Client.mc.skinManager.loadSkin(it, MinecraftProfileTexture.Type.SKIN)
            } ?: DefaultPlayerSkin.getDefaultSkin(player.uuid)
        } else DefaultPlayerSkin.getDefaultSkin(player.uuid)
    }

    override fun glDraw(x: Int, y: Int, z: Float) {
        be.bluexin.mcui.GLCore.color(1f, 1f, 1f, 1f)
        be.bluexin.mcui.GLCore.glBlend(true)
        be.bluexin.mcui.GLCore.depth(true)
        GlStateManager.enableBlendProfile(GlStateManager.Profile.PLAYER_SKIN)
        be.bluexin.mcui.GLCore.glBindTexture(texture)
        be.bluexin.mcui.GLCore.glTexturedRectV2(x.toDouble(), y.toDouble(), width = 16.0, height = 16.0, srcWidth = 32.0, srcHeight = 32.0, srcX = 32.0, srcY = 31.0)
        GlStateManager.disableBlendProfile(GlStateManager.Profile.PLAYER_SKIN)
        be.bluexin.mcui.GLCore.glBlend(false)
    }
}
