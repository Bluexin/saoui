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
package com.saomc.saoui.themes.util

import be.bluexin.saomclib.party.PlayerInfo
import com.saomc.saoui.api.info.IPlayerStatsProvider
import com.saomc.saoui.api.themes.IHudDrawContext
import com.saomc.saoui.capabilities.getRenderData
import com.saomc.saoui.effects.StatusEffects
import com.saomc.saoui.effects.StatusEffects.Companion.getEffects
import com.saomc.saoui.screens.ingame.HealthStep
import com.saomc.saoui.screens.ingame.HealthStep.Companion.getStep
import com.saomc.saoui.social.StaticPlayerHelper.getHungerLevel
import com.saomc.saoui.social.StaticPlayerHelper.getMaxHealth
import com.saomc.saoui.util.PlayerStats.Companion.instance
import net.minecraft.block.material.Material
import net.minecraft.client.Minecraft
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.RenderItem
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraftforge.common.ForgeHooks
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import kotlin.math.min

/**
 * Part of saoui by Bluexin.
 * Provides extra info for what's about to be drawn.
 *
 * @author Bluexin
 */
@SideOnly(Side.CLIENT)
class HudDrawContext(player: EntityPlayer, val mc: Minecraft, itemRenderer: RenderItem) : IHudDrawContext {
    /*
    Feel free to add anything you'd need here.
     */
    private val username: String = player.displayNameString
    private val itemRenderer: RenderItem
    private val usernameWidth: Double = (1 + (mc.fontRenderer.getStringWidth(username) + 4) / 5.0) * 5
    private val stats: IPlayerStatsProvider = instance().stats
    private var player: EntityPlayer
    private var healthStep: HealthStep? = null
    private var z = 0f
    private var hp = 0f
    private var maxHp = 0f
    private var scaledResolution: ScaledResolution? = null
    private var partialTicks = 0f
    private var i = 0
    private var pt: List<PlayerInfo> = listOf()
    private var effects: List<StatusEffects>? = null
    private var nearbyEntities: List<EntityLivingBase> = listOf()
    private var targetEntity: EntityLivingBase? = null
    fun setPt(pt: List<PlayerInfo>) {
        this.pt = pt
    }

    fun setTargetEntity(entity: EntityLivingBase) {
        this.targetEntity = entity
    }

    fun setNearbyEntities(entities: List<EntityLivingBase>) {
        this.nearbyEntities = entities
    }

    override fun username(): String {
        return username
    }

    override fun getZ(): Float {
        return z
    }

    fun setZ(z: Float) {
        this.z = z
    }

    override fun getFontRenderer(): FontRenderer {
        return mc.fontRenderer
    }

    override fun usernamewidth(): Double {
        return usernameWidth
    }

    override fun hpPct(): Double {
        return min(hp / maxHp.toDouble(), 1.0)
    }

    override fun hp(): Float {
        return hp
    }

    override fun maxHp(): Float {
        return maxHp
    }

    /**
     * Aka partialTicks
     */
    fun setTime(time: Float) {
        hp = player.getRenderData()!!.healthSmooth
        maxHp = getMaxHealth(player)
        healthStep = getStep(player, hpPct())
        partialTicks = time
        effects = getEffects(player)
    }

    override fun getPlayer(): EntityPlayer {
        return player
    }

    fun setPlayer(player: EntityPlayer) {
        this.player = player
    }

    override fun getItemRenderer(): RenderItem {
        return itemRenderer
    }

    override fun healthStep(): HealthStep {
        return healthStep!!
    }

    override fun selectedslot(): Int {
        return player.inventory.currentItem
    }

    override fun scaledwidth(): Int {
        return scaledResolution!!.scaledWidth
    }

    override fun scaledheight(): Int {
        return scaledResolution!!.scaledHeight
    }

    fun setScaledResolution(scaledResolution: ScaledResolution?) {
        this.scaledResolution = scaledResolution
    }

    override fun getPartialTicks(): Float {
        return partialTicks
    }

    override fun offhandEmpty(slot: Int): Boolean {
        return player.inventory.offHandInventory[0].isEmpty
    }

    override fun strWidth(s: String): Int {
        return mc.fontRenderer.getStringWidth(s)
    }

    override fun strHeight(): Int {
        return mc.fontRenderer.FONT_HEIGHT
    }

    override fun absorption(): Float {
        return player.absorptionAmount
    }

    override fun level(): Int {
        return stats.getLevel(player)
    }

    override fun experience(): Float {
        return stats.getExpPct(player)
    }

    override fun horsejump(): Float {
        return (player as EntityPlayerSP).horseJumpPower
    }

    fun getI(): Int {
        return i
    }

    override fun setI(i: Int) {
        this.i = i
    }

    override fun i(): Int {
        return getI()
    }

    override fun ptName(index: Int): String {
        return pt[index].username
    }

    override fun ptHp(index: Int): Float {
        return pt[index].let {
            it.player?.getRenderData()?.healthSmooth?: it.health
        }
    }

    override fun ptMaxHp(index: Int): Float {
        return pt[index].maxHealth
    }

    override fun ptHpPct(index: Int): Float {
        return ptHp(index) / ptMaxHp(index)
    }

    override fun ptSize(): Int {
        return pt.size
    }

    override fun ptHealthStep(index: Int): HealthStep {
        val ptMember = pt[index]
        return getStep(ptMember, ptHpPct(index).toDouble())
    }

    override fun foodLevel(): Float {
        return getHungerLevel(mc, player, partialTicks)
    }

    override fun saturationLevel(): Float {
        return player.foodStats.saturationLevel
    }

    override fun statusEffects(): List<StatusEffects> {
        return effects!!
    }

    override fun hasMount(): Boolean {
        return player.isRiding
    }

    override fun mountHp(): Float {
        val t = player.ridingEntity
        return if (t is EntityLivingBase) {
            t.health
        } else 0f
    }

    override fun mountMaxHp(): Float {
        val t = player.ridingEntity
        return if (t is EntityLivingBase) {
            t.maxHealth
        } else 1f
    }

    override fun inWater(): Boolean {
        return player.isInsideOfMaterial(Material.WATER)
    }

    override fun air(): Int {
        return player.air
    }

    override fun armor(): Int {
        return ForgeHooks.getTotalArmorValue(player)
    }

    override fun nearbyEntities(): List<EntityLivingBase> {
        return nearbyEntities
    }

    override fun entityName(index: Int): String = nearbyEntities[index].displayName.formattedText

    override fun entityHp(index: Int): Float = nearbyEntities[index].health

    override fun entityMaxHp(index: Int): Float = nearbyEntities[index].maxHealth

    override fun entityHpPct(index: Int): Float = entityHp(index) / entityMaxHp(index)

    override fun entityHealthStep(index: Int): HealthStep = getStep(nearbyEntity(index), entityHpPct(index).toDouble())

    override fun targetEntity(): EntityLivingBase? = targetEntity

    override fun targetName(): String = targetEntity?.displayName?.formattedText?: ""

    override fun targetHp(): Float = targetEntity?.health?: 0F

    override fun targetMaxHp(): Float = targetEntity?.maxHealth?: 0F

    override fun targetHpPct(): Float = if (targetEntity != null) targetHp() / targetMaxHp() else 0f

    override fun targetHealthStep(): HealthStep = getStep(targetEntity, targetHpPct().toDouble())

    init {
        this.player = player
        this.itemRenderer = itemRenderer
    }
}