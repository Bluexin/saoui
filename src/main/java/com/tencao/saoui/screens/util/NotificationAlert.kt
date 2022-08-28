package com.tencao.saoui.screens.util

import com.mojang.blaze3d.matrix.MatrixStack
import com.tencao.saomclib.Client
import com.tencao.saomclib.GLCore
import com.tencao.saoui.SoundCore
import com.tencao.saoui.api.screens.IIcon
import com.tencao.saoui.play
import com.tencao.saoui.util.IconCore
import net.minecraft.client.gui.toasts.IToast
import net.minecraft.client.gui.toasts.ToastGui

class NotificationAlert(val icon: IIcon, val title: String, val subtitle: String) : IToast {

    private var firstDrawTime: Long = 0
    private var newDisplay = true

    // DRAW
    override fun func_230444_a_(stack: MatrixStack, toastGui: ToastGui, delta: Long): IToast.Visibility {
        if (newDisplay) {
            firstDrawTime = delta
            newDisplay = false
            SoundCore.MESSAGE.play()
        }

        toastGui.minecraft.textureManager.bindTexture(IToast.TEXTURE_TOASTS)
        GLCore.color(1.0f, 1.0f, 1.0f)
        GLCore.glTexturedRectV2(0.0, 0.0, 0.0, 0.0, 96.0, 160.0, 32.0)
        icon.glDraw(6, 8)

        if (subtitle.isEmpty()) {
            GLCore.glString(title, 25, 12, -11534256, stack)
        } else {
            GLCore.glString(title, 25, 7, -11534256, stack)
            GLCore.glString(subtitle, 25, 18, -16777216, stack)
        }
        return if (delta - firstDrawTime < 5000L) IToast.Visibility.SHOW else IToast.Visibility.HIDE
    }

    companion object {
        fun new(icon: IconCore, title: String, subtitle: String) {
            Client.minecraft.toastGui.add(NotificationAlert(icon, title, subtitle))
        }
    }
}
