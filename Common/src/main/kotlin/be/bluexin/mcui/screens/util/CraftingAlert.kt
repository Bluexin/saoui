package be.bluexin.mcui.screens.util

import be.bluexin.mcui.util.Client
import be.bluexin.mcui.SoundCore
import be.bluexin.mcui.play
import be.bluexin.mcui.util.CraftingUtil
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.toasts.GuiToast
import net.minecraft.client.gui.toasts.IToast
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.resources.I18n
import net.minecraft.item.ItemStack

class CraftingAlert(private val itemStack: ItemStack) : IToast {
    private var init = false
    private val itemIcon = ItemIcon { itemStack }
    private var lastDelta: Long = 0

    override fun draw(toastGui: GuiToast, delta: Long): IToast.Visibility {
        if (!init) {
            init = true
            SoundCore.MESSAGE.play()
        }
        toastGui.minecraft.textureManager.bindTexture(IToast.TEXTURE_TOASTS)
        GlStateManager.color(1.0f, 1.0f, 1.0f)
        toastGui.drawTexturedModalRect(0, 0, 0, 96, 160, 32)
        itemIcon.glDraw(6, 8)

        toastGui.minecraft.fontRenderer.drawString(I18n.get("notificationCraftingTitle", CraftingUtil.craftLimit * itemStack.count, itemStack.displayName), 25, 12, -11534256)
        Gui.drawRect(3, 28, 157, 29, -1)
        val f = CraftingUtil.currentCount.toFloat() / CraftingUtil.craftLimit.toFloat()

        val i: Int = if (CraftingUtil.currentCount >= CraftingUtil.craftLimit) {
            -16755456
        } else {
            -11206656
        }

        Gui.drawRect(3, 28, (3.0f + 154.0f * f).toInt(), 29, i)
        this.lastDelta = delta

        return if (!CraftingUtil.craftReady || CraftingUtil.currentCount >= CraftingUtil.craftLimit) {
            IToast.Visibility.HIDE } else IToast.Visibility.SHOW
    }

    companion object {
        fun new(stack: ItemStack) {
            Client.mc.toastGui.add(CraftingAlert(stack))
        }
    }
}
