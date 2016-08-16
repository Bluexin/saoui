package com.saomc.elements;

import com.saomc.api.screens.GuiSelection;

import java.util.HashMap;
import java.util.Map;

/**
 * Sends the elements to the correct classes to be rendered
 * <p>
 * Created by Tencao on 30/07/2016.
 */
public class ElementDispatcher {

    public static Map<Elements, MenuCore> menuElements = new HashMap<>();

    public void dispatch(ParentElement parent, GuiSelection gui) {
        ElementBuilder.getInstance().getforGui(gui).stream().forEach(elements ->
                menuElements.put(elements, new MenuCore(parent, elements))
        );
    }
}
