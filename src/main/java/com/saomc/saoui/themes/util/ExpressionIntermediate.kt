package com.saomc.saoui.themes.util

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

    @XmlValue
    var expression = "" // Will get replaced by the loading
        get() {
            var f = field
            if (LibHelper.obfuscated) f = f.replace("format(", "func_135052_a(")
            f = f.replace('\n', ' ')
            return f
        }
}
