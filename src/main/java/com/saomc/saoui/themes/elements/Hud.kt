package com.saomc.saoui.themes.elements

import com.saomc.saoui.themes.util.HudDrawContext
import java.util.*
import javax.xml.bind.annotation.XmlRootElement

/**
 * Part of saoui by Bluexin.

 * @author Bluexin
 */
@XmlRootElement(namespace = "http://www.bluexin.be/com/saomc/saoui/hud-schema")
open class Hud protected constructor(override val name: String = "Hud") : ElementParent {
    override fun getX(ctx: HudDrawContext) = 0.0

    override fun getY(ctx: HudDrawContext) = 0.0

    override fun getZ(ctx: HudDrawContext) = 0.0

    private val parts = HashMap<HudPartType, ElementGroup>()

    operator fun get(key: HudPartType) = parts[key]

    fun setup() = this.parts.values.forEach { it.setup(this) }
}
