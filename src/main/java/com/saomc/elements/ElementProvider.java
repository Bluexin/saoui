package com.saomc.elements;

import com.saomc.api.screens.IElementBuilder;

/**
 * Created by Tencao on 04/08/2016.
 */
public class ElementProvider {

    private static ElementProvider instance;
    private final IElementBuilder builder;

    private ElementProvider(IElementBuilder builder) {
        this.builder = builder;
    }

    public static void init(IElementBuilder provider) {
        if (instance != null) throw new IllegalStateException("ElementProvider already initialized!");
        instance = new ElementProvider(provider);
    }

    public static ElementProvider instance() {
        if (instance == null) throw new IllegalStateException("ElementProvider not initialized!");
        return instance;
    }

    public IElementBuilder getBuilder() {
        return this.builder;
    }
}
