package com.tencao.saoui.themes.elements

import com.google.gson.annotations.JsonAdapter
import com.tencao.saoui.SAOCore
import com.tencao.saoui.themes.util.typeadapters.JelType
import com.tencao.saoui.themes.util.LibHelper
import com.tencao.saoui.themes.util.json.ExpectJsonAdapter
import com.tencao.saoui.themes.util.json.JsonFragmentAdapterFactory
import javax.xml.bind.Unmarshaller
import javax.xml.bind.annotation.XmlAttribute
import javax.xml.bind.annotation.XmlElement
import javax.xml.bind.annotation.XmlRootElement

@JsonAdapter(JsonFragmentAdapterFactory::class)
@XmlRootElement(namespace = "http://www.bluexin.be/com/saomc/saoui/fragment-schema")
class Fragment @JvmOverloads constructor(
    val expect: Expect? = null
) : ElementGroup() {

    @Suppress("unused")
    fun afterUnmarshal(um: Unmarshaller, parent: Any?) {
        SAOCore.LOGGER.info("afterUnmarshal in $name of $parent with $expect")
        if (expect != null) LibHelper.popContext()
    }
}

@JsonAdapter(ExpectJsonAdapter::class)
data class Expect @JvmOverloads constructor(
    @field:XmlElement(name = "variable")
    val variables: List<Variable> = mutableListOf()
) {
    override fun toString(): String {
        return "Expect(variable=$variables)"
    }

    // TODO : handle JSON
    @Suppress("unused")
    fun afterUnmarshal(um: Unmarshaller? = null, parent: Any? = null) {
        SAOCore.LOGGER.info("afterUnmarshal in $this of $parent")
        LibHelper.pushContext(variables.associate { it.key to it.type })
    }
}

class Variable {
    @XmlAttribute
    lateinit var key: String
    @XmlAttribute
    lateinit var type: JelType

    override fun toString(): String {
        return "Variable(key='$key', type='$type')"
    }
}
