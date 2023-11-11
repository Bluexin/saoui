package be.bluexin.mcui.api.events

import be.bluexin.mcui.api.info.IOption
import net.minecraftforge.eventbus.api.Event

/**
 * This is called when an option is clicked
 * Use this to fire any logic you want tied
 * to said event.
 */
class OptionTriggerEvent(val option: IOption) : Event()
