package com.saomc.saoui.api.events

import com.saomc.saoui.api.screens.Actions
import com.saomc.saoui.api.screens.ElementType
import com.saomc.saoui.api.screens.GuiSelection
import com.saomc.saoui.api.screens.ParentElement
import com.saomc.saoui.themes.elements.menus.MenuElementParent
import jdk.nashorn.internal.objects.annotations.Getter
import net.minecraftforge.fml.common.eventhandler.Event

/**
 * This is the event thats triggered when an element is clicked
 * Use this to assign your own functions to elements
 *
 *
 * Created by Tencao on 03/08/2016.
 */
class ElementAction(
        /**
         * @return Returns the elements name that fired the event
         */
        val name: String,
        /**
         * @return Returns what action was used
         */
        val action: Actions,
        /**
         * @return Returns data
         */
        val data: Int,
        /**
         * Checks to see if the element is open or not
         * Useful for deciding whether to fire an event onClose, or onOpen

         * @return Returns whether the element is already open
         */
        val isOpen: Boolean,
        /**
         * Checks to see if the category is locked
         * before firing an event. Useful for making
         * sure multiple categories don't open at once

         * @return Returns true if locked
         */
        val isLocked: Boolean,
        /**
         * @return Gets the Menu Parent that fired this event
         */
        val parent: MenuElementParent) : Event()
