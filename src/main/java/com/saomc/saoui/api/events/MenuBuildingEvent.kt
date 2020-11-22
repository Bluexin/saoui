package com.saomc.saoui.api.events

import com.saomc.saoui.api.elements.NeoElement
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
class MenuBuildingEvent(val elements: ArrayList<NeoElement>): Event() {

}
