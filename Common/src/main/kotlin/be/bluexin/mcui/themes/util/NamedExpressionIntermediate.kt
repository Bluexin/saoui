@file:OptIn(ExperimentalSerializationApi::class)

package be.bluexin.mcui.themes.util

import be.bluexin.mcui.themes.util.typeadapters.JelType
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlOtherAttributes
import nl.adaptivity.xmlutil.serialization.XmlSerialName
import nl.adaptivity.xmlutil.serialization.XmlValue

//@SerialName("variable")
@Serializable
data class NamedExpressionIntermediate(
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
    @XmlSerialName("variable")
    val variable: List<NamedExpressionIntermediate>
) {
    init {
        LibHelper.pushContext(variable.associate { it.key to it.type })
    }
}
