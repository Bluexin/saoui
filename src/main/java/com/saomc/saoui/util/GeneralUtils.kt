package com.saomc.saoui.util

import com.teamwizardry.librarianlib.core.util.Client
import net.minecraft.client.resources.I18n
import net.minecraft.client.util.NativeUtil
import net.minecraft.client.util.NativeUtil.getTime
import net.minecraft.util.text.TranslationTextComponent
import org.lwjgl.glfw.GLFW


fun String.localize(vararg parameters: Any) = I18n.format(this, parameters)

fun String.translate(vararg parameters: Any) = TranslationTextComponent(this, parameters)

val scaledWidth = Client.minecraft.mainWindow.scaledWidth

val scaledHeight = Client.minecraft.mainWindow.scaledHeight


fun getSystemTime(): Long {
    return (GLFW.glfwGetTime() * 1000L).toLong() / GLFW.glfwGetTimerValue()
}