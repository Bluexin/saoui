package com.tencao.saoui.screens.util

/*
class CraftingAlert(private val itemStack: ItemStack): IToast {
    private var init = false
    private val itemIcon = ItemIcon{itemStack}
    private var lastDelta: Long = 0

    override fun func_230444_a_(stack: MatrixStack, toastGui: ToastGui, delta: Long): IToast.Visibility {
        if (!init){
            init = true
            SoundCore.MESSAGE.play()
        }
        toastGui.minecraft.textureManager.bindTexture(IToast.TEXTURE_TOASTS)
        GLCore.color(1.0f, 1.0f, 1.0f)
        toastGui.blit(stack, 0, 0, 0, 96, 160, 32)
        itemIcon.glDraw(6, 8)
        toastGui.minecraft.fontRenderer.drawText(stack, TranslationTextComponent("notificationCraftingTitle", CraftingUtil.craftLimit * itemStack.count, itemStack.displayName), 25f, 12f, -11534256)
        Screen.fill(stack, 3, 28, 157, 29, -1)
        val f = CraftingUtil.currentCount.toFloat() / CraftingUtil.craftLimit.toFloat()

        val i: Int = if (CraftingUtil.currentCount >= CraftingUtil.craftLimit) {
            -16755456
        } else {
            -11206656
        }

        Screen.fill(stack, 3, 28, (3.0f + 154.0f * f).toInt(), 29, i)
        this.lastDelta = delta

        return if (!CraftingUtil.craftReady || CraftingUtil.currentCount >= CraftingUtil.craftLimit)
            IToast.Visibility.HIDE else IToast.Visibility.SHOW
    }


    companion object{
        fun new (stack: ItemStack){
            Client.minecraft.toastGui.add(CraftingAlert(stack))
        }
    }

}*/
