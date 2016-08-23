package com.saomc.saoui.elements;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.saomc.saoui.api.screens.GuiSelection;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Sends the elements to the correct classes to be rendered
 * <p>
 * Created by Tencao on 30/07/2016.
 */
public class ElementDispatcher {

    public final Map<Element, ListCore> menuElements = new HashMap<>();

    public ElementDispatcher(ParentElement parent, GuiSelection gui) {
        Multimap<String, Element> elementSort = HashMultimap.create();
        for (Element entry : ElementProvider.instance().getBuilder().getforGui(gui)){
            elementSort.put(entry.getParent(), entry);
        }
        for (Map.Entry<String, Collection<Element>> entry : elementSort.asMap().entrySet())
            if (!entry.getKey().equals("null")) {
                ListCore list = new ListCore(parent);
                for (Element element : entry.getValue()) {
                    list.elements.add(element);
                    element.setParentElement(parent);
                    menuElements.put(element, list);
                }
            }
    }
}
