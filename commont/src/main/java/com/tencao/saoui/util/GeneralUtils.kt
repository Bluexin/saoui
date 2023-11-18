package com.tencao.saoui.util

import net.minecraft.client.resources.language.I18n
import net.minecraft.network.chat.TextComponent
import net.minecraft.network.chat.TranslatableComponent
import org.lwjgl.glfw.GLFW

fun String.localize(vararg parameters: Any): String = I18n.get(this, parameters)

fun String.translate(vararg parameters: Any) = TranslatableComponent(this, parameters)

fun String.check() = I18n.exists(this)

fun String.toTextComponent(vararg parameters: Any) = TextComponent(this)

val scaledWidth
    get() = Client.minecraft.window.guiScaledWidth

val scaledHeight
    get() = Client.minecraft.window.guiScaledHeight

fun getSystemTime(): Long {
    return (GLFW.glfwGetTime() * 1000L).toLong() / GLFW.glfwGetTimerValue()
}
