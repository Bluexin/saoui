package be.bluexin.mcui.themes.util

import javax.xml.bind.Unmarshaller

interface AfterUnmarshal {
    fun afterUnmarshal(um: Unmarshaller? = null, parent: Any? = null)
}