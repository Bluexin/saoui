package com.saomc.saoui.themes.elements;

import com.saomc.saoui.themes.util.ExpressionAdapter;
import com.saomc.saoui.themes.util.IntExpressionWrapper;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * Part of saoui by Bluexin.
 *
 * @author Bluexin
 */
public class GLHotbarItem extends GLRectangle {

    @XmlJavaTypeAdapter(value = ExpressionAdapter.IntExpressionAdapter.class)
    protected IntExpressionWrapper slot;
    protected EnumHandSide hand;
    @XmlJavaTypeAdapter(value = ExpressionAdapter.IntExpressionAdapter.class)
    protected IntExpressionWrapper selectedrgba;

    protected GLHotbarItem() {
    }

    /*
    From net.minecraft.client.gui.GuiIngame
     */
    private static void renderHotbarItem(int x, int y, float partialTicks, EntityPlayer player, @Nullable ItemStack stack, HudDrawContext ctx) {
        if (stack != null) {
            float f = (float) stack.animationsToGo - partialTicks;

            if (f > 0.0F) {
                GlStateManager.pushMatrix();
                float f1 = 1.0F + f / 5.0F;
                GlStateManager.translate((float) (x + 8), (float) (y + 12), 0.0F);
                GlStateManager.scale(1.0F / f1, (f1 + 1.0F) / 2.0F, 1.0F);
                GlStateManager.translate((float) (-(x + 8)), (float) (-(y + 12)), 0.0F);
            }

            ctx.getItemRenderer().renderItemAndEffectIntoGUI(player, stack, x, y);

            if (f > 0.0F) GlStateManager.popMatrix();

            ctx.getItemRenderer().renderItemOverlays(ctx.getMc().fontRendererObj, stack, x, y);
        }
    }

    @Override
    public void draw(HudDrawContext ctx) {
        super.draw(ctx);

        if (hand == null) {

        } else {

        }
    }
}
