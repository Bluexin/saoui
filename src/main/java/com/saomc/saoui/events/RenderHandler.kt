package com.saomc.saoui.events

import be.bluexin.saomclib.player
import com.saomc.saoui.SoundCore
import com.saomc.saoui.config.OptionCore
import com.saomc.saoui.neo.screens.NeoGui
import com.saomc.saoui.neo.screens.NeoIngameMenu
import com.saomc.saoui.renders.StaticRenderer
import com.saomc.saoui.screens.ingame.IngameGUI
import com.saomc.saoui.screens.menu.StartupGUI
import cpw.mods.fml.relauncher.Side
import cpw.mods.fml.relauncher.SideOnly
import net.minecraft.client.gui.GuiIngameMenu
import net.minecraft.client.gui.GuiMainMenu
import net.minecraft.client.gui.inventory.GuiContainerCreative
import net.minecraft.client.gui.inventory.GuiInventory
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.entity.EntityLivingBase
import net.minecraftforge.client.event.GuiOpenEvent
import net.minecraftforge.client.event.RenderLivingEvent
import net.minecraftforge.client.event.RenderPlayerEvent
import java.util.*

@SideOnly(Side.CLIENT)
internal object RenderHandler {

    private val deadHandlers = ArrayList<EntityLivingBase>()
    private var menuGUI = true

    fun checkingameGUI() {
        if (EventCore.mc.ingameGUI != null && EventCore.mc.ingameGUI !is IngameGUI)
            EventCore.mc.ingameGUI = IngameGUI(EventCore.mc)
    }

    fun deathHandlers() {
        deadHandlers.forEach { ent ->
            val deadStart = ent.deathTime == 1
            val deadExactly = ent.deathTime >= 18
            if (deadStart) {
                ent.deathTime++
                SoundCore.playAtEntity(ent, SoundCore.PARTICLES_DEATH)
            }

            if (deadExactly) {
                StaticRenderer.doSpawnDeathParticles(EventCore.mc, ent)
//                ent.setDead()
            }
        }
        deadHandlers.removeIf { ent -> ent.deathTime >= 18 }
    }

    fun addDeadMob(ent: EntityLivingBase) {
        deadHandlers.add(ent)
    }

    fun guiInstance(e: GuiOpenEvent) {
        if (!OptionCore.BUGGY_MENU.isEnabled) return

        if (e.gui is GuiIngameMenu) {
            if (OptionCore.NEO_MENU.isEnabled) {
                if (EventCore.mc.currentScreen !is NeoGui<*>) e.gui = NeoIngameMenu()
            }
        }
        if (e.gui is GuiInventory && !OptionCore.DEFAULT_INVENTORY.isEnabled) {
            when {
                EventCore.mc.playerController.isInCreativeMode -> e.gui = GuiContainerCreative(EventCore.mc.player)
                else -> e.isCanceled = true
            }
        }
        /*if (e.getGui() instanceof GuiGameOver && (!OptionCore.DEFAULT_DEATH_SCREEN.isEnabled())) {
            if (!(e.getGui() instanceof DeathScreen)) {
                e.setGui(new DeathScreen());
            }
        }*/
    }

    fun deathCheck() {
        /*
        if (EventCore.mc.currentScreen instanceof DeathScreen && EventCore.mc.player.getHealth() > 0.0F) {
            EventCore.mc.currentScreen.onGuiClosed();
            EventCore.mc.setIngameFocus();
        }*/
    }

    fun renderPlayer(e: RenderPlayerEvent.Post) {
        if (!OptionCore.UI_ONLY.isEnabled) {
            if (e.entityPlayer != null) {
                StaticRenderer.render(RenderManager.instance, e.entityPlayer, e.entityPlayer.posX, e.entityPlayer.posY, e.entityPlayer.posZ)
            }
        }
    }

    fun renderEntity(e: RenderLivingEvent.Post) {
        if (!OptionCore.UI_ONLY.isEnabled) {
            if (e.entity !== EventCore.mc.player) {
                StaticRenderer.render(RenderManager.instance, e.entity, e.x, e.y, e.z)
            }
        }
    }

    fun mainMenuGUI(e: GuiOpenEvent) {
        if (menuGUI)
            if (e.gui is GuiMainMenu)
                if (StartupGUI.shouldShow()) {
                    e.gui = StartupGUI()
                    menuGUI = false
                } //else e.setGui(new MainMenuGUI());
    }

}
