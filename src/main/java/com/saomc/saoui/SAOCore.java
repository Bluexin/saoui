package com.saomc.saoui;

import com.saomc.saoui.api.events.EventInitStatsProvider;
import com.saomc.saoui.config.ConfigHandler;
import com.saomc.saoui.events.EventCore;
import com.saomc.saoui.themes.ThemeLoader;
import com.saomc.saoui.util.DefaultStatsProvider;
import com.saomc.saoui.util.PlayerStats;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.common.MinecraftForge;

@Mod(modid = SAOCore.MODID, name = SAOCore.NAME, version = SAOCore.VERSION, dependencies = SAOCore.DEPS, acceptableSaveVersions = "*", canBeDeactivated = true)
public class SAOCore {
    public static final String MODID = "saoui";
    public static final String NAME = "Sword Art Online UI";
    public static final String VERSION = "2.0-lite-dev";
    public static final String DEPS = "required-before:" + MODID + "ntw;required-after:saomclib@[1.0.5,)";
    public static final float UNKNOWN_TIME_DELAY = -1F;
    // TODO: optimize things, ie remove public and static!

    @Mod.Instance(MODID)
    public static SAOCore instance;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) throws Exception {
        ThemeLoader.load();

        MinecraftForge.EVENT_BUS.register(new EventCore());
        ConfigHandler.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        final EventInitStatsProvider s = new EventInitStatsProvider(new DefaultStatsProvider());
        MinecraftForge.EVENT_BUS.post(s);
        PlayerStats.init(s.getImplementation());
    }

}
