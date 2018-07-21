package com.saomc.saoui.themes.elements

import com.saomc.saoui.GLCore
import com.saomc.saoui.api.themes.IHudDrawContext
import com.saomc.saoui.themes.util.CUnit
import javax.xml.bind.annotation.XmlRootElement

/**
 * Part of saoui by Bluexin, released under GNU GPLv3.
 *
 * @author Bluexin
 */
@XmlRootElement
class RawElement : Element() {

    private lateinit var expression: CUnit

    override fun draw(ctx: IHudDrawContext) {
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
