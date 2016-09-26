package com.saomc.saoui.elements;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.saomc.saoui.api.screens.GuiSelection;
import com.saomc.saoui.util.LogCore;

import java.util.*;

/**
 * Sends the elements to the correct classes to be rendered
 * <p>
 * Created by Tencao on 30/07/2016.
 */
public class ElementDispatcher {

    public final List<ListCore> menuElements = new ArrayList<>();

    public ElementDispatcher(ParentElement parent, GuiSelection gui) {
        //FIXME - Optimize all of this
        //Elements need to be sent in groups to the ListCore, not together
        //Groups are done via parent category, as seen in the ElementBuilder MultiMap
        if (!menuElements.isEmpty()){
            LogCore.logWarn("Gui - " + gui + " called ElementDispatcher when list isn't empty \n" +
                    "Either list wasn't cleaned or it was called early\n" +
                    "List will now be cleaned");
            menuElements.clear();
        }
        if (menuElements.isEmpty()) {

            LinkedHashMultimap<String, Element> elementSort = LinkedHashMultimap.create();
            for (Element entry : ElementProvider.instance().getBuilder().getforGui(gui)) {
                elementSort.put(entry.getParent(), entry);
            }
            for (Map.Entry<String, Collection<Element>> entry : elementSort.asMap().entrySet())
                if (!entry.getKey().equals("null")) {
                    ListCore list = new ListCore(parent);
                    for (Element element : entry.getValue()) {
                        list.elements.add(element);
                        element.setParentElement(parent);
                    }
                    menuElements.add(list);
                }
        }
    }
}
