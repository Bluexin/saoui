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

package com.tencao.saoui.util

import com.tencao.saomclib.GLCore
import com.tencao.saomclib.GLCore.glTexturedRectV2
import com.tencao.saoui.SAOCore
import com.tencao.saoui.api.screens.IIcon
import net.minecraft.util.ResourceLocation
import java.util.*

enum class IconCore : IIcon {

    NONE {
        override fun glDraw(x: Int, y: Int, z: Float) = Unit
        override fun glDrawUnsafe(x: Int, y: Int) = Unit
        override fun getRL(): ResourceLocation? = null
    },
    OPTION,
    HELP,
    LOGOUT,
    CANCEL,
    CONFIRM,
    SETTINGS,
    NAVIGATION,
    MESSAGE,
    SOCIAL,
    PROFILE,
    EQUIPMENT,
    ITEMS,
    SKILLS,
    GUILD,
    PARTY,
    FRIEND,
    CREATE,
    INVITE,
    QUEST,
    FIELD_MAP,
    DUNGEON_MAP,
    ARMOR,
    ACCESSORY,
    MESSAGE_RECEIVED,
    CRAFTING,
    SPRINTING,
    SNEAKING;

    override fun glDraw(x: Int, y: Int, z: Float) {
        // GLCore.color(1f, 1f, 1f, 1f)
        GLCore.glBlend(true)
        GLCore.glBindTexture(rl)
        glTexturedRectV2(x.toDouble(), y.toDouble(), width = 16.0, height = 16.0, srcX = 0.0, srcY = 0.0, srcWidth = 256.0, srcHeight = 256.0)
        GLCore.glBlend(false)
    }

    override fun glDrawUnsafe(x: Int, y: Int) {
        glTexturedRectV2(x.toDouble(), y.toDouble(), width = 16.0, height = 16.0, srcX = 0.0, srcY = 0.0, srcWidth = 256.0, srcHeight = 256.0)
    }

    override fun getRL(): ResourceLocation? {
        return rl
    }

    private val rl by lazy { ResourceLocation(SAOCore.MODID, "textures/menu/icons/${name.lowercase(Locale.getDefault())}.png") }
}
