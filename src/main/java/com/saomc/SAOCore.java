package com.saomc;

import com.saomc.api.events.EventInitParty;
import com.saomc.api.events.EventInitStatsProvider;
import com.saomc.events.ConfigHandler;
import com.saomc.events.EventCore;
import com.saomc.events.FriendsHandler;
import com.saomc.screens.window.Window;
import com.saomc.screens.window.WindowView;
import com.saomc.social.party.DefaultParty;
import com.saomc.social.party.PartyHelper;
import com.saomc.util.DefaultStatsProvider;
import com.saomc.util.OptionCore;
import com.saomc.util.PlayerStats;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = SAOCore.MODID, name = SAOCore.NAME, version = SAOCore.VERSION, clientSideOnly = true)
//@SideOnly(Side.CLIENT)
public class SAOCore {
    public static final String MODID = "saoui";
    public static final String NAME = "Sword Art Online UI";
    public static final String VERSION = "1.9-1.6.0-Dev1";
    public static final float UNKNOWN_TIME_DELAY = -1F;
    // TODO: optimize things, ie remove public and static!

    @Mod.Instance(MODID)
    public static SAOCore instance;

    public static Window getWindow(Minecraft mc) {
        return mc.currentScreen != null && mc.currentScreen instanceof WindowView ? ((WindowView) mc.currentScreen).getWindow() : null;
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new EventCore());

        ConfigHandler.preInit(event);
        FriendsHandler.preInit(event);

//        if (!UpdateChecker.hasChecked()) new UpdateChecker().start(); // Not used for now
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        final EventInitParty e = new EventInitParty(new DefaultParty());
        MinecraftForge.EVENT_BUS.post(e);
        PartyHelper.init(e.getImplementation());
        final EventInitStatsProvider s = new EventInitStatsProvider(new DefaultStatsProvider());
        MinecraftForge.EVENT_BUS.post(s);
        PlayerStats.init(s.getImplementation());
    }

    @SuppressWarnings("unchecked")
    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent evt) {
        final Minecraft mc = Minecraft.getMinecraft();

        GLCore.setFont(mc, OptionCore.CUSTOM_FONT.getValue());
//        FMLInterModComms.sendRuntimeMessage(SAOCore.MODID, "VersionChecker", "addVersionCheck", "https://gitlab.com/saomc/PublicVersions/raw/master/saoui1.8ver.json"); // Not used for now, should be switched to forge's
    }

}
