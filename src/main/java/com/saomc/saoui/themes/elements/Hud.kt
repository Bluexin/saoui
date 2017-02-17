package com.saomc.saoui.themes.elements

import com.saomc.saoui.api.themes.IHudDrawContext
import java.util.*
import javax.xml.bind.annotation.XmlRootElement

/**
 * Part of saoui by Bluexin.

 * @author Bluexin
 */
@XmlRootElement(namespace = "http://www.bluexin.be/com/saomc/saoui/hud-schema")
open class Hud protected constructor(override val name: String = "MenuDefs") : ElementParent {
    override fun getX(ctx: IHudDrawContext) = 0.0

    override fun getY(ctx: IHudDrawContext) = 0.0

    override fun getZ(ctx: IHudDrawContext) = 0.0

    private val parts = HashMap<HudPartType, ElementGroup>()

    operator fun get(key: HudPartType) = parts[key]

    fun setup() = this.parts.values.forEach { it.setup(this) }

    fun draw(key: HudPartType, ctx: IHudDrawContext) = this[key]?.draw(ctx)
}
