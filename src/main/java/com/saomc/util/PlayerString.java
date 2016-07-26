package com.saomc.util;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.text.translation.I18n;

import java.text.DecimalFormat;
import java.util.Collection;

public final class PlayerString implements Strings {

    private final EntityPlayer player;

    public PlayerString(EntityPlayer entityPlayer) {
        player = entityPlayer;
    }

    private static float attr(double attributeValue) {
        return (float) ((int) (attributeValue * 1000)) / 1000;
    }

    public final String toString() {
        final StringBuilder builder = new StringBuilder();
        EntityLivingBase mount = (EntityLivingBase) player.getRidingEntity();

        if (player.isRiding() && OptionCore.MOUNT_STAT_VIEW.getValue()) {
            final String name = mount.getCustomNameTag();
            final double maxHealth = attr(mount.getMaxHealth());
            double health = attr(mount.getHealth());
            final double resistance = attr(mount.getTotalArmorValue());
            final double speed = attr(mount.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue());
            final double jump;
            DecimalFormat df3 = new DecimalFormat("0.000");
            DecimalFormat df1 = new DecimalFormat("0.0");
            String speedFormated = df3.format(speed);
            health *= 10;
            health += 0.5F;
            health /= 10.0F;
            String healthFormated = df1.format(health);

            builder.append(I18n.translateToLocal("displayName")).append(": ").append(name).append('\n');
            builder.append(I18n.translateToLocal("displayHpLong")).append(": ").append(healthFormated).append("/").append(maxHealth).append('\n');
            builder.append(I18n.translateToLocal("displayResLong")).append(": ").append(resistance).append('\n');
            builder.append(I18n.translateToLocal("displaySpdLong")).append(": ").append(speedFormated).append('\n');
            if (mount instanceof EntityHorse) {
                jump = ((EntityHorse) mount).getHorseJumpStrength();
                String jumpFormated = df3.format(jump);
                builder.append(I18n.translateToLocal("displayJmpLong")).append(": ").append(jumpFormated).append('\n');
            }
        } else {
            final int level = player.experienceLevel;
            final int experience = (int) (player.experience * 100);

            final float health = attr(player.getHealth());

            final float maxHealth = attr(player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getAttributeValue());
            final float attackDamage = attr(player.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue());
            // final float movementSpeed = attr(player.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getAttributeValue());
            // final float knowbackResistance = attr(player.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).getAttributeValue());

            float itemDamage = 0.0F;

            if (player.getHeldEquipment() != null) {
                if (player.getHeldItemMainhand() != null) {
                    final Collection<?> itemAttackMain = player.getHeldItemMainhand().getAttributeModifiers(EntityEquipmentSlot.MAINHAND).get(SharedMonsterAttributes.ATTACK_DAMAGE.getAttributeUnlocalizedName());

                    itemDamage += itemAttackMain.stream().filter(value -> value instanceof AttributeModifier).map(value -> (AttributeModifier) value)
                            .filter(mod -> mod.getName().equals("Weapon modifier")).mapToDouble(AttributeModifier::getAmount).sum();
                }
                if (player.getHeldItemOffhand() != null) {
                    final Collection<?> itemAttackOff = player.getHeldItemOffhand().getAttributeModifiers(EntityEquipmentSlot.OFFHAND).get(SharedMonsterAttributes.ATTACK_DAMAGE.getAttributeUnlocalizedName());

                    itemDamage += itemAttackOff.stream().filter(value -> value instanceof AttributeModifier).map(value -> (AttributeModifier) value)
                            .filter(mod -> mod.getName().equals("Weapon modifier")).mapToDouble(AttributeModifier::getAmount).sum();
                }
            }

            final float strength = attr(attackDamage + itemDamage);
            final float agility = attr(player.getAIMoveSpeed()) * 10;
            final float resistance = attr(player.getTotalArmorValue());

            builder.append(I18n.translateToLocal("displayLvLong")).append(": ").append(level).append('\n');
            builder.append(I18n.translateToLocal("displayXpLong")).append(": ").append(experience).append("%\n");

            builder.append(I18n.translateToLocal("displayHpLong")).append(": ").append(health).append("/").append(maxHealth).append('\n');
            builder.append(I18n.translateToLocal("displayStrLong")).append(": ").append(strength).append('\n');
            builder.append(I18n.translateToLocal("displayDexLong")).append(": ").append(agility).append('\n');
            builder.append(I18n.translateToLocal("displayResLong")).append(": ").append(resistance).append("\n");
        }

        return builder.toString();
    }

}
