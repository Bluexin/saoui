package com.saomc.saoui.elements;

import com.saomc.saoui.api.screens.GuiSelection;

import java.util.HashMap;
import java.util.Map;

/**
 * Sends the elements to the correct classes to be rendered
 * <p>
 * Created by Tencao on 30/07/2016.
 */
public class ElementDispatcher {

    public final Map<Element, MenuCore> menuElements = new HashMap<>();

    public ElementDispatcher(ParentElement parent, GuiSelection gui) {
        ElementProvider.instance().getBuilder().getforGui(gui).forEach(el -> menuElements.put(el, new MenuCore(parent, el)));
    }
}
