package com.tencao.saoui.themes.elements

import com.google.gson.annotations.JsonAdapter
import com.tencao.saoui.SAOCore
import com.tencao.saoui.api.themes.IHudDrawContext
import com.tencao.saoui.themes.AbstractThemeLoader
import com.tencao.saoui.themes.util.CValue
import com.tencao.saoui.themes.util.NamedExpressionIntermediate
import com.tencao.saoui.themes.util.json.NamedExpressionJsonAdapter
import com.tencao.saoui.themes.util.typeadapters.JelType
import com.tencao.saoui.themes.util.xml.NamedExpressionXmlAdapter
import net.minecraft.util.ResourceLocation
import javax.xml.bind.annotation.XmlAttribute
import javax.xml.bind.annotation.XmlRootElement
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter

@XmlRootElement
class FragmentReference : CachingElementParent() {

    @XmlAttribute
    private lateinit var id: String

    @JsonAdapter(NamedExpressionJsonAdapter::class)
    @XmlJavaTypeAdapter(NamedExpressionXmlAdapter::class)
    private var variables: MutableMap<String, CValue<*>?> = mutableMapOf()

    @Transient
    private var fragment: Fragment? = null

    override fun setup(parent: ElementParent, fragments: Map<ResourceLocation, () -> Fragment>): Boolean {
        val anonymous = super.setup(parent, fragments)
        if (!::id.isInitialized) {
            val message = "Missing id in fragment reference "
            SAOCore.LOGGER.warn(message + hierarchyName())
            AbstractThemeLoader.Reporter += message + nameOrParent()
        } else {
            fragment = fragments[ResourceLocation(id)]?.invoke()
                ?.also { it.setup(this, fragments) }
            if (fragment == null) {
                val message = "Missing fragment with id $id referenced in "
                SAOCore.LOGGER.warn(message + hierarchyName())
                AbstractThemeLoader.Reporter += message + nameOrParent()
            } else {
                val missing = fragment?.expect?.variables.orEmpty().filter {
                    val inContext = variables[it.key]
                    inContext == null || inContext.type != it.type
                }
                val defaults = missing.filter(NamedExpressionIntermediate::hasDefault).toSet().onEach {
                    variables[it.key] = it.type.expressionAdapter.compile(it)
                }
                val realMissing = missing - defaults
                if (realMissing.isNotEmpty()) {
                    val present = variables.map { (_, value) -> value?.value?.expressionIntermediate }
                    val message = "Missing variables $realMissing for $id (present : $present) in "
                    SAOCore.LOGGER.warn(message + hierarchyName())
                    AbstractThemeLoader.Reporter += message + nameOrParent()
                }
            }
        }

        return anonymous
    }

    override fun draw(ctx: IHudDrawContext) {
        if (enabled?.invoke(ctx) == false) return
        fragment?.let {
            ctx.pushContext(variables)
            it.draw(ctx)
            ctx.popContext()
        }
    }

    private val CValue<*>?.type get() = (this?.value?.expressionIntermediate as? NamedExpressionIntermediate)?.type
}
