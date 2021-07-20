package com.saomc.saoui.api.elements.old

@Deprecated("Waiting for update")
interface IAnimatable<T> {
    fun get(target: T): Any
    fun set(target: T, value: Any)
    fun doesInvolve(target: T, obj: Any): Boolean

    val type: Class<Any>
}
