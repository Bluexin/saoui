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

import com.tencao.saoui.api.info.PlayerStatsProvider
import com.tencao.saoui.config.OptionCore
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
class DefaultStatsProvider : PlayerStatsProvider {

    private fun attr(attributeValue: Double): Float {
        return (attributeValue * 1000).toInt().toFloat() / 1000
    }

    override fun getStatsString(player: PlayerEntity): List<String> {
        val builder = mutableListOf<String>()
        val mount = player.rootVehicle as LivingEntity?

        if (mount != null && mount != player && OptionCore.MOUNT_STAT_VIEW.isEnabled) {
            val name = mount.name
            val maxHealth = attr(mount.maxHealth.toDouble()).toDouble()
            var health = attr(mount.health.toDouble()).toDouble()
            val resistance = attr(mount.armorValue.toDouble()).toDouble()
            val speed = attr(mount.getAttributeValue(Attributes.MOVEMENT_SPEED)).toDouble()
            val jump: Double
            val df3 = DecimalFormat("0.000")
            val df1 = DecimalFormat("0.0")
            val speedFormated = df3.format(speed)
            health *= 10.0
            health += 0.5
            health /= 10.0
            val healthFormated = df1.format(health)

            builder.add("displayName".format().plus(": $name"))
            builder.add("displayHpLong".format().plus(": $healthFormated/$maxHealth"))
            builder.add("displayResLong".format().plus(": $resistance"))
            builder.add("displaySpdLong".format().plus(": $speedFormated"))
            if (mount is HorseEntity) {
                jump = mount.customJump
                val jumpFormated = df3.format(jump)
                builder.add("displayJmpLong".format().plus(": $jumpFormated"))
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

            if (!player.mainHandItem.isEmpty) {
                val itemAttackMain = player.mainHandItem.getAttributeModifiers(EquipmentSlotType.MAINHAND).get(Attributes.ATTACK_DAMAGE)

                itemDamage += itemAttackMain.filterIsInstance<AttributeModifier>().map { value -> value }
                    .filter { mod -> mod.name == "Weapon modifier" }.sumOf { it.amount }.toFloat()
            }
            if (!player.offhandItem.isEmpty) {
                val itemAttackOff = player.offhandItem.getAttributeModifiers(EquipmentSlotType.OFFHAND).get(Attributes.ATTACK_DAMAGE)

                itemDamage += itemAttackOff.filterIsInstance<AttributeModifier>().map { value -> value }
                    .filter { mod -> mod.name == "Weapon modifier" }.sumOf { it.amount }.toFloat()
            }

            val strength = attr((attackDamage + itemDamage).toDouble())
            val agility = attr(player.speed.toDouble()) * 10
            val resistance = attr(player.armorValue.toDouble())

            builder.add("displayLvLong".format().plus(": $level"))
            builder.add("displayXpLong".format().plus(": $experience"))
            builder.add("displayHpLong".format().plus(": $health/$maxHealth"))
            builder.add("displayStrLong".format().plus(": $strength"))
            builder.add("displayDexLong".format().plus(": $agility"))
            builder.add("displayResLong".format().plus(": $resistance"))
        }

        return builder
    }

}
