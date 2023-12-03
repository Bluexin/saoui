package be.bluexin.mcui.fabric.mixin;

import net.minecraft.locale.Language;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.regex.Pattern;

@Mixin(Language.class)
public class ExampleMixin {

    @Mutable
    @Final
    @Shadow
    private static Pattern UNSUPPORTED_FORMAT_PATTERN;

    @Inject(at = @At("TAIL"), method = "<clinit>")
    private static void init(CallbackInfo info) {
//        UNSUPPORTED_FORMAT_PATTERN = Pattern.compile("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
    }
}
