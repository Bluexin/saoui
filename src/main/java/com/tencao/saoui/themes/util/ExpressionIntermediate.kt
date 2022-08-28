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

package com.tencao.saoui.themes.util

import javax.xml.bind.annotation.XmlAttribute
import javax.xml.bind.annotation.XmlValue

/**
 * Part of saoui by Bluexin.
 *
 * @author Bluexin
 */
class ExpressionIntermediate {

    @XmlAttribute(name = "cache")
    val cacheType = CacheType.DEFAULT

    @get:XmlValue
    var expression = "" // Will get replaced by the loading
        get() {
            var f = field
            if (LibHelper.obfuscated) f = f.replace("format(", "func_135052_a(")
            f = f.replace('\n', ' ')
            return f
        }
}
