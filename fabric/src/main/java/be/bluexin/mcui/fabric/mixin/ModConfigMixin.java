package be.bluexin.mcui.fabric.mixin;

import com.electronwill.nightconfig.core.CommentedConfig;
import net.minecraftforge.fml.config.IConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ModConfig.class)
public class ModConfigMixin implements be.bluexin.mcui.mixin.ModConfigMixin {

    @Mutable
    @Final
    @Shadow
    private IConfigSpec<?> spec;

    @Mutable
    @Shadow
    private CommentedConfig configData;

    @Override
    public void mcui$setSpec(IConfigSpec<?> spec) {
        this.spec = spec;
    }

    @Override
    public void mcui$setConfigData(CommentedConfig configData) {
        this.configData = configData;
        this.spec.acceptConfig(configData);
    }
}
