package com.tencao.saoui.themes.elements

import com.google.gson.annotations.JsonAdapter
import com.mojang.blaze3d.matrix.MatrixStack
import com.tencao.saoui.Constants
import com.tencao.saoui.api.themes.IHudDrawContext
import com.tencao.saoui.themes.AbstractThemeLoader
import com.tencao.saoui.themes.util.CValue
import com.tencao.saoui.themes.util.NamedExpressionIntermediate
import com.tencao.saoui.themes.util.json.NamedExpressionJsonAdapter
import com.tencao.saoui.themes.util.xml.NamedExpressionXmlAdapter
import jakarta.xml.bind.annotation.XmlAttribute
import jakarta.xml.bind.annotation.XmlRootElement
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter
import net.minecraft.util.ResourceLocation

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
            Constants.LOGGER.warn(message + hierarchyName())
            AbstractThemeLoader.Reporter += message + nameOrParent()
        } else {
            fragment = fragments[ResourceLocation(id)]?.invoke()
                ?.also { it.setup(this, fragments) }
            if (fragment == null) {
                val message = "Missing fragment with id $id referenced in "
                Constants.LOGGER.warn(message + hierarchyName())
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
                    Constants.LOGGER.warn(message + hierarchyName())
                    AbstractThemeLoader.Reporter += message + nameOrParent()
                }
            }
        }

        return anonymous
    }

    override fun draw(ctx: IHudDrawContext, matrixStack: MatrixStack) {
        if (enabled?.invoke(ctx) == false) return
        fragment?.let {
            ctx.pushContext(variables)
            it.draw(ctx, matrixStack)
            ctx.popContext()
        }
    }

    private val CValue<*>?.type get() = (this?.value?.expressionIntermediate as? NamedExpressionIntermediate)?.type
}
