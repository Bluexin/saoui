package com.saomc.saoui.config;

import com.saomc.saoui.SAOCore;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.common.config.Configuration;

import java.io.File;
import java.util.stream.Stream;

/**
 * Part of SAOUI
 *
 * @author Bluexin
 */
public class ConfigHandler {
    public static String _LAST_UPDATE;
    public static boolean _IGNORE_UPATE;
    public static boolean DEBUG = false;
    public static int debugFakePT;
    private static Configuration config;
    private static File saoConfDir;

    @SuppressWarnings("MethodCallSideOnly")
    public static void preInit(FMLPreInitializationEvent event) {
        config = new Configuration(new File(saoConfDir = confDir(event.getModConfigurationDirectory()), "main.cfg"));
        config.load();

        DEBUG = config.get(Configuration.CATEGORY_GENERAL, "debug", DEBUG).getBoolean();

        _LAST_UPDATE = config.get(Configuration.CATEGORY_GENERAL, "lastUpdate", "nothing").getString();
        _IGNORE_UPATE = config.get(Configuration.CATEGORY_GENERAL, "ignoreUpdate", false).getBoolean();

        Stream.of(OptionCore.values()).filter(OptionCore::isCategory)
                .forEach(c -> Stream.of(OptionCore.values()).filter(o -> o.getCategory() == c)
                        .forEach(o -> {
                            if (config.get(c.name().toLowerCase(), o.name().toLowerCase(), o.isEnabled()).getBoolean())
                                o.enable();
                            else o.disable();
                        }));

        Stream.of(OptionCore.values()).filter(o -> !o.isCategory() && o.getCategory() == null).forEach(o -> {
            if (config.get(Configuration.CATEGORY_GENERAL, o.name().toLowerCase(), o.isEnabled()).getBoolean())
                o.enable();
            else o.disable();
        });

        debugFakePT = config.getInt("debugFakePT", Configuration.CATEGORY_GENERAL, 0, 0, 10, "Amount of fake party members, 0 to disable.");

        config.save();
    }

    @SuppressWarnings("MethodCallSideOnly")
    public static void setOption(OptionCore option) {
        config.get(Configuration.CATEGORY_GENERAL, "option." + option.name().toLowerCase(), option.isEnabled()).set(option.isEnabled());
        saveAllOptions();
    }

    private static void saveAllOptions() {
        config.save();
    }

    public static void saveVersion(String version) {
        config.get(Configuration.CATEGORY_GENERAL, "last.update", getLastVersion()).set(version);
        config.save();
    }

    public static void setIgnoreVersion(boolean value) {
        config.get(Configuration.CATEGORY_GENERAL, "ignore.update", ignoreVersion()).set(value);
        config.save();
    }

    public static String getLastVersion() {
        return _LAST_UPDATE;
    }

    public static boolean ignoreVersion() {
        return _IGNORE_UPATE;
    }

    public static File getSaoConfDir() {
        return saoConfDir;
    }

    private static File confDir(File genDir) {
        return new File(genDir, SAOCore.MODID);
    }
}
