package com.saomc.saoui.themes.elements.menus

import com.saomc.saoui.GLCore
import com.saomc.saoui.SAOCore
import com.saomc.saoui.api.elements.CategoryEnum
import com.saomc.saoui.api.elements.MenuElementParent
import com.saomc.saoui.api.themes.IHudDrawContext
import com.saomc.saoui.themes.elements.Element
import com.saomc.saoui.themes.elements.RepetitionGroup
import net.minecraft.util.ResourceLocation
import javax.xml.bind.annotation.*

/**
 * Part of saoui by Bluexin.
 *
 * @author Bluexin
 */
@XmlRootElement
@XmlSeeAlso(RepetitionGroup::class)
open class MenuElementGroup : MenuElement(), MenuElementParent {

    @XmlElementWrapper(name = "children")
    @XmlElementRef(type = Element::class)
    protected lateinit var elements: List<MenuElement>
    protected var rl: ResourceLocation? = null
    private val texture: String? = null

    @XmlTransient protected var cachedX = 0
    @XmlTransient protected var cachedY = 0
    @XmlTransient protected var cachedZ = 0
    @XmlTransient protected var latestTicks = -1.0F

    /**
     * Returns true if the element should update it's position. Can be extremely useful in huge groups
     */
    protected fun checkUpdate(ctx: IHudDrawContext) = if (latestTicks == ctx.partialTicks) false else {
        latestTicks = ctx.partialTicks; true
    }

    override fun closeCategory(category: CategoryEnum) { }

    override fun openCategory(category: CategoryEnum) { }

    override val parentX: Int
        get() {
//        updateCache(ctx)
        return cachedX
    }

    override val parentY: Int
        get() {
//        updateCache(ctx)
            return cachedY
        }

    override val parentZ: Int
        get() {
//        updateCache(ctx)
        return cachedZ
    }

    override fun draw(ctx: IHudDrawContext) {
        if (this.rl != null) GLCore.glBindTexture(this.rl)
        if (enabled?.invoke(ctx) ?: true) this.elements.forEach { it.draw(ctx) }
    }

    override fun setup(parent: MenuElementParent): Boolean {
        if (this.texture != null) this.rl = ResourceLocation(this.texture)
        val res = super.setup(parent)
        var anonymous = 0
        this.elements.forEach { if (it.name == DEFAULT_NAME) ++anonymous; it.setup(this) }
        if (anonymous > 0) SAOCore.LOGGER.info("Set up $anonymous anonymous elements in $name.")
        return res
    }

    private fun updateCache(ctx: IHudDrawContext) {
        if (checkUpdate(ctx)) {
            cachedX = (parent.get()?.parentX ?: 0) + (this.x?.invoke(ctx) ?: 0)
            cachedY = (parent.get()?.parentY ?: 0) + (this.y?.invoke(ctx) ?: 0)
            cachedZ = (parent.get()?.parentZ ?: 0) + (this.z?.invoke(ctx) ?: 0)
        }
    }
}
