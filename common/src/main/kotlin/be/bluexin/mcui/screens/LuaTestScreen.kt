package be.bluexin.mcui.screens

import be.bluexin.mcui.Constants
import be.bluexin.mcui.api.scripting.JNLua
import be.bluexin.mcui.api.scripting.LoadFragment
import be.bluexin.mcui.api.scripting.LuaJTest
import be.bluexin.mcui.themes.XmlThemeLoader
import be.bluexin.mcui.themes.elements.*
import be.bluexin.mcui.themes.util.*
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.Renderable
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation

class LuaTestScreen : Screen(Component.literal("Lua Test Screen")), ElementParent {

    override val name = this::class.simpleName ?: Element.DEFAULT_NAME

    private val fragment = Fragment().apply {
        name = "${this@LuaTestScreen.name}.root"
        setup(this@LuaTestScreen, emptyMap())
    }

    private lateinit var context: HudDrawContext

    private var loadedWidgets = 0

    override fun init() {
        super.init()
        context = HudDrawContext()
        addRenderableWidget(
            Button.builder(
                Component.literal("Load script")
            ) {
                try {
                    LuaJTest.runScript(ResourceLocation("mcui", "test.lua"))
                } catch (e: Throwable) {
                    Minecraft.getInstance().player?.sendSystemMessage(Component.literal("Something went wrong : ${e.message}. See console for more info."))
                    Constants.LOG.error("Couldn't evaluate test.lua", e)
                }
            }.pos(100, 100).build()
        )
        addRenderableWidget(
            Button.builder(
                Component.translatable("Load Widget")
            ) {
                try {
                    val widget = XmlThemeLoader.loadWidget(
                        ResourceLocation(
                            Constants.MOD_ID,
                            "themes/hex2/widgets/button.xml"
                        )
                    )
                    val name = "Awesome Widget $loadedWidgets"
                    widget.setVariable("text", CString { name })
                    widget.setVariable("x", CDouble { 200.0 })
                    val y = loadedWidgets++ * 25.0
                    widget.setVariable("y", CDouble { y })
                    widget.setup(fragment, emptyMap())
                    widget.TMP_CTX = context
                    addRenderableWidget(widget)
                } catch (e: Throwable) {
                    Minecraft.getInstance().player?.sendSystemMessage(Component.literal("Something went wrong : $e. See console for more info."))
                    Constants.LOG.error("Couldn't load fragment", e)
                }
            }.pos(100, 120).build()
        )
        addRenderableOnly(Renderable { poseStack: PoseStack, mouseX: Int, mouseY: Int, partialTick: Float ->
            fragment.draw(context, poseStack)
        })
    }

    override fun render(poseStack: PoseStack, mouseX: Int, mouseY: Int, partialTick: Float) {
        context.setTime(partialTick)
        super.render(poseStack, mouseX, mouseY, partialTick)
    }

    override fun added() {
        super.added()
        LoadFragment[fragment.name] = fragment
    }

    override fun removed() {
        super.removed()
        LoadFragment.clear(fragment.name)
        JNLua.close()
    }
}