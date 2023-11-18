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

package be.bluexin.mcui.themes.elements

import be.bluexin.mcui.api.themes.IHudDrawContext
import be.bluexin.mcui.themes.util.CInt
import com.mojang.blaze3d.vertex.PoseStack
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlSerialName

/**
 * Part of saoui by Bluexin.
 *
 * @author Bluexin
 */
@Serializable
@SerialName("repetitionGroup")
class RepetitionGroup(
    @SerialName("amount")
    @XmlSerialName("amount")
    private var amount: CInt
) : ElementGroupParent() {

    override fun draw(ctx: IHudDrawContext, poseStack: PoseStack) {
        if (!enabled(ctx)) return

        val m = amount(ctx)
        for (i in 0 until m) {
            ctx.setI(i)
            super.draw(ctx, poseStack)
        }
    }
}
