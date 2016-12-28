package com.saomc.saoui.themes.elements;

import com.saomc.saoui.GLCore;
import com.saomc.saoui.screens.ingame.HealthStep;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * Part of saoui by Bluexin.
 *
 * @author Bluexin
 */
public class HPBar extends GLRectangle {

    @XmlJavaTypeAdapter(value = HexArrayAdapter.class)
    private int[] colorSteps;

    protected HPBar() {
    }

    public HPBar(String texture, double w, double h) {
        this(texture, w, h, new int[]{0xBD0000FF, 0xF40000FF, 0xF47800FF, 0xF4BD00FF, 0xEDEB38FF, 0x93F43EFF, 0xB32DE3FF});
    }

    public HPBar(String texture, double w, double h, int[] colorSteps) {
        this(texture, 0, 0, w, h, colorSteps);
    }

    public HPBar(String texture, double x, double y, double w, double h, int[] colorSteps) {
        this(texture, x, y, 0, 0, w, h, colorSteps);
    }

    public HPBar(String texture, double x, double y, double srcX, double srcY, double w, double h, int[] colorSteps) {
        this(texture, x, y, srcX, srcY, w, h, w, h, colorSteps);
    }

    public HPBar(String texture, double x, double y, double srcX, double srcY, double w, double h, double srcW, double srcH, int[] colorSteps) {
        this(texture, 0xFFFFFFFF, x, y, 0, srcX, srcY, w, h, srcW, srcH, colorSteps);
    }

    public HPBar(String texture, int rgba, double x, double y, double z, double srcX, double srcY, double w, double h, double srcW, double srcH, int[] colorSteps) {
        super(texture, rgba, x, y, z, srcX, srcY, w, h, srcW, srcH);
        this.colorSteps = colorSteps;
    }

    @Override
    public void draw(HudDrawContext ctx) {
        ElementParent p = this.parent.get();
        if (p != null) {

            double x = this.x + p.getX();
            double y = this.y + p.getY();
            double z = this.z + p.getZ() + ctx.getZ();

            if (this.properties.contains(RenderingProperty.USERNAME_OFFSET_POS)) x += ctx.getUsernameWidth();
            else if (this.properties.contains(RenderingProperty.USERNAME_OFFSET_NEG)) x -= ctx.getUsernameWidth();

            GLCore.glColorRGBA(this.colorSteps[HealthStep.getStep((float) ctx.getHpPct()).ordinal()]);
            GLCore.glBindTexture(this.rl);
            GLCore.glTexturedRect(x, y, z, w * ctx.getHpPct(), h, srcX, srcY, srcW * ctx.getHpPct(), srcH);
        }
    }
}
