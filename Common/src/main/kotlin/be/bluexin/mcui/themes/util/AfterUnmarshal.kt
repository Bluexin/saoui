package be.bluexin.mcui.themes.util

import jakarta.xml.bind.Unmarshaller

// FIXME
interface AfterUnmarshal {
    fun afterUnmarshal(um: Unmarshaller? = null, parent: Any? = null)
}