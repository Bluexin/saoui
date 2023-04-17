package be.bluexin.mcui.themes

import be.bluexin.mcui.themes.elements.Fragment
import be.bluexin.mcui.themes.elements.Hud
import java.io.InputStream
import javax.xml.bind.JAXBContext

object XmlThemeLoader : AbstractThemeLoader(ThemeFormat.XML) {

    override fun InputStream.loadHud() = use {
        JAXBContext.newInstance(Hud::class.java)
            .createUnmarshaller()
            .unmarshal(it) as Hud
    }

    override fun InputStream.loadFragment(): Fragment = use {
        JAXBContext.newInstance(Fragment::class.java)
            .createUnmarshaller()
            .unmarshal(it) as Fragment
    }
}