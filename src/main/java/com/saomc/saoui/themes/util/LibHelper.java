package com.saomc.saoui.themes.util;

import com.saomc.saoui.screens.ingame.HealthStep;
import com.saomc.saoui.themes.elements.ElementParent;
import com.saomc.saoui.themes.elements.HudDrawContext;
import gnu.jel.CompilationException;
import gnu.jel.Library;

/**
 * Part of saoui by Bluexin.
 *
 * @author Bluexin
 */
public final class LibHelper {
    public final static Library lib;

    static {
        Class[] staticLib = new Class[]{
                Math.class,
                HealthStep.class
        };
        Class[] dynLib = new Class[]{
                HudDrawContext.class,
                ElementParent.class
        };
        Class[] dotClasses = new Class[]{
                String.class
        };
        lib = new Library(staticLib, dynLib, dotClasses, null, null);
        try {
            lib.markStateDependent("random", null);
        } catch (CompilationException e) {
            e.printStackTrace();
        }
    }
}
