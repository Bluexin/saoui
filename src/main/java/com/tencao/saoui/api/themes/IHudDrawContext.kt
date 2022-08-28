package com.tencao.saoui.api.themes

import com.tencao.saoui.effects.StatusEffects
import com.tencao.saoui.screens.util.HealthStep
import net.minecraft.client.entity.player.ClientPlayerEntity
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.renderer.ItemRenderer
import net.minecraft.entity.LivingEntity

/**
 * Getters to use in JEL (for access in xml themes).
 *
 *
 * These are accessible in the HUD xml.
 *
 * Everywhere "percent" or "scale of 1" is mentioned, it means the value will be a decimal ranging from 0.0 (included)
 * to 1.0 (included).
 *
 * @author Bluexin
 */
// Used to get access in JEL
interface IHudDrawContext {
    /**
     * @return the player's username
     */
    fun username(): String = player?.scoreboardName ?: ""

    /**
     * @return the width of the player's username (in pixel, with the current font)
     */
    fun usernamewidth(): Double

    /**
     * @return the current hp of the player, in percentage of 1
     */
    fun hpPct(): Double

    /**
     * @return the current hp of the player (1 heart = 2 HP)
     */
    fun hp(): Float

    /**
     * @return the current maximum hp of the player (1 heart = 2 HP)
     */
    fun maxHp(): Float

    /**
     * @return the health step the player is currently at
     */
    fun healthStep(): HealthStep

    /**
     * @return the id of the hotbar slot the player is using (from 0 to 8)
     */
    fun selectedslot(): Int

    /**
     * @return screen width, scaled
     */
    fun scaledwidth(): Int

    /**
     * @return screen heigth, scaled
     */
    fun scaledheight(): Int

    /**
     * Used to know whether the specified offhand slot is empty.
     * Currently there is only 1 offhand slot.
     *
     * @param slot offhand slot to query
     * @return whether the specified offhand slot is empty
     */
    fun offhandEmpty(slot: Int): Boolean

    /**
     * Used to get the width in pixels of the string with the current fontrenderer.
     *
     * @param s the string to query the width of
     * @return width in pixels of the provided string with current fontrenderer
     */
    fun strWidth(s: String): Int

    /**
     * Used to get the height in pixels of the font with the current fontrenderer.
     *
     * @return height in pixels of the current font with the fontrenderer
     */
    fun strHeight(): Int

    /**
     * @return the current absorption amount the player has
     */
    fun absorption(): Float

    /**
     * @return the current experience level of the player
     */
    fun level(): Int

    /**
     * @return the current experience percent of the player (scale of 1)
     */
    fun experience(): Float

    /**
     * @return z value. Shouldn't be needed in theme
     */
    val z: Float

    /**
     * @return current font renderer. Useless in themes for now
     */
    val fontRenderer: FontRenderer

    /**
     * @return current item renderer. Useless in themes for now
     */
    val itemRenderer: ItemRenderer

    /**
     * @return current player. Useless in themes for now
     */
    val player: ClientPlayerEntity?

    /**
     * @return partial ticks
     */
    val partialTicks: Float

    /**
     * @return horse jump value (on a scale of 1)
     */
    fun horsejump(): Float

    /**
     * Internal
     */
    var i: Int

    /**
     * @return index in repetition groups
     */
    fun i(): Int = i

    /**
     * @param index the index of the party member to check
     * @return username of the party member at given index
     */
    fun ptName(index: Int): String

    /**
     * @param index the index of the party member to check
     * @return hp of the party member at given index
     */
    fun ptHp(index: Int): Float

    /**
     * @param index the index of the party member to check
     * @return max hp of the party member at given index
     */
    fun ptMaxHp(index: Int): Float

    /**
     * @param index the index of the party member to check
     * @return hp percent of the party member at given index
     */
    fun ptHpPct(index: Int): Float

    /**
     * @param index the index of the party member to check
     * @return health step of the party member at given index
     */
    fun ptHealthStep(index: Int): HealthStep

    /**
     * @return current party size
     */
    fun ptSize(): Int

    /**
     * @return player food level
     */
    fun foodLevel(): Float

    /**
     * @return player food max value
     */
    fun foodMax(): Float {
        return 20.0f
    }

    /**
     * @return player food percentage
     */
    fun foodPct(): Float {
        return (foodLevel() / foodMax()).coerceAtMost(1.0f)
    }

    /**
     * @return player saturation level
     */
    fun saturationLevel(): Float

    /**
     * @return player saturation max value
     */
    fun saturationMax(): Float {
        return 20.0f
    }

    /**
     * @return player saturation percentage
     */
    fun saturationPct(): Float {
        return (saturationLevel() / saturationMax()).coerceAtMost(1.0f)
    }

    /**
     * @return player's current status effects
     */
    fun statusEffects(): List<StatusEffects>

    /**
     * @param i index to check
     * @return status effect at given index
     */
    fun statusEffect(i: Int): StatusEffects? {
        return statusEffects()[i]
    }

    /**
     * @return whether the current player is riding an entity
     */
    fun hasMount(): Boolean

    /**
     * @return current mount hp (or 0 if none)
     */
    fun mountHp(): Float

    /**
     * @return mount max hp (or 1 if none)
     */
    fun mountMaxHp(): Float

    /**
     * @return mount hp percentage (or 1 if none)
     */
    fun mountHpPct(): Float {
        return if (hasMount()) (mountHp() / mountMaxHp()).coerceAtMost(1.0f) else 1.0f
    }

    /**
     * @return whether the player is under water
     */
    fun inWater(): Boolean

    /**
     * @return current air level
     */
    fun air(): Int

    /**
     * @return max air level
     */
    fun airMax(): Int {
        return 300
    }

    /**
     * @return air level percentage
     */
    fun airPct(): Float {
        return (air() / airMax().toFloat()).coerceAtMost(1.0f)
    }

    /**
     * @return armor value
     */
    fun armor(): Int
    // *** NEARBY ENTITIES ***
    /**
     * @return up to 5 nearby entities
     */
    fun nearbyEntities(): List<LivingEntity>

    /**
     * @param i index to check
     * @return Entity at given index
     */
    fun nearbyEntity(i: Int): LivingEntity? {
        return nearbyEntities()[i]
    }

    /**
     * @return nearby entity amount
     */
    fun nearbyEntitySize(): Int {
        return nearbyEntities().size
    }

    /**
     * @param index the index of the entity to check
     * @return username of the entity at given index
     */
    fun entityName(index: Int): String

    /**
     * @param index the index of the entity to check
     * @return hp of the entity at given index
     */
    fun entityHp(index: Int): Float

    /**
     * @param index the index of the entity to check
     * @return max hp of the entity at given index
     */
    fun entityMaxHp(index: Int): Float

    /**
     * @param index the index of the entity to check
     * @return hp percent of the entity at given index
     */
    fun entityHpPct(index: Int): Float

    /**
     * @param index the index of the entity to check
     * @return health step of the entity at given index
     */
    fun entityHealthStep(index: Int): HealthStep

    // *** TARGET ENTITY ***
    fun targetEntity(): LivingEntity?

    /**
     * @return username of the target
     */
    fun targetName(): String

    /**
     * @return hp of the target
     */
    fun targetHp(): Float

    /**
     * @return max hp of the target
     */
    fun targetMaxHp(): Float

    /**
     * @return hp percent of the target
     */
    fun targetHpPct(): Float

    /**
     * @return health step of the target
     */
    fun targetHealthStep(): HealthStep
}
