package com.tencao.saoui.util.render

import com.tencao.saoui.api.entity.rendering.*
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player

val LivingEntity.colorStateHandler: IColorStateHandler
    get() = (this as? IColorStatedEntity)?.colorState ?: (this as? Player)?.let { PlayerColorStateHandler(it) } ?: MobColorStateHandler(this)