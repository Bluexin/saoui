/*
 * Copyright (C) 2016-2019 Arnaud 'Bluexin' Solé
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.tencao.saoui.themes.util

import com.tencao.saomclib.Client
import com.tencao.saomclib.GLCore
import com.tencao.saomclib.party.PlayerInfo
import com.tencao.saoui.api.info.IPlayerStatsProvider
import com.tencao.saoui.api.themes.IHudDrawContext
import com.tencao.saoui.capabilities.getRenderData
import com.tencao.saoui.effects.StatusEffects
import com.tencao.saoui.effects.StatusEffects.Companion.getEffects
import com.tencao.saoui.screens.util.HealthStep
import com.tencao.saoui.screens.util.HealthStep.Companion.getStep
import com.tencao.saoui.social.StaticPlayerHelper.getHungerLevel
import com.tencao.saoui.social.StaticPlayerHelper.getMaxHealth
import com.tencao.saoui.util.PlayerStats.Companion.instance
import net.minecraft.client.Minecraft
import net.minecraft.client.entity.player.ClientPlayerEntity
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.renderer.ItemRenderer
import net.minecraft.entity.LivingEntity
import kotlin.math.min

/**
 * Part of saoui by Bluexin.
 * Provides extra info for what's about to be drawn.
 *
 * @author Bluexin
 */
open class HudDrawContext :
    IHudDrawContext {
    /*
    Feel free to add anything you'd need here.
     */
    val mc: Minecraft = Client.minecraft
    override val fontRenderer: FontRenderer get() = GLCore.glFont
    override val itemRenderer: ItemRenderer get() = mc.itemRenderer
    override val player: ClientPlayerEntity? get() = Client.player
    private val stats: IPlayerStatsProvider inline get() = instance().stats
    override var z = 0f
    private var hp = 0f
    private var maxHp = 0f
    override var partialTicks = 0f
    override var i = 0
    private var pt: List<PlayerInfo> = listOf()
    private var nearbyEntities: List<LivingEntity> = listOf()
    private var targetEntity: LivingEntity? = null

    fun setPt(pt: List<PlayerInfo>) {
        this.pt = pt
    }

    fun setTargetEntity(entity: LivingEntity) {
        this.targetEntity = entity
    }

    fun setNearbyEntities(entities: List<LivingEntity>) {
        this.nearbyEntities = entities
    }

    override fun username(): String = player?.scoreboardName ?: ""

    override fun usernamewidth(): Double = (1 + (mc.fontRenderer.getStringWidth(username()) + 4) / 5.0) * 5

    override fun hpPct(): Double = min(hp / maxHp.toDouble(), 1.0)

    override fun hp(): Float = hp

    override fun maxHp(): Float = maxHp

    /**
     * Aka partialTicks
     */
    fun setTime(time: Float) {
        if (player != null) {
            hp = player?.getRenderData()?.healthSmooth ?: hp
            maxHp = getMaxHealth(player)
        }
        partialTicks = time
    }

    override fun healthStep(): HealthStep = getStep(player, hpPct())

    override fun selectedslot(): Int = player?.inventory?.currentItem ?: 0

    override fun scaledwidth(): Int = Client.minecraft.mainWindow.scaledWidth

    override fun scaledheight(): Int = Client.minecraft.mainWindow.scaledHeight

    override fun offhandEmpty(slot: Int): Boolean = player?.inventory?.offHandInventory?.get(0)?.isEmpty != false

    override fun strWidth(s: String): Int = mc.fontRenderer.getStringWidth(s)

    override fun strHeight(): Int = mc.fontRenderer.FONT_HEIGHT

    override fun absorption(): Float = player?.absorptionAmount ?: 0f

    override fun level(): Int = stats.getLevel(player)

    override fun experience(): Float = stats.getExpPct(player)

    override fun horsejump(): Float = player?.horseJumpPower ?: 0f

    override fun ptName(index: Int): String = pt[index].username

    override fun ptHp(index: Int): Float = pt[index].let {
        it.player?.getRenderData()?.healthSmooth ?: it.health
    }

    override fun ptMaxHp(index: Int): Float = pt[index].maxHealth

    override fun ptHpPct(index: Int): Float = ptHp(index) / ptMaxHp(index)

    override fun ptSize(): Int = pt.size

    override fun ptHealthStep(index: Int): HealthStep {
        val ptMember = pt[index]
        return getStep(ptMember, ptHpPct(index).toDouble())
    }

    override fun foodLevel(): Float = getHungerLevel(mc, player, partialTicks)

    override fun saturationLevel(): Float = player?.foodStats?.saturationLevel ?: 0f

    override fun statusEffects(): List<StatusEffects> {
        return if (player != null) {
            getEffects(player!!)
        } else emptyList()
    }

    override fun hasMount(): Boolean = player?.ridingEntity != null

    override fun mountHp(): Float {
        val t = player?.ridingEntity
        return if (t is LivingEntity) {
            t.health
        } else 0f
    }

    override fun mountMaxHp(): Float {
        val t = player?.ridingEntity
        return if (t is LivingEntity) {
            t.maxHealth
        } else 1f
    }

    override fun inWater(): Boolean = player?.isInWater ?: false

    override fun air(): Int = player?.air ?: 0

    override fun armor(): Int = player?.totalArmorValue ?: 0

    override fun nearbyEntities(): List<LivingEntity> = nearbyEntities

    override fun entityName(index: Int): String = nearbyEntities[index].displayName.getStringTruncated(20)

    override fun entityHp(index: Int): Float = nearbyEntities[index].health

    override fun entityMaxHp(index: Int): Float = nearbyEntities[index].maxHealth

    override fun entityHpPct(index: Int): Float = entityHp(index) / entityMaxHp(index)

    override fun entityHealthStep(index: Int): HealthStep = getStep(nearbyEntity(index), entityHpPct(index).toDouble())

    override fun targetEntity(): LivingEntity? = targetEntity

    override fun targetName(): String = targetEntity?.displayName?.getStringTruncated(20) ?: ""

    override fun targetHp(): Float = targetEntity?.health ?: 0F

    override fun targetMaxHp(): Float = targetEntity?.maxHealth ?: 0F

    override fun targetHpPct(): Float = if (targetEntity != null) targetHp() / targetMaxHp() else 0f

    override fun targetHealthStep(): HealthStep = getStep(targetEntity, targetHpPct().toDouble())
}
