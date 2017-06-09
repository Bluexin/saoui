package com.saomc.saoui.themes.elements.menus

import com.saomc.saoui.api.themes.IHudDrawContext
import java.util.*
import javax.xml.bind.annotation.XmlRootElement

/**
 * Part of saoui by Bluexin.

 * @author Bluexin
 */
@XmlRootElement(namespace = "http://www.bluexin.be/com/saomc/saoui/hud-schema")
open class MenuDefs protected constructor(override val name: String = "MenuDefs") : MenuElementParent {
    override val parentX: Int = 0
    override val parentY: Int = 0
    override val parentZ: Int = 0

    private val parts = HashMap<MenuDefEnum, MenuElementGroup>()

    operator fun get(key: MenuDefEnum) = parts[key]

    fun setup() = this.parts.values.forEach { it.setup(this) }

    fun draw(key: MenuDefEnum, ctx: IHudDrawContext) = this[key]?.draw(ctx)

    override fun closeCategory(name: String) {

    }

    override fun openCategory(name: String) {

    }
}
