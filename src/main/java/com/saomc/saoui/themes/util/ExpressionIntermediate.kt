package com.saomc.saoui.themes.util

import javax.xml.bind.annotation.XmlAttribute
import javax.xml.bind.annotation.XmlValue

/**
 * Part of saoui by Bluexin.
 *
 * @author Bluexin
 */
class ExpressionIntermediate {

    @XmlAttribute(name = "cache") val cacheType = CacheType.DEFAULT

    @XmlValue
    val expression = "" // Will get replaced by the loading
        get() = field.replace('\n', ' ')
}
