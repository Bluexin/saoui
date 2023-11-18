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
package com.tencao.saoui.api.screens

import com.tencao.saoui.xi
import com.tencao.saoui.yi
import net.minecraft.resources.ResourceLocation
import org.joml.Vector2d

/**
 * Part of saoui
 * Icons are used to display any form on screen.
 * Implement this to add your own custom icons.
 *
 * @author Bluexin
 */
@FunctionalInterface
fun interface IIcon {
    /**
     * Called when this icon needs to be drawn on screen, at given x and y coordinates.
     * The given coordinates should be the upper-left corner of this icon.
     *
     * @param x x-coordinate to start drawing from
     * @param y y-coordinate to start drawing from
     */
    fun glDraw(x: Int, y: Int) {
        this.glDraw(x, y, 0f)
    }

    fun glDraw(x: Int, y: Int, z: Float)
    fun glDraw(pos: Vector2d, z: Float) {
        glDraw(pos.xi, pos.yi, z)
    }

    fun glDrawUnsafe(x: Int, y: Int) {
        this.glDraw(x, y)
    }

    fun glDrawUnsafe(pos: Vector2d) {
        glDrawUnsafe(pos.xi, pos.yi)
    }

    val width: Int
        get() = 16
    val height: Int
        get() = 16
    val rl: ResourceLocation?
        get() = null
}