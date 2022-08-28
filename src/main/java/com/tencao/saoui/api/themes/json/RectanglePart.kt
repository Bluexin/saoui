package com.tencao.saoui.api.themes.json

import com.google.gson.annotations.SerializedName
import com.mojang.blaze3d.matrix.MatrixStack
import com.tencao.saomclib.GLCore
import net.minecraft.util.ResourceLocation

data class RectanglePart(
    @SerializedName("texture")
    val texture: ResourceLocation,
    @SerializedName("rgba")
    val rgba: Int = -1,
    @SerializedName("x")
    val x: Double = 0.0,
    @SerializedName("y")
    val y: Double = 0.0,
    @SerializedName("z")
    val z: Double = 0.0,
    @SerializedName("height")
    val height: Double,
    @SerializedName("width")
    val width: Double,
    @SerializedName("srcX")
    val srcX: Double = 0.0,
    @SerializedName("srcY")
    val srcY: Double = 0.0,
    @SerializedName("srcWidth")
    val srcWidth: Double = width,
    @SerializedName("srcHeight")
    val srcHeight: Double = height,
    val parent: GroupPart
) : IRenderPart {

    override fun render(stack: MatrixStack) {
        val x = this.x + parent.getXPos()
        val y = this.y + parent.getYPos()
        GLCore.glBlend(true)
        GLCore.color(if (this.rgba != -1) rgba else 0xFFFFFFFF.toInt())
        GLCore.glBindTexture(texture)
        GLCore.glTexturedRectV2(x, y, z, width, height, srcX, srcY, srcWidth, srcHeight)
    }
}
