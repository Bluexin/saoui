package be.bluexin.mcui.compat

interface Configuration {
    fun get(namespace: String, path: String, write: String, comment: String?, type: Property.Type): Property
    fun save()
}

interface Property {
    enum class Type {
        STRING,
        BOOLEAN,
        INTEGER,
        DOUBLE,
    }

    val string: String
    fun set(value: String)
}
