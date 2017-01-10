package com.saomc.saoui.themes.elements

import com.saomc.saoui.GLCore
import com.saomc.saoui.themes.util.DoubleExpressionWrapper
import com.saomc.saoui.themes.util.HudDrawContext
import com.saomc.saoui.themes.util.IntExpressionWrapper
import net.minecraft.util.ResourceLocation
import javax.xml.bind.annotation.XmlRootElement
import javax.xml.bind.annotation.XmlSeeAlso

/**
 * Part of saoui by Bluexin.

 * @author Bluexin
 */
// Needed for XML loading
@XmlSeeAlso(GLString::class, GLHotbarItem::class)
@XmlRootElement
open class GLRectangle : Element() {
    protected var rgba: IntExpressionWrapper? = null
    protected var srcX: DoubleExpressionWrapper? = null
    protected var srcY: DoubleExpressionWrapper? = null
    protected var w: DoubleExpressionWrapper? = null
    protected var h: DoubleExpressionWrapper? = null
    protected var srcW: DoubleExpressionWrapper? = null
    protected var srcH: DoubleExpressionWrapper? = null
    protected var rl: ResourceLocation? = null
    private val texture: String? = null

    override fun draw(ctx: HudDrawContext) {
        if (!(enabled?.execute(ctx) ?: true)) return

        val p: ElementParent? = this.parent.get()
        val x = (this.x?.execute(ctx) ?: 0.0) + (p?.getX(ctx) ?: 0.0)
        val y = (this.y?.execute(ctx) ?: 0.0) + (p?.getY(ctx) ?: 0.0)
        val z = (this.z?.execute(ctx) ?: 0.0) + (p?.getZ(ctx) ?: 0.0) + ctx.z

        GLCore.glBlend(true)
        GLCore.glColorRGBA(this.rgba?.execute(ctx) ?: 0xFFFFFFFF.toInt())
        GLCore.glBindTexture(this.rl)
        GLCore.glTexturedRect(x, y, z, w?.execute(ctx) ?: 0.0, h?.execute(ctx) ?: 0.0, srcX?.execute(ctx) ?: 0.0, srcY?.execute(ctx) ?: 0.0, srcW?.execute(ctx) ?: 0.0, srcH?.execute(ctx) ?: 0.0)
    }

    override fun setup(parent: ElementParent) {
        super.setup(parent)
        if (this.texture != null) this.rl = ResourceLocation(this.texture)
    }

}
