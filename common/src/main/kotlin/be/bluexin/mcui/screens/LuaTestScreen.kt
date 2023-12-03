package be.bluexin.mcui.screens

import be.bluexin.mcui.Constants
import be.bluexin.mcui.api.scripting.JNLua
import be.bluexin.mcui.api.scripting.LoadFragment
import be.bluexin.mcui.api.scripting.LuaJTest
import be.bluexin.mcui.themes.JsonThemeLoader
import be.bluexin.mcui.themes.elements.ElementParent
import be.bluexin.mcui.themes.elements.Fragment
import be.bluexin.mcui.themes.util.StaticCachedExpression
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.Renderable
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation

class LuaTestScreen : Screen(Component.literal("Lua Test Screen")) {
    private val fragment = Fragment().apply {
        this@LuaTestScreen::class.simpleName?.let { name = it }
    }

    override fun init() {
        super.init()
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
                Component.literal("Load fragment")
            ) {
                try {
                    val frag = JsonThemeLoader.loadFragment(ResourceLocation("saoui", "themes/hex2/fragments/label.xml"))
                    frag.setup(ElementParent.ZERO, emptyMap())
                    fragment.elements = listOf(frag)
                } catch (e: Throwable) {
                    Minecraft.getInstance().player?.sendSystemMessage(Component.literal("Something went wrong : $e. See console for more info."))
                    Constants.LOG.error("Couldn't load fragment", e)
                }
            }.pos(100, 120).build()
        )
        addRenderableOnly(Renderable { poseStack: PoseStack, mouseX: Int, mouseY: Int, partialTick: Float ->
            fragment.draw(StaticCachedExpression.StubContext, poseStack)
        })
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