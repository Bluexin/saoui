package com.saomc.saoui.events;

import com.saomc.saoui.api.events.ElementAction;
import com.saomc.saoui.effects.RenderDispatcher;
import com.saomc.saoui.social.party.PartyHelper;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.*;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

/**
 * This is the core for all event handlers, listening to events then passing on to the other events that need it.
 */
public class EventCore {

    static final Minecraft mc = Minecraft.getMinecraft();

    @SubscribeEvent
    public void chatListener(ClientChatReceivedEvent e) {
        EventHandler.nameNotification(e);
        EventHandler.chatCommand(e);
    }

    @SubscribeEvent
    public void clientTickListener(TickEvent.ClientTickEvent e) {
        EventHandler.abilityCheck();
    }

    @SubscribeEvent
    public void renderTickListener(TickEvent.RenderTickEvent e) {
        RenderHandler.deathHandlers();
        RenderHandler.deathCheck();
    }

    @SubscribeEvent
    public void onDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent e) {
        EventHandler.cleanTempElements();
        PartyHelper.instance().clean();
    }

    @SubscribeEvent
    public void renderPlayerListener(RenderPlayerEvent.Post e) {
        RenderHandler.renderPlayer(e);
    }

    @SubscribeEvent
    public void renderEntityListener(RenderLivingEvent.Post e) {
        RenderHandler.renderEntity(e);
    }

    @SubscribeEvent
    public void renderWorldListener(RenderWorldLastEvent event) {
        RenderDispatcher.dispatch();
    }

    @SubscribeEvent
    public void guiOpenListener(GuiOpenEvent e) {
        RenderHandler.guiInstance(e);
        RenderHandler.mainMenuGUI(e);
    }

    @SubscribeEvent
    public void guiListener(GuiScreenEvent e) {
        RenderHandler.checkingameGUI();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void elementActionListener(ElementAction e){
        ElementHandler.defaultActions(e);
    }
}
