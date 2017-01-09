package com.saomc.saoui.themes.elements;

import com.saomc.saoui.themes.util.HudDrawContext;

/**
 * Part of saoui by Bluexin.
 *
 * @author Bluexin
 */
public interface ElementParent {
    double getX(HudDrawContext ctx);

    double getY(HudDrawContext ctx);

    double getZ(HudDrawContext ctx);
}
