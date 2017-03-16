package com.saomc.saoui.themes.elements

import com.saomc.saoui.GLCore
import com.saomc.saoui.api.themes.IHudDrawContext
import com.saomc.saoui.themes.util.CInt
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import org.lwjgl.opengl.GL11
import javax.xml.bind.annotation.XmlRootElement

/**
 * Part of saoui by Bluexin.

 * @author Bluexin
 */
@XmlRootElement
open class GLHotbarItem constructor() : GLRectangle() {

    protected lateinit var slot: CInt
    protected lateinit var itemXoffset: CInt
    protected lateinit var itemYoffset: CInt

    /*
    From net.minecraft.client.gui.GuiIngame
     */
    private fun renderHotbarItem(x: Int, y: Int, partialTicks: Float, player: EntityPlayer, stack: ItemStack, ctx: IHudDrawContext) {
        val f = stack.animationsToGo.toFloat() - partialTicks

        if (f > 0.0f) {
            GL11.glPushMatrix()
            val f1 = 1.0f + f / 5.0f
            GL11.glTranslatef((x + 8).toFloat(), (y + 12).toFloat(), 0.0f)
            GL11.glScalef(1.0f / f1, (f1 + 1.0f) / 2.0f, 1.0f)
            GL11.glTranslatef((-(x + 8)).toFloat(), (-(y + 12)).toFloat(), 0.0f)
        }

        ctx.itemRenderer.renderItemAndEffectIntoGUI(ctx.fontRenderer, ctx.mc.textureManager, stack, x, y)

        if (f > 0.0f) GL11.glPopMatrix()

        ctx.itemRenderer.renderItemOverlayIntoGUI(ctx.fontRenderer, ctx.mc.textureManager, stack, x, y)
    }

    override fun draw(ctx: IHudDrawContext) {
        if (!(enabled?.invoke(ctx) ?: true)) return
        super.draw(ctx)

        val p: ElementParent? = this.parent.get()
        val it = ctx.player.inventory.mainInventory[slot.invoke(ctx)] ?: return

        GLCore.glBlend(false)
        GLCore.glRescaleNormal(true)
        RenderHelper.enableGUIStandardItemLighting()

        renderHotbarItem(
                (x?.invoke(ctx)?.toInt() ?: 0) + itemXoffset.invoke(ctx) + (p?.getX(ctx)?.toInt() ?: 0),
                (y?.invoke(ctx)?.toInt() ?: 0) + itemYoffset.invoke(ctx) + (p?.getY(ctx)?.toInt() ?: 0),
                ctx.partialTicks, ctx.player, it, ctx)

        GLCore.glRescaleNormal(false)
        RenderHelper.disableStandardItemLighting()
        GLCore.glBlend(true)

    }
}
