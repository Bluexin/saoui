package be.bluexin.mcui.themes.elements

import be.bluexin.mcui.Constants
import be.bluexin.mcui.api.scripting.LuaCallback
import be.bluexin.mcui.api.themes.IHudDrawContext
import be.bluexin.mcui.themes.AbstractThemeLoader
import be.bluexin.mcui.themes.util.*
import be.bluexin.mcui.util.Client
import be.bluexin.mcui.util.DeserializationOrder
import com.mojang.blaze3d.vertex.PoseStack
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import net.minecraft.client.gui.GuiComponent
import net.minecraft.client.gui.components.Renderable
import net.minecraft.client.gui.components.events.GuiEventListener
import net.minecraft.client.gui.narration.NarratableEntry
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.resources.ResourceLocation
import nl.adaptivity.xmlutil.serialization.XmlBefore
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
@XmlSerialName(value = "widget")
class Widget(
    @XmlSerialName("expect")
    @XmlBefore("x", "children", "texture")
    @DeserializationOrder(0)
    val expect: Expect? = null,
    @XmlSerialName("contentWidth")
    private val contentWidth: CInt,
    @XmlSerialName("contentHeight")
    private val contentHeight: CInt,
    @XmlSerialName("active")
    private val active: CBoolean = CBoolean.TRUE
) : ElementGroupParent(), GuiEventListener, Renderable, NarratableEntry {

    @Transient
    var TMP_CTX: IHudDrawContext? = null

    @Transient
    private var focused = false

    @Transient
    private var isMouseOver = false

    @XmlElement
    private var onClick: LuaCallback = LuaCallback.NO_OP

    @Transient
    private val variables: MutableMap<String, CValue<*>?> = mutableMapOf()

    init {
        if (expect != null) LibHelper.popContext()
    }

    private inline fun <T> withContext(body: (IHudDrawContext) -> T): T? = TMP_CTX?.let {
        it.pushContext(variables)
        val r = body(it)
        it.popContext()
        r
    }

    private fun checkMouseOver(mouseX: Int, mouseY: Int, ctx: IHudDrawContext) =
        checkMouseOver(mouseX.toDouble(), mouseY.toDouble(), ctx)

    private fun checkMouseOver(mouseX: Double, mouseY: Double, ctx: IHudDrawContext) {
        val x = x(ctx)
        val y = y(ctx)
        isMouseOver = isActive(ctx) && mouseX >= x && mouseX < x + contentWidth(ctx)
                && mouseY >= y && mouseY < y + contentHeight(ctx)
    }

    override fun draw(ctx: IHudDrawContext, poseStack: PoseStack) {
        if (!enabled(ctx)) return

        prepareDraw(ctx, poseStack)
        GuiComponent.fill(
            poseStack,
            0, 0,
            contentWidth(ctx),
            contentHeight(ctx),
            0,
            if (isMouseOver) 0xd632ef44.toInt() else 0x1082e544
        )

        drawChildren(ctx, poseStack)
        finishDraw(ctx, poseStack)
    }

    override fun setup(parent: ElementParent, fragments: Map<ResourceLocation, () -> Fragment>): Boolean {
        val anonymous = super.setup(parent, fragments)

        val missing = expect?.variables.orEmpty().filter { (key, it) ->
            val inContext = variables[key]
            inContext == null || inContext.type != it.type
        }
        val defaults = missing.onEach { (key, it) ->
            if (it.hasDefault()) variables[key] = it.type.expressionAdapter.compile(it)
        }.keys
        val realMissing = missing - defaults
        if (realMissing.isNotEmpty()) {
            val present = variables.mapValues { (_, value) -> value?.value?.expressionIntermediate }
            val message = "Missing variables $realMissing for (present : $present) in "
            Constants.LOG.warn(message + hierarchyName())
            AbstractThemeLoader.Reporter += message + nameOrParent()
        }

        return anonymous
    }

    fun setVariable(key: String, variable: CValue<*>?) {
        variables[key] = variable
    }

    override fun setFocused(focused: Boolean) {
        this.focused = focused
    }

    override fun isFocused(): Boolean = focused

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        return withContext {
            checkMouseOver(mouseX, mouseY, it)
            if (isMouseOver) try {
                onClick(mouseX, mouseY, button)
            } catch (e: Throwable) {
                Client.showError("Error while evaluating onClick handler for $name", e)
                onClick = LuaCallback.NO_OP
            }
            isMouseOver
        } ?: false
    }

    override fun render(poseStack: PoseStack, mouseX: Int, mouseY: Int, partialTick: Float) {
        withContext {
            checkMouseOver(mouseX, mouseY, it)
            this.draw(it, poseStack)
        }
    }

    override fun isActive(): Boolean = withContext(::isActive) ?: false

    private fun isActive(ctx: IHudDrawContext) = enabled(ctx) && active(ctx)

    override fun updateNarration(narrationElementOutput: NarrationElementOutput) {
//        TODO("Not yet implemented")
    }

    // FIXME
    override fun narrationPriority() = NarratableEntry.NarrationPriority.FOCUSED

    private val CValue<*>?.type get() = (this?.value?.expressionIntermediate as? NamedExpressionIntermediate)?.type

}
