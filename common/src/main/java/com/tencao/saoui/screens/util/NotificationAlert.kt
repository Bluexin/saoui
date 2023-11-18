package com.tencao.saoui.screens.util

import com.mojang.blaze3d.vertex.PoseStack
import com.tencao.saoui.GLCore
import com.tencao.saoui.SoundCore
import com.tencao.saoui.api.screens.IIcon
import com.tencao.saoui.util.Client
import com.tencao.saoui.util.IconCore
import net.minecraft.client.gui.components.toasts.Toast
import net.minecraft.client.gui.components.toasts.ToastComponent

class NotificationAlert(val icon: IIcon, val title: String, val subtitle: String) : Toast {

    private var firstDrawTime: Long = 0
    private var newDisplay = true

    // DRAW
    override fun render(poseStack: PoseStack, toastGui: ToastComponent, delta: Long): Toast.Visibility {
        if (newDisplay) {
            firstDrawTime = delta
            newDisplay = false
            SoundCore.MESSAGE.play()
        }

        GLCore.glBindTexture(Toast.TEXTURE)
        GLCore.color(1.0f, 1.0f, 1.0f)
        GLCore.glTexturedRectV2(0.0, 0.0, 0.0, 0.0, 96.0, 160.0, 32.0)
        icon.glDraw(6, 8)

        if (subtitle.isEmpty()) {
            GLCore.glString(title, 25, 12, -11534256, poseStack)
        } else {
            GLCore.glString(title, 25, 7, -11534256, poseStack)
            GLCore.glString(subtitle, 25, 18, -16777216, poseStack)
        }
        return if (delta - firstDrawTime < 5000L) Toast.Visibility.SHOW else Toast.Visibility.HIDE
    }

    companion object {
        fun new(icon: IconCore, title: String, subtitle: String) {
            Client.minecraft.toasts.addToast(NotificationAlert(icon, title, subtitle))
        }
    }
}
