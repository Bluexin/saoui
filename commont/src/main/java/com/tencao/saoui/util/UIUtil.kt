package com.tencao.saoui.util


object UIUtil {

    fun closeGame() {
        Client.minecraft.screen = null
        Client.minecraft.level?.disconnect()
        Client.minecraft.clearLevel()
    }

    fun resetMouse() {
        if (!Client.minecraft.mouseHandler.mouseGrabbed) {
            Client.minecraft.mouseHandler.mouseGrabbed = true
            Client.minecraft.mouseHandler.releaseMouse()
        }
    }
}
