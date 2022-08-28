/*
 * Copyright (C) 2016-2019 Arnaud 'Bluexin' Solé
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.tencao.saoui.themes.elements

import com.mojang.blaze3d.matrix.MatrixStack
import com.tencao.saomclib.GLCore
import com.tencao.saoui.api.themes.EnumHandSide
import com.tencao.saoui.api.themes.IHudDrawContext
import com.tencao.saoui.themes.util.CInt
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import javax.xml.bind.annotation.XmlRootElement

/**
 * Part of saoui by Bluexin.

 * @author Bluexin
 */
@XmlRootElement
open class GLHotbarItem : GLRectangle() {

    protected lateinit var slot: CInt
    protected lateinit var itemXoffset: CInt
    protected lateinit var itemYoffset: CInt
    protected var hand: EnumHandSide? = null

    /*
    From net.minecraft.client.gui.GuiIngame
     */
    private fun renderHotbarItem(x: Int, y: Int, partialTicks: Float, player: PlayerEntity, stack: ItemStack, ctx: IHudDrawContext) {
        if (stack.isEmpty) return
        val f = stack.animationsToGo.toFloat() - partialTicks

        if (f > 0.0f) {
            GLCore.pushMatrix()
            val f1 = 1.0f + f / 5.0f
            GLCore.translate((x + 8).toFloat(), (y + 12).toFloat(), 0.0f)
            GLCore.scale(1.0f / f1, (f1 + 1.0f) / 2.0f, 1.0f)
            GLCore.translate((-(x + 8)).toFloat(), (-(y + 12)).toFloat(), 0.0f)
        }

        ctx.itemRenderer.renderItemAndEffectIntoGUI(player, stack, x, y)

        if (f > 0.0f) GLCore.popMatrix()

        ctx.itemRenderer.renderItemOverlays(ctx.fontRenderer, stack, x, y)
    }

    override fun draw(ctx: IHudDrawContext, stack: MatrixStack) {
        if (enabled?.invoke(ctx) == false || hand == ctx.player?.primaryHand) return
        super.draw(ctx, stack)

        val p: ElementParent? = this.parent.get()
        val it: ItemStack = if (hand == null) ctx.player!!.inventory.mainInventory[slot(ctx)]
        else ctx.player!!.getHeldItem(hand!!.hand)

        if (it == ItemStack.EMPTY) return

        GLCore.glBlend(false)
        GLCore.glRescaleNormal(true)
        RenderHelper.enableStandardItemLighting()

        renderHotbarItem(
            (x?.invoke(ctx)?.toInt() ?: 0) + itemXoffset(ctx) + (p?.getX(ctx)?.toInt() ?: 0),
            (y?.invoke(ctx)?.toInt() ?: 0) + itemYoffset(ctx) + (p?.getY(ctx)?.toInt() ?: 0),
            ctx.partialTicks,
            ctx.player!!,
            it,
            ctx
        )

        GLCore.glRescaleNormal(false)
        RenderHelper.disableStandardItemLighting()
        GLCore.glBlend(true)
    }
}
