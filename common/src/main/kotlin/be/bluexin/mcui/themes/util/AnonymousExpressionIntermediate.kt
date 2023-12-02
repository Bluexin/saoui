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

package be.bluexin.mcui.themes.util

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import net.minecraft.client.resources.language.I18n
import nl.adaptivity.xmlutil.serialization.XmlOtherAttributes
import nl.adaptivity.xmlutil.serialization.XmlValue

/**
 * Part of saoui by Bluexin.
 *
 * @author Bluexin
 */
@Serializable
sealed class ExpressionIntermediate {
    abstract val serializedExpression: String
    abstract val cacheType: CacheType

    @Transient // TODO : check if this handles obf, otherwise could just static expose in own code
    private val translate = I18n::get.name

    val expression: String
        get() {
            var f = serializedExpression
            f = f.replace('\n', ' ')
                .replace("format(", "$translate(")
            return f
        }

    val asAnonymous: AnonymousExpressionIntermediate
        get() = AnonymousExpressionIntermediate(serializedExpression, cacheType)
}

@Serializable
data class AnonymousExpressionIntermediate(
    @SerialName("expression")
    @XmlValue
    override val serializedExpression: String,
    @SerialName("cache")
    @XmlOtherAttributes
    override val cacheType: CacheType = CacheType.PER_FRAME
) : ExpressionIntermediate()
