package com.saomc.saoui.screens.util

import com.mojang.blaze3d.matrix.MatrixStack
import com.saomc.saoui.GLCore
import com.saomc.saoui.SoundCore
import com.saomc.saoui.api.screens.IIcon
import com.saomc.saoui.play
import com.saomc.saoui.util.IconCore
import com.teamwizardry.librarianlib.core.util.Client
import net.minecraft.client.gui.toasts.IToast
import net.minecraft.client.gui.toasts.ToastGui

class NotificationAlert(val icon: IIcon, val title: String, val subtitle: String): IToast {

    private var firstDrawTime: Long = 0
    private var newDisplay = true

    //DRAW
    override fun func_230444_a_(stack: MatrixStack, toastGui: ToastGui, delta: Long): IToast.Visibility {

        if (newDisplay){
            firstDrawTime = delta
            newDisplay = false
            SoundCore.MESSAGE.play()
        }

        toastGui.minecraft.textureManager.bindTexture(IToast.TEXTURE_TOASTS)
        GLCore.color(1.0f, 1.0f, 1.0f)
        GLCore.glTexturedRectV2(stack, 0f, 0f, 0f, 0f, 96f, 160f, 32f)
        icon.glDraw(6, 8)

        if (subtitle.isEmpty()) {
            toastGui.minecraft.fontRenderer.drawString(stack, title, 25f, 12f, -11534256)
        } else {
            toastGui.minecraft.fontRenderer.drawString(stack, title, 25f, 7f, -11534256)
            toastGui.minecraft.fontRenderer.drawString(stack, subtitle, 25f, 18f, -16777216)
        }
        return if (delta - firstDrawTime < 5000L) IToast.Visibility.SHOW else IToast.Visibility.HIDE
    }

    companion object{
        fun new (icon: IconCore, title: String, subtitle: String){
            Client.minecraft.toastGui.add(NotificationAlert(icon, title, subtitle))
        }
    }
}