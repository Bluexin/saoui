package com.saomc.saoui.elements

import com.saomc.saoui.api.screens.IElementBuilder

class ElementProvider private constructor(val builder: IElementBuilder) {
    companion object {

        private var instance: ElementProvider? = null

        fun init(provider: IElementBuilder) {
            if (instance != null) throw IllegalStateException("ElementProvider already initialized!")
            instance = ElementProvider(provider)
        }

        fun instance(): ElementProvider {
            if (instance == null) throw IllegalStateException("ElementProvider not initialized!")
            return instance!!
        }
    }
}
