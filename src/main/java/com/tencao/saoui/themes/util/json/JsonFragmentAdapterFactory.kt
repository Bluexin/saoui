package com.tencao.saoui.themes.util.json

import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import com.tencao.saoui.themes.elements.Fragment
import com.tencao.saoui.themes.util.LibHelper

class JsonFragmentAdapterFactory : TypeAdapterFactory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> create(gson: Gson, type: TypeToken<T>): TypeAdapter<T>? =
        if (Fragment::class.java.isAssignableFrom(type.rawType)) JsonFragmentAdapter(
            gson.getDelegateAdapter(this, type) as TypeAdapter<Fragment>
        ) as TypeAdapter<T>
        else null
}

class JsonFragmentAdapter(
    private val gson: TypeAdapter<Fragment>
) : TypeAdapter<Fragment>() {

    override fun write(out: JsonWriter, value: Fragment) {
        gson.write(out, value)
    }

    override fun read(reader: JsonReader): Fragment {
        val fragment = gson.read(reader)

        LibHelper.popContext()
        return fragment
    }
}