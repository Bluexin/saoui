package com.saomc.saoui.themes.elements

import com.saomc.saoui.api.themes.IHudDrawContext
import com.saomc.saoui.themes.util.CInt
import javax.xml.bind.annotation.XmlRootElement

/**
 * Part of saoui by Bluexin.
 *
 * @author Bluexin
 */
@XmlRootElement
open class RepetitionGroup : ElementGroup() {
    protected var amount: CInt? = null

    override fun draw(ctx: IHudDrawContext) {
        if (enabled?.invoke(ctx) ?: false) return

        val m = amount?.invoke(ctx) ?: 0
        for (i in 0..m - 1) {
            ctx.setI(i)
            super.draw(ctx)
        }
    }
}
