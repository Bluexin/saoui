package com.saomc.saoui.themes.elements;

import com.saomc.saoui.GLCore;
import com.saomc.saoui.themes.util.DoubleExpressionWrapper;
import com.saomc.saoui.themes.util.ExpressionAdapter;
import com.saomc.saoui.themes.util.IntExpressionWrapper;
import net.minecraft.util.ResourceLocation;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.lang.ref.WeakReference;

/**
 * Part of saoui by Bluexin.
 *
 * @author Bluexin
 */
@SuppressWarnings("unused") // Needed for XML loading
public class GLRectangle implements Element {
    @XmlJavaTypeAdapter(value = ExpressionAdapter.IntExpressionAdapter.class)
    protected IntExpressionWrapper rgba;
    @XmlJavaTypeAdapter(value = ExpressionAdapter.DoubleExpressionAdapter.class)
    protected DoubleExpressionWrapper x;
    @XmlJavaTypeAdapter(value = ExpressionAdapter.DoubleExpressionAdapter.class)
    protected DoubleExpressionWrapper y;
    @XmlJavaTypeAdapter(value = ExpressionAdapter.DoubleExpressionAdapter.class)
    protected DoubleExpressionWrapper z;
    @XmlJavaTypeAdapter(value = ExpressionAdapter.DoubleExpressionAdapter.class)
    protected DoubleExpressionWrapper srcX;
    @XmlJavaTypeAdapter(value = ExpressionAdapter.DoubleExpressionAdapter.class)
    protected DoubleExpressionWrapper srcY;
    @XmlJavaTypeAdapter(value = ExpressionAdapter.DoubleExpressionAdapter.class)
    protected DoubleExpressionWrapper w;
    @XmlJavaTypeAdapter(value = ExpressionAdapter.DoubleExpressionAdapter.class)
    protected DoubleExpressionWrapper h;
    @XmlJavaTypeAdapter(value = ExpressionAdapter.DoubleExpressionAdapter.class)
    protected DoubleExpressionWrapper srcW;
    @XmlJavaTypeAdapter(value = ExpressionAdapter.DoubleExpressionAdapter.class)
    protected DoubleExpressionWrapper srcH;
    protected ResourceLocation rl;
    protected transient WeakReference<ElementParent> parent;
    private String texture;

    protected GLRectangle() {
    }

    public void draw(HudDrawContext ctx) {
        ElementParent p = this.parent.get();
        if (p != null) {
            double x = this.x.execute(ctx) + p.getX(ctx);
            double y = this.y.execute(ctx) + p.getY(ctx);
            double z = this.z.execute(ctx) + p.getZ(ctx) + ctx.getZ();

            GLCore.glBlend(true);
            GLCore.glColorRGBA(this.rgba.execute(ctx));
            GLCore.glBindTexture(this.rl);
            GLCore.glTexturedRect(x, y, z, w.execute(ctx), h.execute(ctx), srcX.execute(ctx), srcY.execute(ctx), srcW.execute(ctx), srcH.execute(ctx));
        }
    }

    @Override
    public void setup(ElementParent parent) {
        if (this.texture != null) this.rl = new ResourceLocation(this.texture);
        this.parent = new WeakReference<>(parent);
    }
}
