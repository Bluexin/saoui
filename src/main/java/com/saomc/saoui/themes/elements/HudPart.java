package com.saomc.saoui.themes.elements;

import com.saomc.saoui.themes.util.DoubleExpressionWrapper;
import com.saomc.saoui.themes.util.ExpressionAdapter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;
import java.util.List;

/**
 * Part of saoui by Bluexin.
 *
 * @author Bluexin
 */
public class HudPart implements ElementParent {

    @XmlJavaTypeAdapter(value = ExpressionAdapter.DoubleExpressionAdapter.class)
    protected DoubleExpressionWrapper x;
    @XmlJavaTypeAdapter(value = ExpressionAdapter.DoubleExpressionAdapter.class)
    protected DoubleExpressionWrapper y;
    @XmlJavaTypeAdapter(value = ExpressionAdapter.DoubleExpressionAdapter.class)
    protected DoubleExpressionWrapper z;
    @XmlElementWrapper(name = "calls")
    @XmlElement(name = "element")
    private List<Element> elements = new ArrayList<>();

    public double getX(HudDrawContext ctx) {
        return x.execute(ctx);
    }

    public double getY(HudDrawContext ctx) {
        return y.execute(ctx);
    }

    public double getZ(HudDrawContext ctx) {
        return z.execute(ctx);
    }

    void setup() {
        this.elements.forEach(e -> e.setup(this));
    }

    public void draw(HudDrawContext ctx) {
        this.elements.forEach(e -> e.draw(ctx));
    }
}
