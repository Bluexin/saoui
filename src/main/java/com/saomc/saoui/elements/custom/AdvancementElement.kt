package com.saomc.saoui.elements.custom

import com.saomc.saoui.elements.IElement
import com.saomc.saoui.elements.IconLabelElement
import com.saomc.saoui.screens.util.toIcon
import com.saomc.saoui.util.getProgress
import net.minecraft.advancements.Advancement
import net.minecraft.init.Items

data class AdvancementElement(val advancement: Advancement, val category: Boolean = false, val desc: List<String> = advancement.getRequirementDesc(), override val init: (IElement.() -> Unit)? = null) : IconLabelElement(
        icon = advancement.display?.icon?.toIcon()
                ?: if (advancement.getProgress()?.isDone == true) Items.MAP.toIcon() else Items.MAP.toIcon(),
        label = advancement.displayText.unformattedText) {

}

fun Advancement.getRequirementDesc(): List<String> {
    val builder = StringBuilder()
    builder.append("Requirements: \n")
    this.requirements.forEach {
        builder.append(it.joinToString())
    }
    return builder.lines()
}
