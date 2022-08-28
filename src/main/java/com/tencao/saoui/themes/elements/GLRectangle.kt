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
import com.tencao.saoui.themes.util.CDouble
import com.tencao.saoui.themes.util.CInt
import net.minecraft.util.ResourceLocation
import javax.xml.bind.annotation.XmlRootElement
import javax.xml.bind.annotation.XmlSeeAlso

/**
 * Part of saoui by Bluexin.

 * @author Bluexin
 */
// Needed for XML loading
@XmlSeeAlso(GLString::class, GLHotbarItem::class)
@XmlRootElement
open class GLRectangle : Element() {
    protected var rgba: CInt? = null
    protected var srcX: CDouble? = null
    protected var srcY: CDouble? = null
    protected var w: CDouble? = null
    protected var h: CDouble? = null
    protected var srcW: CDouble? = null
    protected var srcH: CDouble? = null
    protected var rl: ResourceLocation? = null
    private val texture: String? = null

    override fun draw(ctx: IHudDrawContext, stack: MatrixStack) {
        if (enabled?.invoke(ctx) == false) return

        val p: ElementParent? = this.parent.get()
        val x = (this.x?.invoke(ctx) ?: 0.0) + (p?.getX(ctx) ?: 0.0)
        val y = (this.y?.invoke(ctx) ?: 0.0) + (p?.getY(ctx) ?: 0.0)
        val z = (this.z?.invoke(ctx) ?: 0.0) + (p?.getZ(ctx) ?: 0.0) + ctx.z

        GLCore.glBlend(true)
        GLCore.color(this.rgba?.invoke(ctx) ?: 0xFFFFFFFF.toInt())
        if (this.rl != null) GLCore.glBindTexture(this.rl!!)
        GLCore.glTexturedRectV2(
            x, y, z, w?.invoke(ctx) ?: 0.0, h?.invoke(ctx) ?: 0.0,
            srcX?.invoke(ctx)
                ?: 0.0,
            srcY?.invoke(ctx) ?: 0.0, srcW?.invoke(ctx) ?: w?.invoke(ctx) ?: 0.0,
            srcH?.invoke(ctx)
                ?: h?.invoke(ctx) ?: 0.0
        )
    }

    override fun setup(parent: ElementParent): Boolean {
        if (this.texture != null) this.rl = ResourceLocation(this.texture)
        return super.setup(parent)
    }
}
