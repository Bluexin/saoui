package com.saomc.saoui.themes.elements

import com.saomc.saoui.GLCore
import com.saomc.saoui.api.themes.IHudDrawContext
import com.saomc.saoui.util.LogCore
import net.minecraft.util.ResourceLocation
import javax.xml.bind.annotation.XmlElementRef
import javax.xml.bind.annotation.XmlElementWrapper
import javax.xml.bind.annotation.XmlRootElement

/**
 * Part of saoui by Bluexin.
 *
 * @author Bluexin
 */
@XmlRootElement
open class ElementGroup : Element(), ElementParent { // TODO: make elementGroups with a texture for less RL instantiation and binding on children

    @XmlElementWrapper(name = "children")
    @XmlElementRef(type = Element::class)
    protected lateinit var elements: List<Element>
    protected var rl: ResourceLocation? = null
    private val texture: String? = null

    override fun getX(ctx: IHudDrawContext) = (parent.get()?.getX(ctx) ?: 0.0) + (this.x?.execute(ctx) ?: 0.0)

    override fun getY(ctx: IHudDrawContext) = (parent.get()?.getY(ctx) ?: 0.0) + (this.y?.execute(ctx) ?: 0.0)

    override fun getZ(ctx: IHudDrawContext) = (parent.get()?.getZ(ctx) ?: 0.0) + (this.z?.execute(ctx) ?: 0.0)

    override fun draw(ctx: IHudDrawContext) {
        if (this.rl != null) GLCore.glBindTexture(this.rl)
        if (enabled?.execute(ctx) ?: true) this.elements.forEach { it.draw(ctx) }
    }

    override fun setup(parent: ElementParent): Boolean {
        if (this.texture != null) this.rl = ResourceLocation(this.texture)
        val res = super.setup(parent)
        var anonymous = 0
        this.elements.forEach { if (it.name == Element.DEFAULT_NAME) ++anonymous; it.setup(this) }
        if (anonymous > 0) LogCore.logInfo("Set up $anonymous anonymous elements in $name.")
        return res
    }
}
