@file:OptIn(ExperimentalSerializationApi::class)

package be.bluexin.mcui.themes.util

import be.bluexin.mcui.Constants
import be.bluexin.mcui.themes.util.typeadapters.JelType
import jakarta.xml.bind.Unmarshaller
import jakarta.xml.bind.annotation.XmlAttribute
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlOtherAttributes
import nl.adaptivity.xmlutil.serialization.XmlValue

//@SerialName("variable")
@Serializable
data class NamedExpressionIntermediate(
    @XmlAttribute
    val key: String,
    @XmlOtherAttributes
    val type: JelType = JelType.ERROR,
    @XmlValue
    @SerialName("expression")
    override val serializedExpression: String = "",
    @SerialName("cache")
    @XmlOtherAttributes
    override val cacheType: CacheType = CacheType.PER_FRAME
) : ExpressionIntermediate() {

    fun hasDefault() = serializedExpression.isNotEmpty()
}

@Serializable
data class Variables(
    val variable: List<NamedExpressionIntermediate>
) {
    @Suppress("unused")
    fun afterUnmarshal(um: Unmarshaller? = null, parent: Any? = null) {
        Constants.LOG.info("afterUnmarshal in $this of $parent")
        if (parent == null) LibHelper.pushContext(variable.associate { it.key to it.type })
    }
}
