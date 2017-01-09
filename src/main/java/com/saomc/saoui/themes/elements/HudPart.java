package com.saomc.saoui.themes.elements;

import com.saomc.saoui.themes.util.DoubleExpressionWrapper;
import com.saomc.saoui.themes.util.HudDrawContext;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.ArrayList;
import java.util.List;

/**
 * Part of saoui by Bluexin.
 *
 * @author Bluexin
 */
public class HudPart implements ElementParent {

    protected DoubleExpressionWrapper x;
    protected DoubleExpressionWrapper y;
    protected DoubleExpressionWrapper z;
    @XmlElementWrapper(name = "calls")
    @XmlElementRef(type = GLRectangle.class)
    private List<Element> elements = new ArrayList<>();

    public HudPart(List<Element> elements) {
        this.elements = elements;
    }

    protected HudPart() {
    }

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
