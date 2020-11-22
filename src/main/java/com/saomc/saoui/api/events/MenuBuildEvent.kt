package com.saomc.saoui.api.events

import com.saomc.saoui.elements.IElement
import com.saomc.saoui.elements.registry.ElementRegistry
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
class MenuBuildEvent(val elements: ArrayList<IElement>, val type: ElementRegistry.Type): Event() {

}
