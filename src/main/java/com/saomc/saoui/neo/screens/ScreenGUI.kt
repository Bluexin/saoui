package com.saomc.saoui.neo.screens

import com.saomc.saoui.GLCore
import com.saomc.saoui.api.screens.Actions
import com.saomc.saoui.colorstates.CursorStatus
import com.saomc.saoui.config.OptionCore
import com.saomc.saoui.elements.ElementController
import com.saomc.saoui.elements.ElementDispatcher
import com.saomc.saoui.resources.StringNames
import com.saomc.saoui.themes.elements.MenuElementParent
import com.saomc.saoui.themes.elements.menus.PlaceholderElement
import com.saomc.saoui.util.ColorUtil
import com.saomc.saoui.util.LogCore
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.lwjgl.BufferUtils
import org.lwjgl.input.Cursor
import org.lwjgl.input.Mouse
import java.io.IOException
import java.util.*
import javax.annotation.OverridingMethodsMustInvokeSuper

/**
 * Part of saoui by Bluexin.
 *
 * @author Bluexin
 */
@SideOnly(Side.CLIENT)
abstract class ScreenGUI : GuiScreen(), MenuElementParent {

    companion object {
        protected val ROTATION_FACTOR = 0.25f
        protected var CURSOR_STATUS = CursorStatus.SHOW
        protected val CURSOR_EMPTY = Cursor(1, 1, 0, 0, 1, BufferUtils.createIntBuffer(1), null)
//        protected lateinit var CURSOR_CUSTOM: Cursor
    }

    protected var mouseX = 0
    protected var mouseY = 0
    protected var mouseDown = 0
    protected var mouseDownValue = 0.0F
    protected var rotationYaw = 0.0F
    protected var rotationPitch = 0.0F
    protected var cursorHidden = false
    protected var lockCursor = false
    protected val elements = mutableListOf<PlaceholderElement>()

    @OverridingMethodsMustInvokeSuper
    override fun initGui() {
        if (CURSOR_STATUS != CursorStatus.DEFAULT) hideCursor()
        rotationYaw = mc.player?.rotationYaw ?: 0.0F
        rotationPitch = mc.player?.rotationPitch ?: 0.0F
        elements.forEach { it.init(this) }

        super.initGui()
    }

    private fun hideCursor() {
        if (!cursorHidden) toggleHideCursor()
    }

    private fun toggleHideCursor() {
        cursorHidden = !cursorHidden
        Mouse.setNativeCursor(if (cursorHidden) CURSOR_EMPTY else null)
    }

    override fun updateScreen() {
        mc.player?.rotationYaw = rotationYaw - getX() * ROTATION_FACTOR
        mc.player?.rotationPitch = rotationPitch - getY() * ROTATION_FACTOR
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        profile(mc, "saoui menu") {
            elements.forEach { it.draw(mc, mouseX, mouseY) }

            if (CURSOR_STATUS == CursorStatus.SHOW) { // TODO: maybe there's a way to move all of this to the actual org.lwjgl.input.Cursor

                GLCore.glBlend(true)
                GLCore.tryBlendFuncSeparate(770, 771, 1, 0)
                GLCore.glBindTexture(if (OptionCore.SAO_UI.isEnabled) StringNames.gui else StringNames.guiCustom)

                if (mouseDown != 0) {
                    val fval = partialTicks * 0.1f

                    if (mouseDownValue + fval < 1.0f)
                        mouseDownValue += fval
                    else
                        mouseDownValue = 1.0f

                    GLCore.glColorRGBA(ColorUtil.CURSOR_COLOR.multiplyAlpha(mouseDownValue))
                    GLCore.glTexturedRect((mouseX - 7).toDouble(), (mouseY - 7).toDouble(), 35.0, 115.0, 15.0, 15.0)

                    GLCore.glColorRGBA(ColorUtil.DEFAULT_COLOR)
                } else {
                    mouseDownValue = 0f

                    GLCore.glColorRGBA(ColorUtil.CURSOR_COLOR)
                }

                GLCore.glTexturedRect((mouseX - 7).toDouble(), (mouseY - 7).toDouble(), 20.0, 115.0, 15.0, 15.0)
                GLCore.glBlend(false)
            }
        }
    }

    override fun keyTyped(ch: Char, key: Int) {
        if (OptionCore.CURSOR_TOGGLE.isEnabled && GuiScreen.isCtrlKeyDown()) lockCursor = !lockCursor
        LogCore.logDebug("ch - $ch key - $key")
        super.keyTyped(ch, key)

//        ElementDispatcher.getElements().stream().anyMatch({ e -> e.keyTyped(mc, ch, key) })

        //elements.menuElements.keySet().stream().filter(Element::isFocus).forEach(element -> actionPerformed(element, Actions.KEY_TYPED, key));
    }

    @Throws(IOException::class)
    override fun mouseClicked(cursorX: Int, cursorY: Int, button: Int) {
        super.mouseClicked(cursorX, cursorY, button)
        mouseDown = mouseDown or (0x1 shl button)

        try {
            //if (ElementDispatcher.getElements().stream().noneMatch({ controller -> controller.mousePressed(mc, cursorX, cursorY, button) }))
            backgroundClicked(cursorX, cursorY, button)
        } catch (e: ConcurrentModificationException) {
            //Do Nothing
            LogCore.logDebug("mouseClicked ended unexpectedly")
        }

    }

    override fun mouseReleased(cursorX: Int, cursorY: Int, button: Int) {
        super.mouseReleased(cursorX, cursorY, button)
        mouseDown = mouseDown and (0x1 shl button).inv()

        var found = false

        try {
            for (elementController in ElementDispatcher.getElements())
                if (found)
                    break
                else
                    for (element in elementController.elements)
                        if (element.isOpen && element.mouseReleased(mc, cursorX, cursorY, button) || element.isFocus && elementController.mouseOver(cursorX, cursorY, button) && element.mouseReleased(mc, cursorX, cursorY, button)) {
                            ElementController.actionPerformed(element, Actions.LEFT_RELEASED, button)
                            found = true
                            break
                        }

        } catch (e: ConcurrentModificationException) {
            //Do Nothing
            LogCore.logWarn("mouseClicked ended unexpectedly")
        }

    }

    private fun backgroundClicked(cursorX: Int, cursorY: Int, button: Int) {
        LogCore.logDebug("Background Clicked")
    }

    private fun mouseWheel(cursorX: Int, cursorY: Int, delta: Int) {
        // Nothing here atm
    }

    override fun handleMouseInput() {
        super.handleMouseInput()

        if (Mouse.hasWheel()) {
            val x = Mouse.getEventX() * width / mc.displayWidth
            val y = height - Mouse.getEventY() * height / mc.displayHeight - 1
            val delta = Mouse.getEventDWheel()

            if (delta != 0) mouseWheel(x, y, delta)
        }
    }

    override fun doesGuiPauseGame() = OptionCore.GUI_PAUSE.isEnabled

    override fun onGuiClosed() {
        showCursor()

        close()
    }

    protected open fun close() = elements.forEach { it.close() }

    private fun showCursor() {
        if (cursorHidden) toggleHideCursor()
    }

    private inline fun profile(mc: Minecraft, key: String, body: () -> Unit) {
//        throw UnsupportedOperationException("change with saomclibs helpers once updated") //To change body of created functions use File | Settings | File Templates.
        mc.mcProfiler.startSection(key)
        body.invoke()
        mc.mcProfiler.endSection()
    } // TODO: replace with saomclib's

    override fun getX() =
            if (OptionCore.CURSOR_TOGGLE.isEnabled) if (lockCursor) 0 else (width / 2 - mouseX) / 2
            else if (GuiScreen.isCtrlKeyDown()) 0 else (width / 2 - mouseX) / 2

    override fun getY() =
            if (OptionCore.CURSOR_TOGGLE.isEnabled) if (lockCursor) 0 else (height / 2 - mouseY) / 2
            else if (GuiScreen.isCtrlKeyDown()) 0 else (height / 2 - mouseY) / 2

    override fun getZ() = 0
}
