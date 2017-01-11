package com.saomc.saoui.themes.util;

import com.saomc.saoui.api.info.IOption;
import com.saomc.saoui.api.themes.IHudDrawContext;
import com.saomc.saoui.config.OptionCore;
import com.saomc.saoui.screens.ingame.HealthStep;
import com.saomc.saoui.themes.elements.ElementParent;
import gnu.jel.CompilationException;
import gnu.jel.Library;
import net.minecraft.client.resources.I18n;

/**
 * Part of saoui by Bluexin.
 *
 * @author Bluexin
 */
public final class LibHelper {
    public final static Library LIB;

    static {
        Class[] staticLib = new Class[]{
                Math.class,
                HealthStep.class,
                OptionCore.class,
                I18n.class
        };
        Class[] dynLib = new Class[]{
                IHudDrawContext.class,
                ElementParent.class
        };
        Class[] dotClasses = new Class[]{
                String.class,
                IOption.class
        };
        LIB = new Library(staticLib, dynLib, dotClasses, null, null);
        try {
            LIB.markStateDependent("random", null);
        } catch (CompilationException e) {
            e.printStackTrace();
        }
    }
}
