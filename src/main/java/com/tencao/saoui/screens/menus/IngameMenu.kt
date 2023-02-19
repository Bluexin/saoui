/*
 * Copyright (C) 2016-2019 Arnaud 'Bluexin' Solé
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.tencao.saoui.screens.menus

import com.tencao.saomclib.Client
import com.tencao.saomclib.utils.math.Vec2d
import com.tencao.saomclib.utils.math.vec
import com.tencao.saoui.SAOCore
import com.tencao.saoui.SoundCore
import com.tencao.saoui.api.elements.CategoryButton
import com.tencao.saoui.api.elements.IconElement
import com.tencao.saoui.api.elements.NeoElement
import com.tencao.saoui.api.elements.registry.ElementRegistry
import com.tencao.saoui.api.events.MenuBuildingEvent
import com.tencao.saoui.api.screens.IIcon
import com.tencao.saoui.play
import com.tencao.saoui.screens.CoreGUI
import com.tencao.saoui.screens.util.PopupNotice
import com.tencao.saoui.util.CraftingUtil
import com.tencao.saoui.util.UIUtil
import net.minecraft.client.resources.I18n.format
import net.minecraftforge.common.MinecraftForge

/**
 * Part of saoui by Bluexin, released under GNU GPLv3.
 *
 * @author Bluexin
 */
class IngameMenu(elements: MutableList<NeoElement> = mutableListOf()) : CoreGUI<Unit>(Vec2d.ZERO, elements = elements) {

    var loggingOut = false

    override var result: Unit
        get() = Unit
        set(_) {}

    override fun initGui() {
        elements.clear()
        val defaultList = getDefaultElements()
        // TODO : now we hooked up ElementRegistry this event is being sent more than before !
        val event = MenuBuildingEvent(defaultList)
        MinecraftForge.EVENT_BUS.post(event)
        event.elements.forEach { it.parent = this }
        elements.addAll(event.elements)

        pos = vec(width / 2.0 - 10, (height - elements.size * 20) / 2.0)
        destination = pos
        SoundCore.ORB_DROPDOWN.play()

        if (!hasChecked) {
            if (!SAOCore.isSAOMCLibServerSide) {
                openGui(
                    PopupNotice(
                        format("notificationSAOMCLibTitle"),
                        listOf(format("notificationSAOMCLibDesc"), format("notificationSAOMCLibDesc2")),
                        ""
                    )
                )
            }
            hasChecked = true
        }
    }

    override fun updateScreen() {
        if (loggingOut) {
            UIUtil.closeGame()
        } else {
            CraftingUtil.updateItemHelper()
            super.updateScreen()
        }
    }

    fun getDefaultElements(): List<NeoElement> {
        return ElementRegistry.registeredElements[ElementRegistry.Type.INGAMEMENU]
            ?: ElementRegistry.getDefaultElements()
    }

    override fun doesGuiPauseGame(): Boolean {
        return loggingOut || super.doesGuiPauseGame()
    }

    companion object {
        val mc = Client.minecraft
        var hasChecked: Boolean = false
        fun tlCategory(
            icon: IIcon,
            index: Int,
            description: MutableList<String> = mutableListOf(),
            body: (CategoryButton.() -> Unit)? = null
        ): CategoryButton {
            return CategoryButton(IconElement(icon, vec(0, 25 * index), description = description), null, body)
        }
    }
}
