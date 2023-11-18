package be.bluexin.mcui.themes.elements

import be.bluexin.mcui.Constants
import be.bluexin.mcui.themes.util.AfterUnmarshal
import be.bluexin.mcui.themes.util.LibHelper
import be.bluexin.mcui.themes.util.NamedExpressionIntermediate
import be.bluexin.mcui.themes.util.json.ExpectJsonAdapter
import com.google.gson.annotations.JsonAdapter
import jakarta.xml.bind.Unmarshaller
import jakarta.xml.bind.annotation.XmlElement
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.ExperimentalXmlUtilApi
import nl.adaptivity.xmlutil.serialization.XmlNamespaceDeclSpec
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@OptIn(ExperimentalXmlUtilApi::class)
@Serializable
@XmlSerialName(
//    namespace = "https://www.bluexin.be/com/saomc/saoui/fragment-schema",
//    prefix = "bl",
    value = "bl:fragment"
)
@XmlNamespaceDeclSpec("bl=https://www.bluexin.be/com/saomc/saoui/fragment-schema")
class Fragment(
    @XmlSerialName("expect")
//    @XmlBefore("children")
    val expect: Expect? = null
) : ElementGroupParent(), AfterUnmarshal {

    override fun afterUnmarshal(um: Unmarshaller?, parent: Any?) {
        Constants.LOG.info("afterUnmarshal in $name of $parent with $expect")
        if (expect != null) LibHelper.popContext()
    }
}

@JsonAdapter(ExpectJsonAdapter::class)
@Serializable
//@SerialName("expect")
data class Expect(
    @field:XmlElement(name = "variable")
    @XmlSerialName("variable")
    val variables: List<NamedExpressionIntermediate> = mutableListOf()
) : AfterUnmarshal {
    override fun toString(): String {
        return "Expect(variable=$variables)"
    }

    override fun afterUnmarshal(um: Unmarshaller?, parent: Any?) {
        Constants.LOG.info("afterUnmarshal in $this of $parent")
        LibHelper.pushContext(variables.associate { it.key to it.type })
    }
}
