package com.tencao.saoui.themes.util

import com.tencao.saoui.themes.util.typeadapters.JelType
import javax.xml.bind.annotation.XmlAttribute

class NamedExpressionIntermediate : ExpressionIntermediate() {
    @get:XmlAttribute
    var key: String = ""
    @get:XmlAttribute
    var type: JelType = JelType.ERROR
}

class Variables {
    var variable: MutableList<NamedExpressionIntermediate> = mutableListOf()
}
