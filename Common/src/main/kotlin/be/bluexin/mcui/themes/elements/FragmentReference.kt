package be.bluexin.mcui.themes.elements

import be.bluexin.mcui.Constants
import be.bluexin.mcui.api.themes.IHudDrawContext
import be.bluexin.mcui.themes.AbstractThemeLoader
import be.bluexin.mcui.themes.util.CValue
import be.bluexin.mcui.themes.util.NamedExpressionIntermediate
import be.bluexin.mcui.themes.util.Variables
import be.bluexin.mcui.themes.util.expressionIntermediate
import be.bluexin.mcui.themes.util.serialization.NamedExpressionSerializer
import com.mojang.blaze3d.vertex.PoseStack
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import net.minecraft.resources.ResourceLocation
import nl.adaptivity.xmlutil.serialization.XmlId

@Serializable
@SerialName("fragmentReference")
class FragmentReference(
    @Suppress("CanBeParameter", "RedundantSuppression") // It can't be a param because of @Serializable
    private val serializedVariables: Variables,
    @XmlId
    private var id: String = MISSING_ID
) : CachingElementParent() {

    @Transient
    private var variables: MutableMap<String, CValue<*>?> = serializedVariables.variable.associateTo(mutableMapOf()) {
        it.key to if (it.expression.isEmpty()) null else it.type.expressionAdapter.compile(it)
    }

    @Transient
    private var fragment: Fragment? = null

    override fun setup(parent: ElementParent, fragments: Map<ResourceLocation, () -> Fragment>): Boolean {
        val anonymous = super.setup(parent, fragments)
        if (id == MISSING_ID) {
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

    override fun draw(ctx: IHudDrawContext, poseStack: PoseStack) {
        if (enabled?.invoke(ctx) == false) return
        fragment?.let {
            ctx.pushContext(variables)
            it.draw(ctx, poseStack)
            ctx.popContext()
        }
    }

    private val CValue<*>?.type get() = (this?.value?.expressionIntermediate as? NamedExpressionIntermediate)?.type

    private companion object {
        private const val MISSING_ID = "@@MISSING_ID@@"
    }
}
