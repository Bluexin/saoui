package com.tencao.saoui.themes.util.json

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import com.tencao.saoui.themes.AbstractThemeLoader
import com.tencao.saoui.themes.elements.Expect
import com.tencao.saoui.themes.elements.Variable
import com.tencao.saoui.themes.util.*
import com.tencao.saoui.themes.util.typeadapters.JelType
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class ExpectJsonAdapter : TypeAdapter<Expect>() {
    private val logger: Logger by lazy { LogManager.getLogger(javaClass) }

    override fun write(out: JsonWriter, value: Expect?) {
        if (value == null || value.variables.isEmpty()) out.nullValue()
        else {
            out.beginObject()
            value.variables.forEach {
                out.name(it.key)
                out.value(it.type.name)
            }
            out.endObject()
        }
    }

    override fun read(reader: JsonReader): Expect? = when (val nextToken = reader.peek()) {
        JsonToken.BEGIN_OBJECT -> {
            reader.beginObject()
            val e = Expect(
                buildList {
                    while (reader.hasNext()) {
                        add(Variable().apply {
                            key = reader.nextName()
                            type = reader.nextString().let(JelType::valueOf)
                        })
                    }
                }
            )
            e.afterUnmarshal()
            reader.endObject()
            e
        }
        JsonToken.NULL -> null
        else -> {
            val message = "Unable to deserialize ${reader.path} : Unexpected token $nextToken"
            logger.warn(message)
            AbstractThemeLoader.Reporter += message
            null
        }
    }
}