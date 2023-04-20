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

package be.bluexin.mcui.themes.elements

import be.bluexin.mcui.api.themes.IHudDrawContext
import be.bluexin.mcui.themes.util.CInt
import jakarta.xml.bind.annotation.XmlRootElement
import net.minecraft.world.entity.HumanoidArm
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack

/**
 * Part of saoui by Bluexin.

 * @author Bluexin
 */
@XmlRootElement
open class GLHotbarItem : GLRectangle() {

    protected lateinit var slot: CInt
    protected lateinit var itemXoffset: CInt
    protected lateinit var itemYoffset: CInt
    protected var hand: HumanoidArm? = null

    /*
    From net.minecraft.client.gui.GuiIngame
     */
    private fun renderHotbarItem(x: Int, y: Int, partialTicks: Float, player: Player, stack: ItemStack, ctx: IHudDrawContext) {
        if (stack.isEmpty) return
        val f = stack.useDuration - partialTicks

        if (f > 0.0f) {
//            GLCore.pushMatrix()
            val f1 = 1.0f + f / 5.0f
//            GLCore.translate((x + 8).toFloat(), (y + 12).toFloat(), 0.0f)
//            GLCore.scale(1.0f / f1, (f1 + 1.0f) / 2.0f, 1.0f)
//            GLCore.translate((-(x + 8)).toFloat(), (-(y + 12)).toFloat(), 0.0f)
        }

        ctx.itemRenderer.renderGuiItem(pose, stack, x, y)

//        if (f > 0.0f) GLCore.popMatrix()

        ctx.itemRenderer.renderGuiItemDecorations(pose, ctx.fontRenderer, stack, x, y)
    }

    override fun draw(ctx: IHudDrawContext) {
        if (enabled?.invoke(ctx) == false || hand == ctx.player.mainArm) return
        super.draw(ctx)

        val p: ElementParent? = this.parent.get()
        val it: ItemStack = if (hand == null) ctx.player.inventory.items[slot(ctx)]
        else ctx.player.inventory.offhand[slot(ctx)]

        if (it == ItemStack.EMPTY) return

//        GLCore.glBlend(false)
//        GLCore.glRescaleNormal(true)
//        RenderHelper.enableGUIStandardItemLighting()

        renderHotbarItem(
            (x?.invoke(ctx)?.toInt() ?: 0) + itemXoffset(ctx) + (p?.getX(ctx)?.toInt() ?: 0),
            (y?.invoke(ctx)?.toInt() ?: 0) + itemYoffset(ctx) + (p?.getY(ctx)?.toInt() ?: 0),
            ctx.partialTicks,
            ctx.player,
            it,
            ctx
        )

//        GLCore.glRescaleNormal(false)
//        RenderHelper.disableStandardItemLighting()
//        GLCore.glBlend(true)
    }
}
