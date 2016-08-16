package com.saomc.elements;

import com.saomc.api.screens.IElement;

/**
 * Created by Tencao on 04/08/2016.
 */
public class ElementProvider {

    private static ElementProvider instance;
    private final IElement elements;

    private ElementProvider(IElement elements) {
        this.elements = elements;
    }

    public static void init(IElement provider) {
        if (instance != null) throw new IllegalStateException("ElementProvider already initialized!");
        instance = new ElementProvider(provider);
    }

    public static ElementProvider instance() {
        if (instance == null) throw new IllegalStateException("ElementProvider not initialized!");
        return instance;
    }

    public IElement getElements() {
        return this.elements;
    }
}
