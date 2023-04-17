package be.bluexin.mcui.api.events

import be.bluexin.mcui.api.elements.NeoElement
import net.minecraftforge.fml.common.eventhandler.Event

/**
 * This event is called everytime the menu is
 * opened. If two elements are added in the
 * same place, then only the first one persists.
 *
 * Please add buttons in the order they need to
 * be added to prevent missing element issues.
 *
 */
class MenuBuildingEvent(val elements: List<NeoElement>) : Event()
