package com.saomc.saoui.themes.elements

import com.saomc.saoui.themes.util.DoubleExpressionWrapper
import com.saomc.saoui.themes.util.HudDrawContext
import java.util.*
import javax.xml.bind.annotation.XmlElementRef
import javax.xml.bind.annotation.XmlElementWrapper

/**
 * Part of saoui by Bluexin.

 * @author Bluexin
 */
open class HudPart protected constructor() : ElementParent {

    protected var x: DoubleExpressionWrapper? = null
    protected var y: DoubleExpressionWrapper? = null
    protected var z: DoubleExpressionWrapper? = null
    @XmlElementWrapper(name = "calls")
    @XmlElementRef(type = Element::class)
    private val elements: List<Element> = ArrayList()

    override fun getX(ctx: HudDrawContext): Double = x?.execute(ctx) ?: 0.0

    override fun getY(ctx: HudDrawContext): Double = y?.execute(ctx) ?: 0.0

    override fun getZ(ctx: HudDrawContext): Double = z?.execute(ctx) ?: 0.0

    fun setup() = this.elements.forEach { it.setup(this) }

    fun draw(ctx: HudDrawContext) = this.elements.forEach { it.draw(ctx) }
}
