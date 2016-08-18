package com.saomc.events;

import com.saomc.api.events.ElementAction;
import com.saomc.api.screens.Actions;
import com.saomc.elements.ElementBuilder;

/**
 * This handles and controls the default event, and our custom events for slots
 *
 * Created by Tencao on 18/08/2016.
 */
public class ElementHandler {

    protected static void defaultActions(ElementAction e){
            if (e.getAction() == Actions.LEFT_RELEASED) {
                if (e.isOpen()) ElementBuilder.getInstance().disableChildElements(e.getCategory(), e.getGui());
                else ElementBuilder.getInstance().enableChildElements(e.getCategory(), e.getGui());
            }
    }
}
