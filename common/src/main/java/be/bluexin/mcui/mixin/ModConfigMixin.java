package be.bluexin.mcui.mixin;

import com.electronwill.nightconfig.core.CommentedConfig;
import net.minecraftforge.fml.config.IConfigSpec;

public interface ModConfigMixin {
    void mcui$setSpec(final IConfigSpec<?> spec);

    void mcui$setConfigData(CommentedConfig configData);
}
