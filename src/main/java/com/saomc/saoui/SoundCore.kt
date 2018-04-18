package com.saomc.saoui

import com.saomc.saoui.config.OptionCore
import net.minecraft.client.Minecraft
import net.minecraft.client.audio.ISound
import net.minecraft.client.audio.PositionedSoundRecord
import net.minecraft.client.audio.SoundHandler
import net.minecraft.entity.Entity
import net.minecraft.util.ResourceLocation
import net.minecraft.util.SoundCategory

object SoundCore {

    val CONFIRM = "sao.confirm"
    val DIALOG_CLOSE = "sao.dialog.close"
    val MENU_POPUP = "sao.menu.popup"
    val MESSAGE = "sao.message"
    val ORB_DROPDOWN = "sao.orb.dropdown"
    val PARTICLES_DEATH = "sao.particles.death"
    val LOW_HEALTH = "sao.low.health"

    private fun getResource(name: String): ResourceLocation {
        return ResourceLocation(SAOCore.MODID, name)
    }

    fun isSfxPlaying(name: String): Boolean {
        return Minecraft.getMinecraft().soundHandler.isSoundPlaying(create(getResource(name)))
    }

    fun playFromEntity(entity: Entity?, name: String) {
        if (entity != null) {
            playAtEntity(entity, name)
        }
    }

    fun playAtEntity(entity: Entity, name: String) {
        val mc = Minecraft.getMinecraft()

        if (mc.world != null && mc.world.isRemote) {
            play(mc.soundHandler, name, entity.posX.toFloat(), entity.posY.toFloat(), entity.posZ.toFloat())
        }
    }

    fun play(mc: Minecraft?, name: String) {
        if (mc?.world != null && mc.world.isRemote) {
            play(mc.soundHandler, name)
        }
    }

    fun play(handler: SoundHandler?, name: String) {
        if (OptionCore.SOUND_EFFECTS() && handler != null) {
            handler.playSound(create(getResource(name)))
        }
    }

    private fun play(handler: SoundHandler?, name: String, x: Float, y: Float, z: Float) {
        if (OptionCore.SOUND_EFFECTS() && handler != null) {
            handler.playSound(create(getResource(name), x, y, z))
        }
    }

    fun resumeSounds(mc: Minecraft) {
        mc.soundHandler.resumeSounds()
    }

    //Helper functions - 1.8.8 mirror
    private fun create(soundResource: ResourceLocation): PositionedSoundRecord {
        return PositionedSoundRecord(soundResource, SoundCategory.MASTER, 1.0f, 1.0f, false, 0, ISound.AttenuationType.NONE, 0.0f, 0.0f, 0.0f)
    }

    private fun create(soundResource: ResourceLocation, xPosition: Float, yPosition: Float, zPosition: Float): PositionedSoundRecord {
        return PositionedSoundRecord(soundResource, SoundCategory.MASTER, 4.0f, 1.0f, false, 0, ISound.AttenuationType.LINEAR, xPosition, yPosition, zPosition)
    }
}
