package com.saomc.saoui;

import com.saomc.saoui.api.events.EventInitStatsProvider;
import com.saomc.saoui.config.ConfigHandler;
import com.saomc.saoui.events.EventCore;
import com.saomc.saoui.themes.ThemeLoader;
import com.saomc.saoui.util.DefaultStatsProvider;
import com.saomc.saoui.util.PlayerStats;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import javax.xml.bind.JAXBException;

@SuppressWarnings("MethodCallSideOnly")
@Mod(modid = SAOCore.MODID, name = SAOCore.NAME, version = SAOCore.VERSION, clientSideOnly = true, dependencies = SAOCore.DEPS, acceptableSaveVersions = "*", canBeDeactivated = true)
public class SAOCore {
    public static final String MODID = "saoui";
    public static final String NAME = "Sword Art Online UI";
    public static final String VERSION = "2.0-lite-dev";
    public static final String DEPS = "required-before:" + MODID + "ntw;required-after:saomclib@[1.1,)";
    public static final float UNKNOWN_TIME_DELAY = -1F;
    // TODO: optimize things, ie remove public and static!

    @Mod.Instance(MODID)
    public static SAOCore instance;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) throws Exception {
        MinecraftForge.EVENT_BUS.register(new EventCore());
        ConfigHandler.preInit(event);

        ((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(resourceManager -> {
            try {
                ThemeLoader.load();
            } catch (JAXBException e) {
                e.printStackTrace();
            }
        });
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        final EventInitStatsProvider s = new EventInitStatsProvider(new DefaultStatsProvider());
        MinecraftForge.EVENT_BUS.post(s);
        PlayerStats.init(s.getImplementation());
    }

}
