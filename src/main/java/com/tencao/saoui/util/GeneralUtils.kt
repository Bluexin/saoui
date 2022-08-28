package com.tencao.saoui.util

import com.tencao.saomclib.Client
import net.minecraft.client.resources.I18n
import net.minecraft.util.text.StringTextComponent
import net.minecraft.util.text.TranslationTextComponent
import org.lwjgl.glfw.GLFW

fun String.localize(vararg parameters: Any): String = I18n.format(this, parameters)

fun String.translate(vararg parameters: Any) = TranslationTextComponent(this, parameters)

fun String.toTextComponent(vararg parameters: Any) = StringTextComponent(this)

val scaledWidth
    get() = Client.minecraft.mainWindow.scaledWidth

val scaledHeight
    get() = Client.minecraft.mainWindow.scaledHeight

fun getSystemTime(): Long {
    return (GLFW.glfwGetTime() * 1000L).toLong() / GLFW.glfwGetTimerValue()
}
