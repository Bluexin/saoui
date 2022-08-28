package com.tencao.saoui.api.themes.json

import com.mojang.blaze3d.matrix.MatrixStack
import com.tencao.saomclib.Client
import com.tencao.saomclib.GLCore

data class StringPart(
    val x: Double,
    val y: Double,
    val text: String,
    val rgba: Int = -1,
    val shadow: Boolean,
    val parent: GroupPart
) : IRenderPart {

    override fun render(stack: MatrixStack) {
        val x = this.x + parent.getXPos()
        val y = this.y + parent.getYPos()
        GLCore.glString(Client.minecraft.fontRenderer, text, x.toInt(), y.toInt(), if (this.rgba != -1) rgba else 0xFFFFFFFF.toInt(), stack, shadow)
    }
}
