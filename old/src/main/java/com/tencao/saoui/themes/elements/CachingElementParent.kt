package com.tencao.saoui.themes.elements

import com.tencao.saoui.api.themes.IHudDrawContext

abstract class CachingElementParent : Element(), ElementParent {

    @Transient
    protected var cachedX = 0.0

    @Transient
    protected var cachedY = 0.0

    @Transient
    protected var cachedZ = 0.0

    @Transient
    protected var latestTicks = -1.0F

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

    protected fun updateCache(ctx: IHudDrawContext) {
        if (checkUpdate(ctx)) {
            cachedX = (parent.get()?.getX(ctx) ?: 0.0) + (this.x?.invoke(ctx) ?: 0.0)
            cachedY = (parent.get()?.getY(ctx) ?: 0.0) + (this.y?.invoke(ctx) ?: 0.0)
            cachedZ = (parent.get()?.getZ(ctx) ?: 0.0) + (this.z?.invoke(ctx) ?: 0.0)
        }
    }

    /**
     * Returns true if the element should update it's position. Can be extremely useful in huge groups
     */
    protected fun checkUpdate(ctx: IHudDrawContext) = if (latestTicks == ctx.partialTicks) false else {
        latestTicks = ctx.partialTicks; true
    }
}