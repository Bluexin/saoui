package com.saomc.saoui.themes.elements

import com.saomc.saoui.GLCore
import com.saomc.saoui.api.themes.IHudDrawContext
import com.saomc.saoui.themes.util.CInt
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumHandSide
import javax.xml.bind.annotation.XmlRootElement

/**
 * Part of saoui by Bluexin.

 * @author Bluexin
 */
@XmlRootElement
open class GLHotbarItem: GLRectangle() {

    protected lateinit var slot: CInt
    protected lateinit var itemXoffset: CInt
    protected lateinit var itemYoffset: CInt
    protected var hand: EnumHandSide? = null

    /*
    From net.minecraft.client.gui.GuiIngame
     */
    private fun renderHotbarItem(x: Int, y: Int, partialTicks: Float, player: EntityPlayer, stack: ItemStack, ctx: IHudDrawContext) {
        val f = stack.animationsToGo.toFloat() - partialTicks

        if (f > 0.0f) {
            GlStateManager.pushMatrix()
            val f1 = 1.0f + f / 5.0f
            GlStateManager.translate((x + 8).toFloat(), (y + 12).toFloat(), 0.0f)
            GlStateManager.scale(1.0f / f1, (f1 + 1.0f) / 2.0f, 1.0f)
            GlStateManager.translate((-(x + 8)).toFloat(), (-(y + 12)).toFloat(), 0.0f)
        }

        ctx.itemRenderer.renderItemAndEffectIntoGUI(player, stack, x, y)

        if (f > 0.0f) GlStateManager.popMatrix()

        ctx.itemRenderer.renderItemOverlays(ctx.fontRenderer, stack, x, y)
    }

    override fun draw(ctx: IHudDrawContext) {
        if (!(enabled?.invoke(ctx) ?: true) || hand == ctx.player.primaryHand) return
        super.draw(ctx)

        val p: ElementParent? = this.parent.get()
        val it: ItemStack?

        if (hand == null) it = ctx.player.inventory.mainInventory[slot(ctx)]
        else it = ctx.player.inventory.offHandInventory[slot(ctx)]
        if (it == ItemStack.EMPTY) return

        GLCore.glBlend(false)
        GLCore.glRescaleNormal(true)
        RenderHelper.enableGUIStandardItemLighting()

        renderHotbarItem(
                (x?.invoke(ctx)?.toInt() ?: 0) + itemXoffset(ctx) + (p?.getX(ctx)?.toInt() ?: 0),
                (y?.invoke(ctx)?.toInt() ?: 0) + itemYoffset(ctx) + (p?.getY(ctx)?.toInt() ?: 0),
                ctx.partialTicks, ctx.player, it, ctx)

        GLCore.glRescaleNormal(false)
        RenderHelper.disableStandardItemLighting()
        GLCore.glBlend(true)

    }
}
