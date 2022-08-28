package com.tencao.saoui.api.entity.rendering

object UninitializedStateHandler : IColorStateHandler {

    override fun getColorState(): ColorState = ColorState.INVALID

    override fun shouldDrawCrystal(): Boolean = false
    override fun shouldDrawHealth(): Boolean = false
}
