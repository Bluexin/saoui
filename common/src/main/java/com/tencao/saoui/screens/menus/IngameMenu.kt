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

import com.tencao.saoui.SoundCore
import com.tencao.saoui.Vector2d
import com.tencao.saoui.api.elements.CategoryButton
import com.tencao.saoui.api.elements.IconElement
import com.tencao.saoui.api.elements.NeoElement
import com.tencao.saoui.api.elements.registry.ElementRegistry.getDefaultElements
import com.tencao.saoui.api.screens.IIcon
import com.tencao.saoui.screens.CoreGUI
import com.tencao.saoui.util.*
import com.tencao.saoui.util.math.vec
import net.minecraft.client.Minecraft

/**
 * Part of saoui by Bluexin, released under GNU GPLv3.
 *
 * @author Bluexin
 */
class IngameMenu(elements: MutableList<NeoElement> = mutableListOf()) : CoreGUI<Unit>("Ingame Menu", Vector2d(0, 0), elements = elements) {

    var loggingOut = false

    override var result: Unit
        get() = Unit
        set(_) {}

    override fun init(minecraft: Minecraft, width: Int, height: Int) {
        super.init(minecraft, width, height)
        elements.clear()
        val defaultList = getDefaultElements()
        defaultList.forEach { it.parent = this }
        elements.addAll(defaultList)

        pos = vec(width / 2.0 - 10, (height - elements.size * 20) / 2.0)
        destination = pos
        SoundCore.ORB_DROPDOWN.play()
    }

    override fun tick() {
        if (loggingOut) {
            UIUtil.closeGame()
        } else {
            // CraftingUtil.updateItemHelper()
            super.tick()
        }
    }


    override fun isPauseScreen(): Boolean {
        return loggingOut || super.isPauseScreen()
    }

    companion object {
        val mc = Client.minecraft
        fun tlCategory(icon: IIcon, index: Int, description: MutableList<String> = mutableListOf(), body: (CategoryButton.() -> Unit)? = null): CategoryButton {
            return CategoryButton(IconElement(icon, vec(0, 25 * index), description = description), null, body)
        }
    }
}
