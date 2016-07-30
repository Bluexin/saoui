package com.saomc;

import com.saomc.api.events.EventInitParty;
import com.saomc.api.events.EventInitSkills;
import com.saomc.api.events.EventInitStatsProvider;
import com.saomc.events.ConfigHandler;
import com.saomc.events.EventCore;
import com.saomc.screens.window.Window;
import com.saomc.screens.window.WindowView;
import com.saomc.social.friends.FriendsHandler;
import com.saomc.social.party.DefaultParty;
import com.saomc.social.party.PartyHelper;
import com.saomc.util.*;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.util.ArrayList;
import java.util.Arrays;

@Mod(modid = SAOCore.MODID, name = SAOCore.NAME, version = SAOCore.VERSION, clientSideOnly = true)
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
        final EventInitSkills sk = new EventInitSkills(new ArrayList<>(Arrays.asList(DefaultSkills.values())));
        MinecraftForge.EVENT_BUS.post(sk);
        SkillList.init(sk.getSkills(), sk.isRingShown());
    }

    @SuppressWarnings("unchecked")
    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent evt) {
        final Minecraft mc = Minecraft.getMinecraft();

        GLCore.setFont(mc, OptionCore.CUSTOM_FONT.getValue());
//        FMLInterModComms.sendRuntimeMessage(SAOCore.MODID, "VersionChecker", "addVersionCheck", "https://gitlab.com/saomc/PublicVersions/raw/master/saoui1.8ver.json"); // Not used for now, should be switched to forge's
    }

}
