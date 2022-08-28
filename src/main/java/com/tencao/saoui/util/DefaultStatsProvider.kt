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

package com.tencao.saoui.util

import com.tencao.saoui.api.info.IPlayerStatsProvider
import com.tencao.saoui.config.OptionCore
import net.minecraft.client.resources.I18n
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.ai.attributes.AttributeModifier
import net.minecraft.entity.ai.attributes.Attributes
import net.minecraft.entity.passive.horse.HorseEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.EquipmentSlotType
import java.text.DecimalFormat

/**
 * Part of saoui

 * @author Bluexin
 */
class DefaultStatsProvider : IPlayerStatsProvider {

    private fun attr(attributeValue: Double): Float {
        return (attributeValue * 1000).toInt().toFloat() / 1000
    }

    override fun getStatsString(player: PlayerEntity): List<String> {
        val builder = mutableListOf<String>()
        val mount = player.lowestRidingEntity as LivingEntity?

        if (mount != null && mount != player && OptionCore.MOUNT_STAT_VIEW.isEnabled) {
            val name = mount.name
            val maxHealth = attr(mount.maxHealth.toDouble()).toDouble()
            var health = attr(mount.health.toDouble()).toDouble()
            val resistance = attr(mount.totalArmorValue.toDouble()).toDouble()
            val speed = attr(mount.getAttributeValue(Attributes.MOVEMENT_SPEED)).toDouble()
            val jump: Double
            val df3 = DecimalFormat("0.000")
            val df1 = DecimalFormat("0.0")
            val speedFormated = df3.format(speed)
            health *= 10.0
            health += 0.5
            health /= 10.0
            val healthFormated = df1.format(health)

            builder.add(I18n.format("displayName") + ": $name")
            builder.add(I18n.format("displayHpLong") + ": $healthFormated/$maxHealth")
            builder.add(I18n.format("displayResLong") + ": $resistance")
            builder.add(I18n.format("displaySpdLong") + ": $speedFormated")
            if (mount is HorseEntity) {
                jump = mount.horseJumpStrength
                val jumpFormated = df3.format(jump)
                builder.add(I18n.format("displayJmpLong") + ": $jumpFormated")
            }
        } else {
            val level = PlayerStats.instance().stats.getLevel(player)
            val experience = (PlayerStats.instance().stats.getExpPct(player) * 100).toInt()

            val health = attr(player.health.toDouble())

            val maxHealth = attr(player.getAttributeValue(Attributes.MAX_HEALTH))
            val attackDamage = attr(player.getAttributeValue(Attributes.ATTACK_DAMAGE))
            // final float movementSpeed = attr(player.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getAttributeValue());
            // final float knocbackResistance = attr(player.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).getAttributeValue());

            var itemDamage = 0.0f

            if (!player.heldItemMainhand.isEmpty) {
                val itemAttackMain = player.heldItemMainhand.getAttributeModifiers(EquipmentSlotType.MAINHAND).get(Attributes.ATTACK_DAMAGE)

                itemDamage += itemAttackMain.filterIsInstance<AttributeModifier>().map { value -> value }
                    .filter { mod -> mod.name == "Weapon modifier" }.sumOf { it.amount }.toFloat()
            }
            if (!player.heldItemOffhand.isEmpty) {
                val itemAttackOff = player.heldItemOffhand.getAttributeModifiers(EquipmentSlotType.OFFHAND).get(Attributes.ATTACK_DAMAGE)

                itemDamage += itemAttackOff.filterIsInstance<AttributeModifier>().map { value -> value }
                    .filter { mod -> mod.name == "Weapon modifier" }.sumOf { it.amount }.toFloat()
            }

            val strength = attr((attackDamage + itemDamage).toDouble())
            val agility = attr(player.aiMoveSpeed.toDouble()) * 10
            val resistance = attr(player.totalArmorValue.toDouble())

            builder.add(I18n.format("displayLvLong") + ": $level")
            builder.add(I18n.format("displayXpLong") + ": $experience")
            builder.add(I18n.format("displayHpLong") + ": $health/$maxHealth")
            builder.add(I18n.format("displayStrLong") + ": $strength")
            builder.add(I18n.format("displayDexLong") + ": $agility")
            builder.add(I18n.format("displayResLong") + ": $resistance")
        }

        return builder
    }
}
