package com.saomc.saoui.themes.elements

import com.saomc.saoui.GLCore
import com.saomc.saoui.themes.util.ExpressionAdapter
import com.saomc.saoui.themes.util.IntExpressionWrapper
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumHandSide
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter

/**
 * Part of saoui by Bluexin.

 * @author Bluexin
 */
open class GLHotbarItem protected constructor() : GLRectangle() {

    @XmlJavaTypeAdapter(ExpressionAdapter.IntExpressionAdapter::class)
    protected lateinit var slot: IntExpressionWrapper
    @XmlJavaTypeAdapter(ExpressionAdapter.IntExpressionAdapter::class)
    protected lateinit var itemXoffset: IntExpressionWrapper
    @XmlJavaTypeAdapter(ExpressionAdapter.IntExpressionAdapter::class)
    protected lateinit var itemYoffset: IntExpressionWrapper
    protected var hand: EnumHandSide? = null

    /*
    From net.minecraft.client.gui.GuiIngame
     */
    private fun renderHotbarItem(x: Int, y: Int, partialTicks: Float, player: EntityPlayer, stack: ItemStack, ctx: HudDrawContext) {
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

        ctx.itemRenderer.renderItemOverlays(ctx.mc.fontRendererObj, stack, x, y)
    }

    override fun draw(ctx: HudDrawContext) {
        super.draw(ctx)

        val p = this.parent.get()
        val it: ItemStack

        if (hand == null) it = ctx.player.inventory.mainInventory[slot.execute(ctx)]
        else if (hand == ctx.player.primaryHand.opposite()) it = ctx.player.inventory.offHandInventory[slot.execute(ctx)]
        else return

        GLCore.glBlend(false)
        GLCore.glRescaleNormal(true)
        RenderHelper.enableGUIStandardItemLighting()

        renderHotbarItem(
                x.execute(ctx).toInt() + itemXoffset.execute(ctx) + (p?.getX(ctx) ?: 0.0).toInt(),
                y.execute(ctx).toInt() + itemYoffset.execute(ctx) + (p?.getY(ctx) ?: 0.0).toInt(),
                ctx.partialTicks, ctx.player, it, ctx)

        GLCore.glRescaleNormal(false)
        RenderHelper.disableStandardItemLighting()
        GLCore.glBlend(true)

    }
}
