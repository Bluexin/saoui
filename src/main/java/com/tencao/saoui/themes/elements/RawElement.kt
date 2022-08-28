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
import com.tencao.saomclib.GLCore
import com.tencao.saoui.api.themes.IHudDrawContext
import com.tencao.saoui.themes.util.CUnit
import net.minecraft.util.ResourceLocation
import javax.xml.bind.annotation.XmlRootElement

/**
 * Part of saoui by Bluexin, released under GNU GPLv3.
 *
 * @author Bluexin
 */
@XmlRootElement
class RawElement : Element() {

    private var rl: ResourceLocation? = null
    private val texture: String? = null

    private lateinit var expression: CUnit

    override fun setup(parent: ElementParent): Boolean {
        if (this.texture != null) this.rl = ResourceLocation(this.texture)
        return super.setup(parent)
    }

    override fun draw(ctx: IHudDrawContext, stack: MatrixStack) {
        GLCore.pushMatrix()
        val p: ElementParent? = this.parent.get()
        val x = (this.x?.invoke(ctx) ?: 0.0) + (p?.getX(ctx) ?: 0.0)
        val y = (this.y?.invoke(ctx) ?: 0.0) + (p?.getY(ctx) ?: 0.0)
        val z = (this.z?.invoke(ctx) ?: 0.0) + (p?.getZ(ctx) ?: 0.0) + ctx.z
        GLCore.translate(x, y, z)
        expression(ctx)
        GLCore.popMatrix()
    }
}
