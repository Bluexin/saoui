package be.bluexin.mcui.themes.util

import be.bluexin.mcui.SAOCore
import be.bluexin.mcui.themes.util.typeadapters.JelType
import javax.xml.bind.Unmarshaller
import javax.xml.bind.annotation.XmlAttribute

class NamedExpressionIntermediate : ExpressionIntermediate() {
    @get:XmlAttribute
    var key: String = ""
    @get:XmlAttribute
    var type: JelType = JelType.ERROR

    override fun toString(): String {
        return "NamedExpressionIntermediate(key='$key', type='$type')"
    }

    fun hasDefault() = expression.isNotEmpty()
}

class Variables {
    var variable: MutableList<NamedExpressionIntermediate> = mutableListOf()

    @Suppress("unused")
    fun afterUnmarshal(um: Unmarshaller? = null, parent: Any? = null) {
        Constants.LOG.info("afterUnmarshal in $this of $parent")
        if (parent == null) LibHelper.pushContext(variable.associate { it.key to it.type })
    }
}
