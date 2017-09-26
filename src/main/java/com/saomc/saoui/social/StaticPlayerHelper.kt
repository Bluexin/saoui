package com.saomc.saoui.social

import be.bluexin.saomclib.capabilities.PartyCapability
import be.bluexin.saomclib.capabilities.getPartyCapability
import be.bluexin.saomclib.party.IParty
import com.saomc.saoui.config.OptionCore
import net.minecraft.client.Minecraft
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import java.util.*

/**
 * Part of SAOUI

 * @author Bluexin
 */
object StaticPlayerHelper {
    private val HEALTH_ANIMATION_FACTOR = 0.075f
    private val HEALTH_FRAME_FACTOR = HEALTH_ANIMATION_FACTOR * HEALTH_ANIMATION_FACTOR * 0x40f * 0x64f
    private val healthSmooth = HashMap<UUID, Float>()
    private val hungerSmooth = HashMap<UUID, Float>()

    fun listOnlinePlayers(mc: Minecraft, range: Double): List<EntityPlayer> {

        return mc.world.getPlayers(EntityPlayer::class.java) { p -> mc.player.getDistanceToEntity(p) <= range }
    }

    private fun listOnlinePlayers(mc: Minecraft): List<EntityPlayer> {
        return mc.world.playerEntities
    }

    fun findOnlinePlayer(mc: Minecraft, username: String): EntityPlayer? {
        return mc.world.getPlayerEntityByName(username)
    }

    private fun isOnline(mc: Minecraft, names: Array<String>): BooleanArray { // TODO: update a boolean[] upon player join server? (/!\ client-side)
        val players = listOnlinePlayers(mc)
        val online = BooleanArray(names.size)

        for (i in names.indices) {
            val index = i
            online[i] = players.stream().anyMatch { player -> getName(player) == names[index] }
        }

        return online
    }

    fun isOnline(mc: Minecraft, name: String): Boolean {
        return isOnline(mc, arrayOf(name))[0]
    }

    fun getName(player: EntityPlayer?): String {
        return if (player == null) "" else player.displayNameString
    }

    fun getName(mc: Minecraft): String {
        return getName(mc.player)
    }

    fun unformatName(name: String): String {
        var name = name
        var index = name.indexOf("�")

        while (index != -1) {
            if (index + 1 < name.length)
                name = name.replace(name.substring(index, index + 2), "")
            else
                name = name.replace("�", "")

            index = name.indexOf("�")
        }

        return name
    }

    fun getHealth(mc: Minecraft, entity: Entity, time: Float): Float { // FIXME: this seems to break if called many times in a single render frame
        if (OptionCore.SMOOTH_HEALTH.isEnabled) {
            val healthReal: Float
            val uuid = entity.uniqueID

            if (entity is EntityLivingBase)
                healthReal = entity.health
            else
                healthReal = if (entity.isDead) 0f else 1f

            if (healthSmooth.containsKey(uuid)) {
                var healthValue: Float = healthSmooth[uuid]!!
                if (healthValue > healthReal) {
                    healthValue = healthReal
                    healthSmooth.put(uuid, healthReal)
                }

                if (healthReal <= 0 && entity is EntityLivingBase) {
                    val value = (18 - entity.deathTime).toFloat() / 18

                    if (value <= 0) healthSmooth.remove(uuid)

                    return Math.max(0.0f, healthValue * value)
                } else if (Math.round(healthValue * 10) != Math.round(healthReal * 10))
                    healthValue += (healthReal - healthValue) * (gameTimeDelay(mc, time) * HEALTH_ANIMATION_FACTOR)
                else
                    healthValue = healthReal

                healthSmooth.put(uuid, healthValue)
                return Math.max(0.0f, healthValue)
            } else {
                healthSmooth.put(uuid, healthReal)
                return Math.max(0.0f, healthReal)
            }
        } else
            return if (entity is EntityLivingBase) Math.max(0.0f, entity.health) else if (entity.isDead) 0f else 1f
    }

    fun getMaxHealth(entity: Entity): Float {
        return if (entity is EntityLivingBase) Math.max(0.0000001f, entity.maxHealth) else 1f
    }

    fun getHungerFract(mc: Minecraft, entity: Entity, time: Float) =
            if (entity !is EntityPlayer) 1.0f
            else getHungerLevel(mc, entity, time) / 20.0f

    fun getHungerLevel(mc: Minecraft, entity: Entity, time: Float): Float {
        if (entity !is EntityPlayer) return 1.0f
        val player = entity
        val hungerReal: Float
        if (OptionCore.SMOOTH_HEALTH.isEnabled) {
            val uuid = entity.getUniqueID()

            hungerReal = player.foodStats.foodLevel.toFloat()

            if (hungerSmooth.containsKey(uuid)) {
                var hungerValue: Float = hungerSmooth[uuid]!!
                if (hungerValue > hungerReal) {
                    hungerValue = hungerReal
                    hungerSmooth.put(uuid, hungerReal)
                }

                if (hungerReal <= 0) {
                    val value = (18 - player.deathTime).toFloat() / 18
                    if (value <= 0) hungerSmooth.remove(uuid)

                    return hungerValue * value
                } else if (Math.round(hungerValue * 10) != Math.round(hungerReal * 10))
                    hungerValue += (hungerReal - hungerValue) * (gameTimeDelay(mc, time) * HEALTH_ANIMATION_FACTOR)
                else hungerValue = hungerReal

                hungerSmooth.put(uuid, hungerValue)
                return hungerValue
            } else {
                hungerSmooth.put(uuid, hungerReal)
                return hungerReal
            }
        } else return player.foodStats.foodLevel.toFloat()
    }

    private fun gameTimeDelay(mc: Minecraft, time: Float): Float {
        return if (time >= 0f) time else HEALTH_FRAME_FACTOR / gameFPS(mc)
    }

    fun isCreative(player: EntityPlayer): Boolean { // TODO: test this!
        return player.capabilities.isCreativeMode
    }

    private fun gameFPS(mc: Minecraft): Int {
        return mc.limitFramerate
    }

    fun thePlayer(): EntityPlayer {
        return Minecraft.getMinecraft().player
    }

    fun getIParty(): IParty {
        return Minecraft.getMinecraft().player.getPartyCapability().getOrCreatePT()
    }

    fun getIParty(player: EntityPlayer): IParty {
        return player.getPartyCapability().getOrCreatePT()
    }
}
