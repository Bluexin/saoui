package com.tencao.saoui.themes.elements

import com.tencao.saoui.SAOCore
import com.tencao.saoui.api.themes.IHudDrawContext
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
        if (!::id.isInitialized) SAOCore.LOGGER.warn("Missing id in fragment reference $name !")
        else {
            fragment = fragments[ResourceLocation(id)]
                ?.also { it.setup(parent, fragments) }
            if (fragment == null) SAOCore.LOGGER.warn("Missing fragment with id $id referenced in $name !")
        }

        return super.setup(parent, fragments)
    }

    override fun draw(ctx: IHudDrawContext) {
        fragment?.draw(ctx)
    }
}