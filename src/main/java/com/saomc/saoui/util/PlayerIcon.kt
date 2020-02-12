package com.saomc.saoui.util

import be.bluexin.saomclib.party.PlayerInfo
import com.saomc.saoui.GLCore
import com.saomc.saoui.api.screens.IIcon
import com.teamwizardry.librarianlib.features.kotlin.Minecraft
import net.minecraft.client.entity.AbstractClientPlayer
import net.minecraft.client.resources.DefaultPlayerSkin
import net.minecraft.util.ResourceLocation

class PlayerIcon(val player: PlayerInfo): IIcon {

    val resourceLocation = getSkin(player.username)

    fun getSkin(name: String): ResourceLocation{
        val resource = AbstractClientPlayer.getLocationSkin(name)
        AbstractClientPlayer.getDownloadImageSkin(resource, name)
        //return resource
        return DefaultPlayerSkin.getDefaultSkin(player.uuid)
    }

    override fun getRL(): ResourceLocation? {
        return resourceLocation
    }

    override fun glDraw(x: Int, y: Int, z: Float) {
        GLCore.color(1f, 1f, 1f, 1f)
        GLCore.glBlend(true)
        Minecraft().renderEngine.bindTexture(resourceLocation)
        GLCore.glTexturedRectV2(x.toDouble(), y.toDouble(), width = 16.0, height = 16.0)
        //GLCore.glBlend(false)
    }


    override fun glDrawUnsafe(x: Int, y: Int) {
        GLCore.glTexturedRectV2(x.toDouble(), y.toDouble(), width = 16.0, height = 16.0)
    }
}