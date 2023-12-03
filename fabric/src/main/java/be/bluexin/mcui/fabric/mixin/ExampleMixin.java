package be.bluexin.mcui.fabric.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class ExampleMixin {
    @Shadow
    public Gui gui;

    @Inject(at = @At("TAIL"), method = "<init>(Lnet/minecraft/client/main/GameConfig;)V")
    private void init(CallbackInfo info) {
//        this.gui = new McuiGui((Minecraft) this);
    }
}
