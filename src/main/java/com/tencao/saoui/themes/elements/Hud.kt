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

package com.tencao.saoui.themes.elements

import com.mojang.blaze3d.matrix.MatrixStack
import com.tencao.saoui.api.themes.IHudDrawContext
import javax.xml.bind.annotation.XmlRootElement

/**
 * Part of saoui by Bluexin.

 * @author Bluexin
 */
@XmlRootElement(namespace = "http://www.bluexin.be/com/saomc/saoui/hud-schema")
open class Hud protected constructor(override val name: String = "MenuDefs") : ElementParent {
    val version = "1.0"

    override fun getX(ctx: IHudDrawContext) = 0.0

    override fun getY(ctx: IHudDrawContext) = 0.0

    override fun getZ(ctx: IHudDrawContext) = 0.0

    private val parts = HashMap<HudPartType, ElementGroup>()

    operator fun get(key: HudPartType) = parts[key]

    fun setup() = this.parts.values.forEach { it.setup(this) }

    fun draw(key: HudPartType, ctx: IHudDrawContext, stack: MatrixStack) {
        this[key]?.draw(ctx, stack)
    }
}
