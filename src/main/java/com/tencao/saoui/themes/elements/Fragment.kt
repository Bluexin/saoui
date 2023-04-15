package com.tencao.saoui.themes.elements

import com.tencao.saoui.SAOCore
import com.tencao.saoui.themes.util.typeadapters.JelType
import com.tencao.saoui.themes.util.LibHelper
import javax.xml.bind.Unmarshaller
import javax.xml.bind.annotation.XmlAttribute
import javax.xml.bind.annotation.XmlRootElement

@XmlRootElement(namespace = "http://www.bluexin.be/com/saomc/saoui/fragment-schema")
class Fragment : ElementGroup() {
    var expect: Expect? = null
        private set

    @Suppress("unused")
    fun afterUnmarshal(um: Unmarshaller, parent: Any?) {
        SAOCore.LOGGER.info("afterUnmarshal in $name of $parent with $expect")
        if (expect != null) LibHelper.popContext()
    }
}

class Expect {
    private var variable: MutableList<Variable> = mutableListOf()

    val variables get(): List<Variable> = variable.toList()

    override fun toString(): String {
        return "Expect(variable=$variable)"
    }

    // TODO : handle JSON
    @Suppress("unused")
    fun afterUnmarshal(um: Unmarshaller, parent: Any?) {
        SAOCore.LOGGER.info("afterUnmarshal in $this of $parent")
        LibHelper.pushContext(variable.associate { it.key to it.type })
    }
}

open class Variable {
    @XmlAttribute
    lateinit var key: String
    @XmlAttribute
    lateinit var type: JelType

    override fun toString(): String {
        return "Variable(key='$key', type='$type')"
    }
}
