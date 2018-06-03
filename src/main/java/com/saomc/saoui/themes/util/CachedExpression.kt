package com.saomc.saoui.themes.util

import com.saomc.saoui.api.themes.IHudDrawContext
import com.saomc.saoui.effects.StatusEffects
import com.saomc.saoui.screens.ingame.HealthStep

/**
 * Part of saoui by Bluexin.
 *
 * @author Bluexin
 */
abstract class CachedExpression<out T>(val expression: CompiledExpressionWrapper<T>) : Function1<IHudDrawContext, T> {
    protected abstract val cache: T?
}

class FrameCachedExpression<T>(expression: CompiledExpressionWrapper<T>) : CachedExpression<T>(expression) {
    override var cache: T? = null

    private var lastTime = -1.0F

    private fun checkUpdateTime(ctx: IHudDrawContext) = if (lastTime == ctx.partialTicks) false else {
        lastTime = ctx.partialTicks
        true
    }

    override fun invoke(ctx: IHudDrawContext): T {
        if (checkUpdateTime(ctx)) cache = expression.invoke(ctx)
        return cache!!
    }
}

class StaticCachedExpression<out T>(expression: CompiledExpressionWrapper<T>) : CachedExpression<T>(expression) {
    override val cache: T by lazy { expression.invoke(StubContext) }

    companion object StubContext : IHudDrawContext {
        override fun ptHealthStep(index: Int) = HealthStep.CREATIVE
        override fun ptName(index: Int) = ""
        override fun ptHp(index: Int) = 0f
        override fun ptMaxHp(index: Int) = 0f
        override fun ptHpPct(index: Int) = 0f
        override fun ptSize() = 0
        override fun setI(i: Int) = Unit
        override fun i() = 0
        override fun username() = null
        override fun usernamewidth() = 0.0
        override fun hpPct() = 0.0
        override fun hp() = 0f
        override fun maxHp() = 0f
        override fun healthStep() = null
        override fun selectedslot() = 0
        override fun scaledwidth() = 0
        override fun scaledheight() = 0
        override fun offhandEmpty(slot: Int) = false
        override fun strWidth(s: String) = 0
        override fun absorption() = 0f
        override fun level() = 0
        override fun experience() = 0f
        override fun getZ() = 0.0f
        override fun getFontRenderer() = null
        override fun getItemRenderer() = null
        override fun getPlayer() = null
        override fun getPartialTicks() = 0f
        override fun horsejump() = 0f
        override fun foodLevel() = 0f
        override fun saturationLevel() = 0f
        override fun statusEffects() = mutableListOf<StatusEffects>()
    }

    override fun invoke(ctx: IHudDrawContext) = cache
}

class SizeCachedExpression<T>(expression: CompiledExpressionWrapper<T>) : CachedExpression<T>(expression) {
    override var cache: T? = null

    var lastW = 0
    var lastH = 0

    private fun checkUpdateSize(ctx: IHudDrawContext) = if (lastW == ctx.scaledwidth() && lastH == ctx.scaledheight()) false else {
        lastW = ctx.scaledwidth()
        lastH = ctx.scaledheight()
        true
    }

    override fun invoke(ctx: IHudDrawContext): T {
        if (checkUpdateSize(ctx)) cache = expression.invoke(ctx)
        return cache!!
    }
}

class UnCachedExpression<out T>(expression: CompiledExpressionWrapper<T>) : CachedExpression<T>(expression) {
    override val cache: T?
        get() = throw UnsupportedOperationException()

    override fun invoke(ctx: IHudDrawContext) = expression.invoke(ctx)
}
