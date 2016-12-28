package com.saomc.saoui.themes.elements;

import com.saomc.saoui.GLCore;
import com.saomc.saoui.themes.util.ExpressionAdapter;
import com.saomc.saoui.themes.util.StringExpressionWrapper;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * Part of saoui by Bluexin.
 *
 * @author Bluexin
 */
public class GLString extends GLRectangle {

    @XmlJavaTypeAdapter(value = ExpressionAdapter.StringExpressionAdapter.class)
    protected StringExpressionWrapper text;

    private boolean shadow = true;

    private GLString() {
    }

    @Override
    public void draw(HudDrawContext ctx) {
        ElementParent p = this.parent.get();
        if (p != null) {
            double x = this.x.execute(ctx) + p.getX(ctx);
            double y = this.y.execute(ctx) + p.getY(ctx) + (this.h.execute(ctx) - ctx.getMc().fontRendererObj.FONT_HEIGHT) / 2.0D;

            GLCore.glString(ctx.getMc().fontRendererObj, this.text.execute(ctx), (int) x, (int) y, rgba.execute(ctx), shadow);
        }
    }
}
