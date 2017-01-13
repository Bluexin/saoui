package com.saomc.saoui.themes.elements

import com.saomc.saoui.GLCore
import com.saomc.saoui.api.themes.IHudDrawContext
import com.saomc.saoui.themes.util.CString
import javax.xml.bind.annotation.XmlRootElement

/**
 * Part of saoui by Bluexin.

 * @author Bluexin
 */
@XmlRootElement
open class GLString : GLRectangle() {

    protected lateinit var text: CString
    private val shadow = true

    override fun draw(ctx: IHudDrawContext) {
        if (!(enabled?.invoke(ctx) ?: true)) return
        val p = this.parent.get()
        val x = (this.x?.invoke(ctx) ?: 0.0) + (p?.getX(ctx) ?: 0.0)
        val y = (this.y?.invoke(ctx) ?: 0.0) + (p?.getY(ctx) ?: 0.0) + ((this.h?.invoke(ctx) ?: 0.0) - ctx.fontRenderer.FONT_HEIGHT) / 2.0

        GLCore.glString(ctx.fontRenderer, this.text.invoke(ctx), x.toInt(), y.toInt(), rgba?.invoke(ctx) ?: 0xFFFFFFFF.toInt(), shadow)
    }
}
