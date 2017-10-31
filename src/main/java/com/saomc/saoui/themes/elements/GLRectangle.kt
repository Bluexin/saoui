package com.saomc.saoui.themes.elements

import com.saomc.saoui.GLCore
import com.saomc.saoui.api.themes.IHudDrawContext
import com.saomc.saoui.themes.util.CDouble
import com.saomc.saoui.themes.util.CInt
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
    protected var rgba: CInt? = null
    protected var srcX: CDouble? = null
    protected var srcY: CDouble? = null
    protected var w: CDouble? = null
    protected var h: CDouble? = null
    protected var srcW: CDouble? = null
    protected var srcH: CDouble? = null
    protected var rl: ResourceLocation? = null
    private val texture: String? = null

    override fun draw(ctx: IHudDrawContext) {
        if (enabled?.invoke(ctx) == false) return

        val p: ElementParent? = this.parent.get()
        val x = (this.x?.invoke(ctx) ?: 0.0) + (p?.getX(ctx) ?: 0.0)
        val y = (this.y?.invoke(ctx) ?: 0.0) + (p?.getY(ctx) ?: 0.0)
        val z = (this.z?.invoke(ctx) ?: 0.0) + (p?.getZ(ctx) ?: 0.0) + ctx.z

        GLCore.glBlend(true)
        GLCore.glColorRGBA(this.rgba?.invoke(ctx) ?: 0xFFFFFFFF.toInt())
        if (this.rl != null) GLCore.glBindTexture(this.rl!!)
        GLCore.glTexturedRect(x, y, z, w?.invoke(ctx) ?: 0.0, h?.invoke(ctx) ?: 0.0, srcX?.invoke(ctx) ?: 0.0, srcY?.invoke(ctx) ?: 0.0, srcW?.invoke(ctx) ?: w?.invoke(ctx) ?: 0.0, srcH?.invoke(ctx) ?: h?.invoke(ctx) ?: 0.0)
    }

    override fun setup(parent: ElementParent): Boolean {
        if (this.texture != null) this.rl = ResourceLocation(this.texture)
        return super.setup(parent)
    }

}
