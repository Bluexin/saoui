package com.tencao.saoui.mixin

import com.mojang.blaze3d.vertex.PoseStack
import com.tencao.saoui.config.OptionCore
import com.tencao.saoui.themes.ThemeManager
import com.tencao.saoui.themes.elements.HudPartType
import com.tencao.saoui.themes.util.HudDrawContext
import com.tencao.saoui.util.Client
import net.minecraft.client.gui.Gui
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

/*
@Mixin(Gui::class)
class IngameGuiMixin {

    @Inject(method = ["renderHotbar(F, Lcom/mojang/blaze3d/vertex/PoseStack;)V"], at =  [At("HEAD")])
    fun renderHotbar(partialTicks: Float, poseStack: PoseStack, ci: CallbackInfo) {
        if (!OptionCore.DEFAULT_HOTBAR.isEnabled || !OptionCore.VANILLA_UI.isEnabled) {
            if (Client.minecraft.gameMode?.isAlwaysFlying != true) {
                Client.minecraft.profiler.push("hotbar")
                ThemeManager.HUD.draw(HudPartType.HOTBAR, context, poseStack)
                Client.minecraft.profiler.pop()
                ci.cancel()
            }
        }
    }



    companion object {
        private var context: HudDrawContext = HudDrawContext()
    }



}*/