package com.saomc.saoui.themes.util

/**
 * Part of saoui by Bluexin.
 *
 * @author Bluexin
 */
enum class CacheType(private val provider: Function1<CompiledExpressionWrapper<*>, CachedExpression<*>>) {

    /**
     * Values will be cached per frame rendering.
     */
    DEFAULT({ FrameCachedExpression(it) }),

    /**
     * Values will be cached whenever they're first queried, and never updated.
     */
    STATIC({ StaticCachedExpression(it) }),

    /**
     * Values will be cached whenever a screen size change is detected.
     */
    SIZE_CHANGE({ SizeCachedExpression(it) }),

    /**
     * Values will not be cached (unrecommended -- in most cases DEFAULT is better. Use with precaution).
     */
    NONE({ UnCachedExpression(it) });

    @Suppress("UNCHECKED_CAST")
    fun <T> cacheExpression(expr: CompiledExpressionWrapper<T>) = provider.invoke(expr) as CachedExpression<T>
}
