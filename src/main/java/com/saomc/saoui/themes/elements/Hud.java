package com.saomc.saoui.themes.elements;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.HashMap;
import java.util.Map;

/**
 * Part of saoui by Bluexin.
 *
 * @author Bluexin
 */
@XmlRootElement
public class Hud {

    private Map<HudPartType, HudPart> parts = new HashMap<>();

    public HudPart get(HudPartType key) {
        return parts.get(key);
    }

    public void setup() {
        this.parts.values().forEach(HudPart::setup);
    }
}
