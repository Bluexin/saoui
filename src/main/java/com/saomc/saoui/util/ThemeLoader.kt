package com.saomc.saoui.util

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.texture.SimpleTexture
import net.minecraft.util.ResourceLocation

object ThemeLoader {

    fun loadTextures(textures: List<ResourceLocation>){
        textures.forEach { Minecraft.getMinecraft().getTextureManager().loadTexture(it, SimpleTexture(it)) }
    }

    fun unloadTextures(textures: List<ResourceLocation>){
        textures.forEach { Minecraft.getMinecraft().getTextureManager().deleteTexture(it) }
    }
}