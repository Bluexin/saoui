package com.tencao.saoui.api.themes

import net.minecraft.util.Hand
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.StringTextComponent

/**
 * A backwards compatible class to ensure themes continue to work. */
enum class EnumHandSide(val handName: ITextComponent, val hand: Hand) {
    LEFT(StringTextComponent("Left"), Hand.OFF_HAND),
    RIGHT(StringTextComponent("Right"), Hand.MAIN_HAND);

    open fun opposite(): EnumHandSide {
        return if (this == LEFT) RIGHT else LEFT
    }
}
