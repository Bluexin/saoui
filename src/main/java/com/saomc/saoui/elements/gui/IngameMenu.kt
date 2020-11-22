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

package com.saomc.saoui.elements.gui

import com.saomc.saoui.SAOCore
import com.saomc.saoui.SoundCore
import com.saomc.saoui.elements.registry.ElementRegistry
import com.saomc.saoui.play
import com.saomc.saoui.util.CraftingUtil
import com.saomc.saoui.util.UIUtil
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.Minecraft
import net.minecraft.client.resources.I18n.format

/**
 * Part of saoui by Bluexin, released under GNU GPLv3.
 *
 * @author Bluexin
 */
class IngameMenu : CoreGUI<Unit>() {

    var loggingOut = false

    override var result: Unit
        get() = Unit
        set(_) {}

    override fun initGui() {
        elements.addAll(ElementRegistry.registeredElements[ElementRegistry.Type.INGAMEMENU]!!)
        elements.forEach { it.open(true) }

        pos = vec(width / 2.0 - 10, (height - elements.size * 20) / 2.0)
        //destination = pos
        SoundCore.ORB_DROPDOWN.play()

        if (!hasChecked) {
            if (!SAOCore.isSAOMCLibServerSide) {
                openGui(PopupNotice(format("notificationSAOMCLibTitle"), listOf(format("notificationSAOMCLibDesc"), format("notificationSAOMCLibDesc2")), ""))
            }
            hasChecked = true
        }
    }

    override fun updateScreen() {
        if (loggingOut)
            UIUtil.closeGame()
        else {
            CraftingUtil.updateItemHelper()
            super.updateScreen()
        }
    }


    override fun onGuiClosed() {
        super.onGuiClosed()
        elements.forEach { it.close(false) }
        elements.clear()
    }

    override fun doesGuiPauseGame(): Boolean {
        return loggingOut || super.doesGuiPauseGame()
    }

    companion object {
        val mc = Minecraft()
        var hasChecked: Boolean = false

    }
}
