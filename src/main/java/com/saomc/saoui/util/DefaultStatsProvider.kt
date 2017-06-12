package com.saomc.saoui.util

import com.saomc.saoui.api.info.IPlayerStatsProvider
import com.saomc.saoui.config.OptionCore
import net.minecraft.client.resources.I18n
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.SharedMonsterAttributes
import net.minecraft.entity.ai.attributes.AttributeModifier
import net.minecraft.entity.passive.EntityHorse
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.EntityEquipmentSlot

import java.text.DecimalFormat

/**
 * Part of saoui

 * @author Bluexin
 */
class DefaultStatsProvider : IPlayerStatsProvider {

    private fun attr(attributeValue: Double): Float {
        return (attributeValue * 1000).toInt().toFloat() / 1000
    }

    override fun getStatsString(player: EntityPlayer): String {
        val builder = StringBuilder()
        val mount = player.ridingEntity as EntityLivingBase?

        if (player.isRiding && OptionCore.MOUNT_STAT_VIEW.isEnabled) {
            val name = mount!!.name
            val maxHealth = attr(mount.maxHealth.toDouble()).toDouble()
            var health = attr(mount.health.toDouble()).toDouble()
            val resistance = attr(mount.totalArmorValue.toDouble()).toDouble()
            val speed = attr(mount.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).attributeValue).toDouble()
            val jump: Double
            val df3 = DecimalFormat("0.000")
            val df1 = DecimalFormat("0.0")
            val speedFormated = df3.format(speed)
            health *= 10.0
            health += 0.5
            health /= 10.0
            val healthFormated = df1.format(health)

            builder.append(I18n.format("displayName")).append(": ").append(name).append('\n')
            builder.append(I18n.format("displayHpLong")).append(": ").append(healthFormated).append("/").append(maxHealth).append('\n')
            builder.append(I18n.format("displayResLong")).append(": ").append(resistance).append('\n')
            builder.append(I18n.format("displaySpdLong")).append(": ").append(speedFormated).append('\n')
            if (mount is EntityHorse) {
                jump = mount.horseJumpStrength
                val jumpFormated = df3.format(jump)
                builder.append(I18n.format("displayJmpLong")).append(": ").append(jumpFormated).append('\n')
            }
        } else {
            val level = PlayerStats.instance().stats.getLevel(player)
            val experience = (PlayerStats.instance().stats.getExpPct(player) * 100).toInt()

            val health = attr(player.health.toDouble())

            val maxHealth = attr(player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).attributeValue)
            val attackDamage = attr(player.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).attributeValue)
            // final float movementSpeed = attr(player.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getAttributeValue());
            // final float knocbackResistance = attr(player.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).getAttributeValue());

            var itemDamage = 0.0f

            if (player.heldItemMainhand != null) {
                val itemAttackMain = player.heldItemMainhand.getAttributeModifiers(EntityEquipmentSlot.MAINHAND).get(SharedMonsterAttributes.ATTACK_DAMAGE.name)

                itemDamage += itemAttackMain.filter { value -> value is AttributeModifier }.map { value -> value }
                        .filter { mod -> mod.name == "Weapon modifier" }.sumByDouble { it.amount }.toFloat()
            }
            if (player.heldItemOffhand != null) {
                val itemAttackOff = player.heldItemOffhand.getAttributeModifiers(EntityEquipmentSlot.OFFHAND).get(SharedMonsterAttributes.ATTACK_DAMAGE.name)

                itemDamage += itemAttackOff.filter { value -> value is AttributeModifier }.map { value -> value }
                        .filter { mod -> mod.name == "Weapon modifier" }.sumByDouble { it.amount }.toFloat()
            }


            val strength = attr((attackDamage + itemDamage).toDouble())
            val agility = attr(player.aiMoveSpeed.toDouble()) * 10
            val resistance = attr(player.totalArmorValue.toDouble())

            builder.append(I18n.format("displayLvLong")).append(": ").append(level).append('\n')
            builder.append(I18n.format("displayXpLong")).append(": ").append(experience).append("%\n")

            builder.append(I18n.format("displayHpLong")).append(": ").append(health).append("/").append(maxHealth).append('\n')
            builder.append(I18n.format("displayStrLong")).append(": ").append(strength).append('\n')
            builder.append(I18n.format("displayDexLong")).append(": ").append(agility).append('\n')
            builder.append(I18n.format("displayResLong")).append(": ").append(resistance).append("\n")
        }

        return builder.toString()
    }
}
