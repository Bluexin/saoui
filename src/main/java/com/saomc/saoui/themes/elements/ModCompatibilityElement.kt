package com.saomc.saoui.themes.elements

import com.saomc.saoui.SAOCore
import com.saomc.saoui.api.themes.IHudDrawContext
import com.saomc.saoui.themes.ThemeLoader
import javax.xml.bind.annotation.XmlAttribute
import javax.xml.bind.annotation.XmlRootElement
import javax.xml.bind.annotation.XmlTransient
import javax.xml.bind.annotation.adapters.XmlAdapter
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter

@XmlRootElement
@XmlJavaTypeAdapter(ModCompatibilityAdapter::class)
class ModCompatibilityElement : ElementGroup() {
    @XmlTransient
    lateinit var key: String
    @XmlTransient
    lateinit var version: String

    override fun draw(ctx: IHudDrawContext) {
        val oldExt = ctx.setExt(key, version)
        super.draw(ctx)
        ctx.setExt(oldExt)
    }

    override fun setup(parent: ElementParent): Boolean {
        SAOCore.LOGGER.info("Setup mod compat")
        return super.setup(parent)
    }
}

data class ModCompatibilityLoader(
        @XmlAttribute val loadFrom: String,
        @XmlAttribute val key: String,
        @XmlAttribute val version: String
) {
    fun isExtensionPresent() = key to version in ThemeLoader
}

class ModCompatibilityAdapter : XmlAdapter<ModCompatibilityLoader, ModCompatibilityElement>() {
    override fun marshal(v: ModCompatibilityElement?) = throw UnsupportedOperationException("marshalling xml expressions isn't allowed just yet.") // TODO: see Evaluator.compileBits(...)

    override fun unmarshal(v: ModCompatibilityLoader): ModCompatibilityElement? =
            if (v.isExtensionPresent()) ThemeLoader.loadExtension(v) else null
}