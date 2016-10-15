package com.saomc.saoui.themes.elements;

import com.saomc.saoui.GLCore;

/**
 * Part of saoui by Bluexin.
 *
 * @author Bluexin
 */
public class GLString extends GLRectangle {
    private String text;

    private boolean shadow = true;

    private GLString() {

    }

    public GLString(String s) {
        this.text = s;
    }

    @Override
    public void draw(DrawContext ctx) {
        ElementParent p = this.parent.get();
        if (p != null) {

            double x = this.x + p.getX();
            double y = this.y + p.getY() + (this.h - ctx.getMc().fontRendererObj.FONT_HEIGHT) / 2.0D;

            GLCore.glString(ctx.getMc().fontRendererObj, this.properties.contains(RenderingProperty.USERNAME) ? ctx.getUsername() : this.text, (int) x, (int) y, rgba, shadow);
        }
    }
}
