package com.tencao.saoui.util

import com.tencao.saomclib.Client
import net.minecraft.client.renderer.texture.SimpleTexture
import net.minecraft.util.ResourceLocation

object ThemeLoader {

    fun loadTextures(textures: List<ResourceLocation>) {
        textures.forEach { Client.textureManager.loadTexture(it, SimpleTexture(it)) }
    }

    fun unloadTextures(textures: List<ResourceLocation>) {
        textures.forEach { Client.textureManager.deleteTexture(it) }
    }
}
