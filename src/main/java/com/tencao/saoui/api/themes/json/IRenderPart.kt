package com.tencao.saoui.api.themes.json

import com.mojang.blaze3d.matrix.MatrixStack

interface IRenderPart {
    fun render(stack: MatrixStack)
}
