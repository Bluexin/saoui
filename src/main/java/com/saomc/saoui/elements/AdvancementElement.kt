package com.saomc.saoui.elements

import com.saomc.saoui.elements.controllers.IController
import com.saomc.saoui.screens.CoreGUI
import com.saomc.saoui.screens.util.PopupAdvancement
import com.saomc.saoui.screens.util.toIcon
import com.saomc.saoui.util.getProgress
import net.minecraft.advancements.Advancement
import net.minecraft.init.Items

class AdvancementElement(val advancement: Advancement, controller: IController): IconLabelElement(icon =advancement.display?.icon?.toIcon()?: if (advancement.getProgress()?.isDone == true) Items.MAP.toIcon() else Items.MAP.toIcon(),
        controller = controller, label = advancement.displayText.unformattedText, description = advancement.getRequirementDesc()) {

    override fun open() {
        (controllingParent.tlController as? CoreGUI<*>)?.openGui(PopupAdvancement(advancement))?.plusAssign {
            var index = controllingParent.elements.indexOf(this)
            when (it){
                PopupAdvancement.Result.NEXT -> {
                    if (++index >= controllingParent.elements.size)
                        index = 0
                    controllingParent.elements[index].open()
                }
                PopupAdvancement.Result.PREVIOUS ->  {
                    if (--index < 0)
                        index = controllingParent.elements.size.minus(1)
                    controllingParent.elements[index].open()
                }
                else -> {}
            }
        }
    }
}

fun Advancement.getRequirementDesc(): MutableList<String>{
    val builder = StringBuilder()
    builder.append("Requirements: \n")
    this.requirements.forEach {
        builder.append(it.joinToString())
    }
    return builder.lines().toMutableList()
}