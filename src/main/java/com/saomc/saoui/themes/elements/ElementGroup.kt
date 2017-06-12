package com.saomc.saoui.themes.elements

import com.saomc.saoui.GLCore
import com.saomc.saoui.SAOCore
import com.saomc.saoui.api.themes.IHudDrawContext
import net.minecraft.util.ResourceLocation
import javax.xml.bind.annotation.*

/**
 * Part of saoui by Bluexin.
 *
 * @author Bluexin
 */
@XmlRootElement
@XmlSeeAlso(RepetitionGroup::class)
open class ElementGroup : Element(), ElementParent {

    @XmlElementWrapper(name = "children")
    @XmlElementRef(type = Element::class)
    protected lateinit var elements: List<Element>
    protected var rl: ResourceLocation? = null
    private val texture: String? = null

    @XmlTransient protected var cachedX = 0.0
    @XmlTransient protected var cachedY = 0.0
    @XmlTransient protected var cachedZ = 0.0
    @XmlTransient protected var latestTicks = -1.0F

    /**
     * Returns true if the element should update it's position. Can be extremely useful in huge groups
     */
    protected fun checkUpdate(ctx: IHudDrawContext) = if (latestTicks == ctx.partialTicks) false else {
        latestTicks = ctx.partialTicks; true
    }


    override fun getX(ctx: IHudDrawContext): Double {
        updateCache(ctx)
        return cachedX
    }

    override fun getY(ctx: IHudDrawContext): Double {
        updateCache(ctx)
        return cachedY
    }

    override fun getZ(ctx: IHudDrawContext): Double {
        updateCache(ctx)
        return cachedZ
    }

    override fun draw(ctx: IHudDrawContext) {
        if (this.rl != null) GLCore.glBindTexture(this.rl!!)
        if (enabled?.invoke(ctx) ?: true) this.elements.forEach { it.draw(ctx) }
    }

    override fun setup(parent: ElementParent): Boolean {
        if (this.texture != null) this.rl = ResourceLocation(this.texture)
        val res = super.setup(parent)
        var anonymous = 0
        this.elements.forEach { if (it.name == Element.DEFAULT_NAME) ++anonymous; it.setup(this) }
        if (anonymous > 0) SAOCore.LOGGER.info("Set up $anonymous anonymous elements in $name.")
        return res
    }

    private fun updateCache(ctx: IHudDrawContext) {
        if (checkUpdate(ctx)) {
            cachedX = (parent.get()?.getX(ctx) ?: 0.0) + (this.x?.invoke(ctx) ?: 0.0)
            cachedY = (parent.get()?.getY(ctx) ?: 0.0) + (this.y?.invoke(ctx) ?: 0.0)
            cachedZ = (parent.get()?.getZ(ctx) ?: 0.0) + (this.z?.invoke(ctx) ?: 0.0)
        }
    }
}
