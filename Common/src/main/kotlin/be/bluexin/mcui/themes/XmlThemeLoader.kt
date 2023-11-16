package be.bluexin.mcui.themes

import be.bluexin.mcui.themes.elements.Fragment
import be.bluexin.mcui.themes.elements.Hud
import java.io.InputStream
import jakarta.xml.bind.JAXBContext
import nl.adaptivity.xmlutil.StAXReader
import nl.adaptivity.xmlutil.serialization.XML

object XmlThemeLoader : AbstractThemeLoader(ThemeFormat.XML) {

    private val hudReader by lazy {
        JAXBContext.newInstance(Hud::class.java)
        .createUnmarshaller()
    }

    override fun InputStream.loadHud() = use {
        hudReader.unmarshal(it) as Hud
    }

    private val fragmentReader by lazy {
        JAXBContext.newInstance(Fragment::class.java)
        .createUnmarshaller()
    }

    override fun InputStream.loadFragment(): Fragment = use {
        XML.decodeFromReader(StAXReader(it, "UTF-8"))
    }

    private fun InputStream.loadFragmentV2(): Fragment = use {
        XML.decodeFromReader(StAXReader(it, "UTF-8"))
    }
}
