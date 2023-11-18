package be.bluexin.mcui.themes.util.serialization

import be.bluexin.mcui.themes.util.*
import be.bluexin.mcui.themes.util.typeadapters.JelType
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@OptIn(ExperimentalSerializationApi::class)
class NamedExpressionSerializer : KSerializer<Map<String, CValue<*>?>> {
    private val delegate = Variables.serializer()

    override fun deserialize(decoder: Decoder): Map<String, CValue<*>?> = decoder.decodeSerializableValue(delegate)
        .variable.associate {
            it.key to if (it.expression.isEmpty()) null else it.type.expressionAdapter.compile(it)
        }

    override val descriptor by lazy {
        SerialDescriptor(javaClass.canonicalName, delegate.descriptor)
    }

    override fun serialize(encoder: Encoder, value: Map<String, CValue<*>?>) = encoder.encodeSerializableValue(
        delegate, Variables(
            value.map { (key, value) ->
                val nei = value?.value?.expressionIntermediate as? NamedExpressionIntermediate
                NamedExpressionIntermediate(
                    key = key,
                    type = nei?.type ?: JelType.ERROR,
                    serializedExpression = nei?.serializedExpression.orEmpty(),
                    cacheType = nei?.cacheType ?: CacheType.PER_FRAME,
                )
            }
        )
    )
}