package com.tencao.saoui.themes

import com.tencao.saoui.themes.elements.Hud
import java.io.InputStream
import javax.xml.bind.JAXBContext

class XmlThemeLoader : AbstractThemeLoader(ThemeFormat.XML) {

    override fun InputStream.loadHud() = use {
        JAXBContext.newInstance(Hud::class.java)
            .createUnmarshaller()
            .unmarshal(it) as Hud
    }
}