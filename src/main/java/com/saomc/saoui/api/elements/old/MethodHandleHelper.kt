package com.saomc.saoui.api.elements.old

import net.minecraftforge.fml.common.ObfuscationReflectionHelper
import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Method

/**
 * @author WireSegal
 * Created at 6:49 PM on 8/14/16.
 */
object MethodHandleHelper {

    //region base

    /**
     * Reflects a method from a class, and provides a MethodHandle for it.
     * Methodhandles MUST be invoked from java code, due to the way [@PolymorphicSignature] works.
     */
    @JvmStatic
    fun <T : Any> handleForMethod(clazz: Class<T>, obfName: String, vararg methodClasses: Class<*>): MethodHandle {
        @Suppress("DEPRECATION")
        val m = ObfuscationReflectionHelper.findMethod(clazz, obfName, *methodClasses)
        return MethodHandles.publicLookup().unreflect(m)
    }

    /**
     * Reflects a field from a class, and provides a MethodHandle for it.
     * MethodHandles MUST be invoked from java code, due to the way [@PolymorphicSignature] works.
     */
    @JvmStatic
    fun <T : Any> handleForField(clazz: Class<T>, getter: Boolean, fieldName: String): MethodHandle {
        val f = ObfuscationReflectionHelper.findField(clazz, fieldName)
        return if (getter) MethodHandles.publicLookup().unreflectGetter(f) else MethodHandles.publicLookup().unreflectSetter(f)
    }

    /**
     * Reflects a constructor from a class, and provides a MethodHandle for it.
     * MethodHandles MUST be invoked from java code, due to the way [@PolymorphicSignature] works.
     */
    @JvmStatic
    fun <T : Any> handleForConstructor(clazz: Class<T>, vararg constructorArgs: Class<*>): MethodHandle {
        val c = try {
            val m = clazz.getDeclaredConstructor(*constructorArgs)
            m.isAccessible = true
            m
        } catch (e: Exception) {
            throw ObfuscationReflectionHelper.UnableToFindMethodException(e)
        }

        return MethodHandles.publicLookup().unreflectConstructor(c)
    }

    //endregion

    //region getter

    /**
     * Reflects a getter from a class, and provides a wrapper for it.
     * No casts are required to use this, although they are recommended.
     */
    @JvmStatic
    fun <T : Any> wrapperForGetter(clazz: Class<T>, fieldName: String): (T) -> Any? {
        val handle = handleForField(clazz, true, fieldName)
        return wrapperForGetter(handle)
    }

    /**
     * Provides a wrapper for an existing MethodHandle getter.
     * No casts are required to use this, although they are recommended.
     */
    @JvmStatic
    fun <T : Any> wrapperForGetter(handle: MethodHandle): (T) -> Any? {
        val wrapper = InvocationWrapper(handle.asType(MethodType.genericMethodType(1)))
        return { wrapper(it) }
    }

    @JvmStatic
    fun <T : Any> wrapperForGetter(field: Field): (T) -> Any? = wrapperForGetter(MethodHandles.publicLookup().unreflectGetter(field))

    /**
     * Reflects a static getter from a class, and provides a wrapper for it.
     * No casts are required to use this, although they are recommended.
     */
    @JvmStatic
    fun wrapperForStaticGetter(clazz: Class<*>, fieldName: String): () -> Any? {
        val handle = handleForField(clazz, true, fieldName)
        return wrapperForStaticGetter(handle)
    }

    /**
     * Provides a wrapper for an existing static MethodHandle getter.
     * No casts are required to use this, although they are recommended.
     */
    @JvmStatic
    fun wrapperForStaticGetter(handle: MethodHandle): () -> Any? {
        val wrapper = InvocationWrapper(handle.asType(MethodType.genericMethodType(0)))
        return { wrapper() }
    }

    @JvmStatic
    fun wrapperForStaticGetter(field: Field): () -> Any? = wrapperForStaticGetter(MethodHandles.publicLookup().unreflectGetter(field))

    //endregion

    //region setter

    /**
     * Reflects a setter from a class, and provides a wrapper for it.
     */
    @JvmStatic
    fun <T : Any> wrapperForSetter(clazz: Class<T>, fieldName: String): (T, Any?) -> Unit {
        val handle = handleForField(clazz, false, fieldName)
        return wrapperForSetter(handle)
    }

    /**
     * Provides a wrapper for an existing MethodHandle setter.
     */
    @JvmStatic
    fun <T : Any> wrapperForSetter(handle: MethodHandle): (T, Any?) -> Unit {
        val wrapper = InvocationWrapper(handle.asType(MethodType.genericMethodType(2)))
        return { obj, value -> wrapper(obj, value) }
    }

    @JvmStatic
    fun <T : Any> wrapperForSetter(field: Field): (T, Any?) -> Unit = wrapperForSetter(MethodHandles.publicLookup().unreflectSetter(field))

    /**
     * Reflects a static setter from a class, and provides a wrapper for it.
     */
    @JvmStatic
    fun wrapperForStaticSetter(clazz: Class<*>, fieldName: String): (Any?) -> Unit {
        val handle = handleForField(clazz, false, fieldName)
        return wrapperForStaticSetter(handle)
    }

    /**
     * Provides a wrapper for an existing static MethodHandle setter.
     */
    @JvmStatic
    fun wrapperForStaticSetter(handle: MethodHandle): (Any?) -> Unit {
        val wrapper = InvocationWrapper(handle.asType(MethodType.genericMethodType(1)))
        return { wrapper(it) }
    }

    @JvmStatic
    fun wrapperForStaticSetter(field: Field): (Any?) -> Unit = wrapperForStaticSetter(MethodHandles.publicLookup().unreflectSetter(field))

    //endregion

    //region methods

    /**
     * Reflects a method from a class, and provides a wrapper for it.
     */
    @JvmStatic
    fun <T : Any> wrapperForMethod(clazz: Class<T>, methodName: String, vararg methodClasses: Class<*>): (T, Array<Any?>) -> Any? {
        val handle = handleForMethod(clazz, methodName, *methodClasses)
        return wrapperForMethod(handle)
    }

    /**
     * Provides a wrapper for an existing MethodHandle method wrapper.
     */
    @JvmStatic
    fun <T : Any> wrapperForMethod(handle: MethodHandle): (T, Array<Any?>) -> Any? {
        val type = handle.type()
        val count = type.parameterCount()
        var remapped = handle.asType(MethodType.genericMethodType(count))

        if (count > 1)
            remapped = remapped.asSpreader(Array<Any>::class.java, count)

        val wrapper = InvocationWrapper(remapped)
        if (count == 1)
            return { obj, _ -> wrapper(obj) }

        return { obj, args -> wrapper.invokeArity(arrayOf(obj, *args)) }
    }

    @JvmStatic
    fun <T : Any> wrapperForMethod(method: Method): (T, Array<Any?>) -> Any? = wrapperForMethod(
        MethodHandles.publicLookup().unreflect(method))

    /**
     * Reflects a static method from a class, and provides a wrapper for it.
     */
    @JvmStatic
    fun wrapperForStaticMethod(clazz: Class<*>, methodName: String, vararg methodClasses: Class<*>): (Array<Any?>) -> Any? {
        val handle = handleForMethod(clazz, methodName, *methodClasses)
        return wrapperForStaticMethod(handle)
    }

    /**
     * Provides a wrapper for an existing MethodHandle method wrapper.
     */
    @JvmStatic
    fun wrapperForStaticMethod(handle: MethodHandle): (Array<Any?>) -> Any? {
        val type = handle.type()
        val count = type.parameterCount()
        val wrapper = InvocationWrapper(handle.asType(MethodType.genericMethodType(count)).asSpreader(Array<Any>::class.java, count))
        return { wrapper.invokeArity(it) }
    }

    @JvmStatic
    fun wrapperForStaticMethod(method: Method): (Array<Any?>) -> Any? = wrapperForStaticMethod(
        MethodHandles.publicLookup().unreflect(method))

    //endregion

    //region constructors

    /**
     * Reflects a constructor from a class, and provides a wrapper for it.
     */
    @JvmStatic
    fun <T : Any> wrapperForConstructor(clazz: Class<*>, vararg constructorArgs: Class<*>): (Array<Any?>) -> T {
        val handle = handleForConstructor(clazz, *constructorArgs)
        return wrapperForConstructor(handle)
    }

    /**
     * Provides a wrapper for an existing MethodHandle constructor wrapper.
     */
    @Suppress("UNCHECKED_CAST")
    @JvmStatic
    fun <T : Any> wrapperForConstructor(handle: MethodHandle): (Array<Any?>) -> T {
        val type = handle.type()
        val count = type.parameterCount()
        val wrapper = InvocationWrapper(handle.asType(MethodType.genericMethodType(count)).asSpreader(Array<Any>::class.java, count))
        return { wrapper.invokeArity(it) as T }
    }

    @JvmStatic
    fun <T : Any> wrapperForConstructor(constructor: Constructor<T>): (Array<Any?>) -> T {
        constructor.isAccessible = true
        return wrapperForConstructor(MethodHandles.publicLookup().unreflectConstructor(constructor))
    }

    //endregion

    //region delegates

    @JvmStatic
    fun <T : Any, V> delegateForReadOnly(clazz: Class<T>, fieldName: String): ImmutableFieldDelegate<T, V> {
        val getter = wrapperForGetter(clazz, fieldName)
        return ImmutableFieldDelegate(getter)
    }

    @JvmStatic
    fun <T : Any, V> delegateForReadWrite(clazz: Class<T>, fieldName: String): MutableFieldDelegate<T, V> {
        val getter = wrapperForGetter(clazz, fieldName)
        val setter = wrapperForSetter(clazz, fieldName)
        return MutableFieldDelegate(getter, setter)
    }

    @JvmStatic
    fun <T : Any, V> delegateForStaticReadOnly(clazz: Class<*>, fieldName: String): ImmutableStaticFieldDelegate<T, V> {
        val getter = wrapperForStaticGetter(clazz, fieldName)
        return ImmutableStaticFieldDelegate(getter)
    }

    @JvmStatic
    fun <T : Any, V> delegateForStaticReadWrite(clazz: Class<*>, fieldName: String): MutableStaticFieldDelegate<T, V> {
        val getter = wrapperForStaticGetter(clazz, fieldName)
        val setter = wrapperForStaticSetter(clazz, fieldName)
        return MutableStaticFieldDelegate(getter, setter)
    }

    //endregion
}

//region extensions

@Suppress("UNCHECKED_CAST")
fun <T : Any, V> Class<T>.mhGetter(name: String): (T) -> V = MethodHandleHelper.wrapperForGetter(this, name) as (T) -> V

@Suppress("UNCHECKED_CAST")
fun <T : Any, V> Class<T>.mhSetter(name: String): (T, V) -> Unit = MethodHandleHelper.wrapperForSetter(this, name)

@Suppress("UNCHECKED_CAST")
fun <V> Class<*>.mhStaticGetter(name: String): () -> V = MethodHandleHelper.wrapperForStaticGetter(this, name) as () -> V

@Suppress("UNCHECKED_CAST")
fun <V> Class<*>.mhStaticSetter(name: String): (V) -> Unit = MethodHandleHelper.wrapperForStaticSetter(this, name)

fun <T : Any, V> Class<T>.mhValDelegate(name: String) = MethodHandleHelper.delegateForReadOnly<T, V>(this, name)
fun <T : Any, V> Class<T>.mhVarDelegate(name: String) = MethodHandleHelper.delegateForReadWrite<T, V>(this, name)

fun <T : Any, V> Class<T>.mhStaticValDelegate(name: String) = MethodHandleHelper.delegateForStaticReadOnly<T, V>(this, name)
fun <T : Any, V> Class<T>.mhStaticVarDelegate(name: String) = MethodHandleHelper.delegateForStaticReadWrite<T, V>(this, name)

fun <T : Any> Class<T>.mhMethod(name: String, vararg params: Class<*>) = MethodHandleHelper.wrapperForMethod(this, name, *params)
fun <T : Any> Class<T>.mhStaticMethod(name: String, vararg params: Class<*>) = MethodHandleHelper.wrapperForStaticMethod(this, name, *params)

//endregion
