package com.saomc.saoui.themes.elements;

import com.saomc.saoui.GLCore;
import net.minecraft.util.ResourceLocation;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Part of saoui by Bluexin.
 *
 * @author Bluexin
 */
@SuppressWarnings("unused") // Needed for XML loading
public class GLRectangle implements Element {
    @XmlJavaTypeAdapter(value = HexAdapter.class)
    protected Integer rgba;
    protected double x;
    protected double y;
    protected double z;
    protected double srcX;
    protected double srcY;
    protected double w;
    protected double h;
    protected double srcW;
    protected double srcH;
    protected ResourceLocation rl;
    @XmlElementWrapper
    @XmlElement(name = "property")
    protected List<RenderingProperty> properties = new ArrayList<>();
    protected transient WeakReference<ElementParent> parent;
    private String texture;

    public GLRectangle(String texture, double w, double h) {
        this(texture, 0, 0, w, h);
    }

    public GLRectangle(String texture, double x, double y, double w, double h) {
        this(texture, x, y, 0, 0, w, h);
    }

    public GLRectangle(String texture, double x, double y, double srcX, double srcY, double w, double h) {
        this(texture, x, y, srcX, srcY, w, h, w, h);
    }

    public GLRectangle(String texture, double x, double y, double srcX, double srcY, double w, double h, double srcW, double srcH) {
        this(texture, 0xFFFFFFFF, x, y, 0, srcX, srcY, w, h, srcW, srcH);
    }

    public GLRectangle(String texture, int rgba, double x, double y, double z, double srcX, double srcY, double w, double h, double srcW, double srcH) {
        this.texture = texture;
        this.rgba = rgba;
        this.x = x;
        this.y = y;
        this.z = z;
        this.srcX = srcX;
        this.srcY = srcY;
        this.w = w;
        this.h = h;
        this.srcW = srcW;
        this.srcH = srcH;
    }

    protected GLRectangle() {
    }

    public void draw(DrawContext ctx) {
        ElementParent p = this.parent.get();
        if (p != null) {

            double x = this.x + p.getX();
            double y = this.y + p.getY();
            double z = this.z + p.getZ() + ctx.getZ();

            if (this.properties.contains(RenderingProperty.USERNAME_OFFSET_POS)) x += ctx.getUsernameWidth();
            else if (this.properties.contains(RenderingProperty.USERNAME_OFFSET_NEG)) x -= ctx.getUsernameWidth();

            GLCore.glColorRGBA(this.rgba);
            GLCore.glBindTexture(this.rl);
            GLCore.glTexturedRect(x, y, z, this.properties.contains(RenderingProperty.USERNAME) ? ctx.getUsernameWidth() : w, h, srcX, srcY, srcW, srcH);
        }
    }

    @Override
    public void setup(ElementParent parent) {
        if (this.texture != null) this.rl = new ResourceLocation(this.texture);
        this.parent = new WeakReference<>(parent);
    }
}
