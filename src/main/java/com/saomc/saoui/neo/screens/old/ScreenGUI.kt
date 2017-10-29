package com.saomc.saoui.neo.screens.old

import be.bluexin.saomclib.profile
import com.saomc.saoui.GLCore
import com.saomc.saoui.SAOCore
import com.saomc.saoui.api.elements.CategoryEnum
import com.saomc.saoui.api.elements.MenuElementParent
import com.saomc.saoui.api.screens.Actions
import com.saomc.saoui.colorstates.CursorStatus
import com.saomc.saoui.config.OptionCore
import com.saomc.saoui.resources.StringNames
import com.saomc.saoui.themes.elements.menus.CategoryData
import com.saomc.saoui.util.ColorUtil
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
    protected val categories = mutableListOf<CategoryData>()

    @OverridingMethodsMustInvokeSuper
    override fun initGui() {
        if (CURSOR_STATUS != CursorStatus.DEFAULT) hideCursor()
        rotationYaw = mc.player!!.rotationYaw
        rotationPitch = mc.player!!.rotationPitch
        categories.forEach { it.init(this) }

        super.initGui()
    }

    private fun getCursorX(): Int {
        return if (lockCursor) 0 else (width / 2 - mouseX) / 2
    }

    private fun getCursorY(): Int {
        return if (lockCursor) 0 else (height / 2 - mouseY) / 2
    }


    private fun hideCursor() {
        if (!cursorHidden) toggleHideCursor()
    }

    private fun toggleHideCursor() {
        cursorHidden = !cursorHidden
        Mouse.setNativeCursor(if (cursorHidden) CURSOR_EMPTY else null)
    }

    override fun updateScreen() {
    }

    override fun drawScreen(cursorX: Int, cursorY: Int, partialTicks: Float) {
        mc.profile("saoui menu") {
            mouseX = cursorX
            mouseY = cursorY
            mc.player.rotationYaw = rotationYaw - getCursorX() * ROTATION_FACTOR
            mc.player?.rotationPitch = rotationPitch - getCursorY() * ROTATION_FACTOR

            categories.forEach { it.draw(mc, cursorX, cursorY) }

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
                    GLCore.glTexturedRect((cursorX - 7).toDouble(), (cursorY - 7).toDouble(), 35.0, 115.0, 15.0, 15.0)

                    GLCore.glColorRGBA(ColorUtil.DEFAULT_COLOR)
                } else {
                    mouseDownValue = 0f

                    GLCore.glColorRGBA(ColorUtil.CURSOR_COLOR)
                }

                GLCore.glTexturedRect((cursorX - 7).toDouble(), (cursorY - 7).toDouble(), 20.0, 115.0, 15.0, 15.0)
            }


        }
    }

    override fun closeCategory(category: CategoryEnum) {
        categories.find { it.category == category }?.setEnabled(false)
    }

    override fun openCategory(category: CategoryEnum){
        categories.find { it.category == category }?.setEnabled(true)
    }

    override fun keyTyped(ch: Char, key: Int) {
        if (OptionCore.CURSOR_TOGGLE.isEnabled && GuiScreen.isCtrlKeyDown()) lockCursor = !lockCursor
        SAOCore.LOGGER.debug("ch - $ch key - $key")
        super.keyTyped(ch, key)

//        ElementDispatcher.getElements().stream().anyMatch({ e -> e.keyTyped(mc, ch, key) })

        //elements.menuElements.keySet().stream().filter(Element::isFocus).forEach(element -> actionPerformed(element, Actions.KEY_TYPED, key));
    }

    @Throws(IOException::class)
    override fun mouseClicked(cursorX: Int, cursorY: Int, button: Int) {
        super.mouseClicked(cursorX, cursorY, button)
        mouseDown = mouseDown or (0x1 shl button)
        val mouseButton: Actions = if (button == 0) Actions.LEFT_PRESSED else Actions.RIGHT_PRESSED

        if (categories.firstOrNull { it.mouseClicked(cursorX, cursorY, mouseButton) } == null)
            try {
                backgroundClicked(cursorX, cursorY, mouseButton)
            } catch (e: ConcurrentModificationException) {
                //Do Nothing
                SAOCore.LOGGER.debug("mouseClicked ended unexpectedly")
            }

    }

    override fun mouseReleased(cursorX: Int, cursorY: Int, button: Int) {
        super.mouseReleased(cursorX, cursorY, button)
        val mouseButton: Actions = if (button == 0) Actions.LEFT_RELEASED else Actions.RIGHT_RELEASED
        categories.firstOrNull { it.mouseClicked(cursorX, cursorY, mouseButton) }
        mouseDown = mouseDown and (0x1 shl button).inv()
    }

    open fun backgroundClicked(cursorX: Int, cursorY: Int, button: Actions) {
        SAOCore.LOGGER.debug("Background Clicked")
    }

    private fun mouseWheel(cursorX: Int, cursorY: Int, delta: Int) {
        categories.forEach { it.mouseScroll(cursorX, cursorY, delta) }
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

    override fun doesGuiPauseGame() = false//OptionCore.GUI_PAUSE.isEnabled

    override fun onGuiClosed() {
        showCursor()

        close()
    }

    protected open fun close() = categories.forEach { it.close() }

    private fun showCursor() {
        if (cursorHidden) toggleHideCursor()
    }

    override val parentX: Int
        get() = getCursorX()

    override val parentY: Int
        get() = getCursorY()

    override val parentZ = 0

    operator fun plusAssign(category: CategoryData) {
        this.categories += category
    }

    operator fun CategoryData.unaryPlus() {
        this@ScreenGUI += this
    }
}
