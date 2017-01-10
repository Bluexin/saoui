package com.saomc.saoui.themes.elements

import com.saomc.saoui.themes.util.HudDrawContext
import com.saomc.saoui.util.LogCore
import javax.xml.bind.annotation.XmlElementRef
import javax.xml.bind.annotation.XmlElementWrapper
import javax.xml.bind.annotation.XmlRootElement

/**
 * Part of saoui by Bluexin.
 *
 * @author Bluexin
 */
@XmlRootElement
open class ElementGroup : Element(), ElementParent {

    @XmlElementWrapper(name = "children")
    @XmlElementRef(type = Element::class)
    protected lateinit var elements: List<Element>

    override fun getX(ctx: HudDrawContext) = (parent.get()?.getX(ctx) ?: 0.0) + (this.x?.execute(ctx) ?: 0.0)

    override fun getY(ctx: HudDrawContext) = (parent.get()?.getY(ctx) ?: 0.0) + (this.y?.execute(ctx) ?: 0.0)

    override fun getZ(ctx: HudDrawContext) = (parent.get()?.getZ(ctx) ?: 0.0) + (this.z?.execute(ctx) ?: 0.0)

    override fun draw(ctx: HudDrawContext) {
        if (enabled?.execute(ctx) ?: true) this.elements.forEach { it.draw(ctx) }
    }

    override fun setup(parent: ElementParent): Boolean {
        val res = super.setup(parent)
        var anonymous = 0
        this.elements.forEach { if (it.name == Element.DEFAULT_NAME) ++anonymous; it.setup(this) }
        if (anonymous > 0) LogCore.logInfo("Set up $anonymous anonymous elements in $name.")
        return res
    }
}
