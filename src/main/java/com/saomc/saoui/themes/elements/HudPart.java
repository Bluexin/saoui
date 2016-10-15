package com.saomc.saoui.themes.elements;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.ArrayList;
import java.util.List;

/**
 * Part of saoui by Bluexin.
 *
 * @author Bluexin
 */
public class HudPart implements ElementParent {

    @XmlElementWrapper(name = "calls")
    @XmlElement(name = "element")
    private List<Element> elements = new ArrayList<>();
    private double x;
    private double y;
    private double z;

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    void setup() {
        this.elements.forEach(e -> e.setup(this));
    }

    public void draw(DrawContext ctx) {
        this.elements.forEach(e -> e.draw(ctx));
    }
}
