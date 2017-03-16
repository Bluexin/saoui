package com.saomc.saoui.events;

import com.saomc.saoui.SoundCore;
import com.saomc.saoui.config.OptionCore;
import com.saomc.saoui.neo.screens.IngameMenuGUI;
import com.saomc.saoui.renders.StaticRenderer;
import com.saomc.saoui.screens.death.DeathScreen;
import com.saomc.saoui.screens.ingame.IngameGUI;
import com.saomc.saoui.screens.menu.StartupGUI;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;

import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
class RenderHandler {

    private static final List<EntityLivingBase> deadHandlers = new ArrayList<>();
    private static boolean menuGUI = true;

    static void checkingameGUI() {
        if (EventCore.mc.ingameGUI != null && !(EventCore.mc.ingameGUI instanceof IngameGUI))
            EventCore.mc.ingameGUI = new IngameGUI(EventCore.mc);
    }

    static void deathHandlers() {
        deadHandlers.forEach(ent -> {
            if (ent != null) {
                final boolean deadStart = (ent.deathTime == 1);
                final boolean deadExactly = (ent.deathTime >= 18);
                if (deadStart) {
                    ent.deathTime++;
                    SoundCore.playAtEntity(ent, SoundCore.PARTICLES_DEATH);
                }

                if (deadExactly) {
                    StaticRenderer.doSpawnDeathParticles(EventCore.mc, ent);
                    ent.setDead();
                }
            }
        });
        deadHandlers.removeIf(ent -> ent.isDead);
    }

    static void guiInstance(GuiOpenEvent e) {
        if (!OptionCore.BUGGY_MENU.isEnabled()) return;
        if (OptionCore.DEBUG_MODE.isEnabled()) System.out.print(e.gui + " called GuiOpenEvent \n");

        if (e.gui instanceof GuiIngameMenu) {
            if (!(EventCore.mc.currentScreen instanceof IngameMenuGUI)) {
                e.gui = new IngameMenuGUI();
            }
        }
        if (e.gui instanceof GuiInventory && !OptionCore.DEFAULT_INVENTORY.isEnabled()) {
            if (EventCore.mc.playerController.isInCreativeMode())
                e.gui = new GuiContainerCreative(EventCore.mc.thePlayer);
            else if (!(EventCore.mc.currentScreen instanceof IngameMenuGUI))
                e.gui = new IngameMenuGUI(/*(GuiInventory) EventCore.mc.currentScreen*/);
            else e.setCanceled(true);
        }
        if (e.gui instanceof GuiGameOver && (!OptionCore.DEFAULT_DEATH_SCREEN.isEnabled())) {
            if (!(e.gui instanceof DeathScreen)) {
                e.gui = new DeathScreen();
            }
        }
        if (e.gui instanceof IngameMenuGUI)
            if (EventCore.mc.currentScreen instanceof GuiOptions) {
                e.setCanceled(true);
                EventCore.mc.currentScreen.onGuiClosed();
                EventCore.mc.setIngameFocus();
            }

    }

    static void deathCheck() {
        if (EventCore.mc.currentScreen instanceof DeathScreen && EventCore.mc.thePlayer.getHealth() > 0.0F) {
            EventCore.mc.currentScreen.onGuiClosed();
            EventCore.mc.setIngameFocus();
        }
    }

    static void renderPlayer(RenderPlayerEvent.Post e) {
        if (!OptionCore.UI_ONLY.isEnabled()) {
            if (e.entityPlayer != null) {
                StaticRenderer.render(RenderManager.instance, e.entityPlayer, e.entityPlayer.posX, e.entityPlayer.posY, e.entityPlayer.posZ);
            }
        }
    }

    static void renderEntity(RenderLivingEvent.Post e) {
        if (!OptionCore.UI_ONLY.isEnabled()) {
            if (e.entity != EventCore.mc.thePlayer) {
                StaticRenderer.render(RenderManager.instance, e.entity, e.x, e.y, e.z);
            }
        }
    }

    static void mainMenuGUI(GuiOpenEvent e) {
        if (menuGUI)
            if (e.gui instanceof GuiMainMenu)
                if (StartupGUI.shouldShow()) {
                    e.gui = new StartupGUI();
                    menuGUI = false;
                } //else e.gui = new MainMenuGUI());
    }

}
