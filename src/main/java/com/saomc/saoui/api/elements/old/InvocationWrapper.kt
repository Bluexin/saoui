package com.saomc.saoui.api.elements.old

import java.lang.invoke.MethodHandle

/**
 * @author WireSegal
 * Created at 7:38 PM on 10/22/16.
 */
/*package-private*/
@Deprecated("Update with LibrarianLib")
internal class InvocationWrapper     /*package-private*/(private val handle: MethodHandle) {
    /*package-private*/
    @Throws(Throwable::class)
    operator fun invoke(): Any {
        return handle.invokeExact()
    }

    /*package-private*/
    @Throws(Throwable::class)
    operator fun invoke(obj: Any?): Any {
        return handle.invokeExact(obj)
    }

    /*package-private*/
    @Throws(Throwable::class)
    operator fun invoke(obj: Any?, second: Any?): Any {
        return handle.invokeExact(obj, second)
    }

    /*package-private*/
    @Throws(Throwable::class)
    fun invokeArity(args: Array<Any?>): Any {
        return handle.invokeExact(*args)
    }
}
