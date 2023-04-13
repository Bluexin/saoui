package com.tencao.saoui.themes.elements

import com.tencao.saoui.SAOCore
import com.tencao.saoui.api.themes.IHudDrawContext
import com.tencao.saoui.themes.AbstractThemeLoader
import net.minecraft.util.ResourceLocation
import javax.xml.bind.annotation.XmlAttribute
import javax.xml.bind.annotation.XmlRootElement

@XmlRootElement
class FragmentReference : Element() {
    @XmlAttribute
    private lateinit var id: String

    @Transient
    private var fragment: ElementGroup? = null

    override fun setup(parent: ElementParent, fragments: Map<ResourceLocation, Fragment>): Boolean {
        val anonymous = super.setup(parent, fragments)
        if (!::id.isInitialized) {
            val message = "Missing id in fragment reference "
            SAOCore.LOGGER.warn(message + hierarchyName())
            AbstractThemeLoader.Reporter += message + nameOrParent()
        }
        else {
            fragment = fragments[ResourceLocation(id)]
                ?.also { it.setup(parent, fragments) }
            if (fragment == null) {
                val message = "Missing fragment with id $id referenced in "
                SAOCore.LOGGER.warn(message + hierarchyName())
                AbstractThemeLoader.Reporter += message + nameOrParent()
            }
        }

        return anonymous
    }

    override fun draw(ctx: IHudDrawContext) {
        fragment?.draw(ctx)
    }
}