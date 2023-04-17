package be.bluexin.mcui.themes.util.json

import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import be.bluexin.mcui.themes.util.AfterUnmarshal

class AfterUnmarshalAdapterFactory : TypeAdapterFactory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> create(gson: Gson, type: TypeToken<T>): TypeAdapter<T>? =
        if (AfterUnmarshal::class.java.isAssignableFrom(type.rawType)) JsonFragmentAdapter(
            gson.getDelegateAdapter(this, type) as TypeAdapter<AfterUnmarshal>
        ) as TypeAdapter<T>
        else null
}

class JsonFragmentAdapter<T: AfterUnmarshal>(
    private val gson: TypeAdapter<T>
) : TypeAdapter<T>() {

    override fun write(out: JsonWriter, value: T) = gson.write(out, value)

    override fun read(reader: JsonReader) = gson.read(reader)?.apply(AfterUnmarshal::afterUnmarshal)
}