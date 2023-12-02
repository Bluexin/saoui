package be.bluexin.mcui.themes.elements

import be.bluexin.mcui.api.themes.IHudDrawContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
sealed class CachingElementParent : Element(), ElementParent {

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

    private fun updateCache(ctx: IHudDrawContext) {
        if (checkUpdate(ctx)) {
            cachedX = parentOrZero.getX(ctx) + this.x(ctx)
            cachedY = parentOrZero.getY(ctx) + this.y(ctx)
            cachedZ = parentOrZero.getZ(ctx) + this.z(ctx)
        }
    }

    /**
     * Returns true if the element should update it's position. Can be extremely useful in huge groups
     */
    private fun checkUpdate(ctx: IHudDrawContext) =
        if (latestTicks == ctx.partialTicks) false
        else {
            latestTicks = ctx.partialTicks
            true
        }
}