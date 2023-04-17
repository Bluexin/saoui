package be.bluexin.mcui.themes.elements

import com.google.gson.annotations.JsonAdapter
import be.bluexin.mcui.SAOCore
import be.bluexin.mcui.api.themes.IHudDrawContext
import be.bluexin.mcui.themes.AbstractThemeLoader
import be.bluexin.mcui.themes.util.CValue
import be.bluexin.mcui.themes.util.NamedExpressionIntermediate
import be.bluexin.mcui.themes.util.json.NamedExpressionJsonAdapter
import be.bluexin.mcui.themes.util.xml.NamedExpressionXmlAdapter
import net.minecraft.resources.ResourceLocation
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
            Constants.LOG.warn(message + hierarchyName())
            AbstractThemeLoader.Reporter += message + nameOrParent()
        } else {
            fragment = fragments[ResourceLocation(id)]?.invoke()
                ?.also { it.setup(this, fragments) }
            if (fragment == null) {
                val message = "Missing fragment with id $id referenced in "
                Constants.LOG.warn(message + hierarchyName())
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
                    Constants.LOG.warn(message + hierarchyName())
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
