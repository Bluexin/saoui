package com.saomc.saoui.screens.window;

import com.saomc.saoui.GLCore;
import com.saomc.saoui.api.screens.Actions;
import com.saomc.saoui.api.screens.GuiSelection;
import com.saomc.saoui.api.screens.ParentElement;
import com.saomc.saoui.colorstates.CursorStatus;
import com.saomc.saoui.config.OptionCore;
import com.saomc.saoui.elements.Element;
import com.saomc.saoui.elements.ElementController;
import com.saomc.saoui.elements.ElementDispatcher;
import com.saomc.saoui.elements.defaultelements.DefaultElements;
import com.saomc.saoui.resources.StringNames;
import com.saomc.saoui.themes.ThemeLoader;
import com.saomc.saoui.util.ColorUtil;
import com.saomc.saoui.util.LogCore;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Cursor;
import org.lwjgl.input.Mouse;

import javax.xml.bind.JAXBException;
import java.util.ConcurrentModificationException;

@SideOnly(Side.CLIENT)
public abstract class ScreenGUI extends GuiScreen implements ParentElement {

    private static final float ROTATION_FACTOR = 0.25F;
    protected static CursorStatus CURSOR_STATUS = CursorStatus.SHOW;
    private final Cursor emptyCursor;
    private GuiSelection type;
    private int mouseX, mouseY;
    private int mouseDown;
    private float mouseDownValue;
    private float[] rotationYaw, rotationPitch;
    private boolean cursorHidden = false;
    private boolean lockCursor = false;

    protected ScreenGUI(GuiSelection guiSelection) {
        super();
        type = guiSelection;
        DefaultElements.dispatchItemFilter();
        if (ElementDispatcher.isEmpty()) ElementDispatcher.clear();
        ElementDispatcher.getInstance().dispatch(this, guiSelection);
        Cursor cursor = null;
        try {
            cursor = new Cursor(1, 1, 0, 0, 1, BufferUtils.createIntBuffer(1), null);
        } catch (LWJGLException e) {
            e.printStackTrace();
        } finally {
            emptyCursor = cursor;
        }
    }

    @Override
    public void initGui() {
        if (CURSOR_STATUS != CursorStatus.DEFAULT) hideCursor();

        super.initGui();
        init();
    }

    protected void init() {
        if (mc.thePlayer != null) {
            rotationYaw = new float[]{mc.thePlayer.rotationYaw};
            rotationPitch = new float[]{mc.thePlayer.rotationPitch};
        }
    }

    private int getCursorX() {
        if (OptionCore.CURSOR_TOGGLE.isEnabled()) return lockCursor ? 0 : (width / 2 - mouseX) / 2;
        else return !isCtrlKeyDown() ? (width / 2 - mouseX) / 2 : 0;
    }

    private int getCursorY() {
        if (OptionCore.CURSOR_TOGGLE.isEnabled()) return lockCursor ? 0 : (height / 2 - mouseY) / 2;
        else return !isCtrlKeyDown() ? (height / 2 - mouseY) / 2 : 0;
    }

    @Override
    public int getX(boolean relative) {
        return getCursorX();
    }

    @Override
    public int getY(boolean relative) {
        return getCursorY();
    }

    @Override
    public void updateScreen() {
        if (ElementDispatcher.isEmpty()) return;
        ElementDispatcher.check();
        ElementDispatcher.getElements().forEach(e -> e.update(mc));
    }

    @Override
    public void drawScreen(int cursorX, int cursorY, float partialTicks) {
        if (ElementDispatcher.isEmpty()) return;
        mouseX = cursorX;
        mouseY = cursorY;

        if (mc.thePlayer != null) {
            mc.thePlayer.rotationYaw = rotationYaw[0] - getCursorX() * ROTATION_FACTOR;
            mc.thePlayer.rotationPitch = rotationPitch[0] - getCursorY() * ROTATION_FACTOR;
        }

//        super.drawScreen(cursorX, cursorY, partialTicks); -> we might not want this to be called. Shouldn't have any effect ("empty" call)

        GLCore.glStartUI(mc);

        ElementDispatcher.getElements().forEach(e -> e.draw(mc, cursorX, cursorY));

        if (CURSOR_STATUS == CursorStatus.SHOW) { // TODO: maybe there's a way to move all of this to the actual org.lwjgl.input.Cursor

            GLCore.glBlend(true);
            GLCore.tryBlendFuncSeparate(770, 771, 1, 0);
            GLCore.glBindTexture(OptionCore.SAO_UI.isEnabled() ? StringNames.gui : StringNames.guiCustom);

            if (mouseDown != 0) {
                final float fval = partialTicks * 0.1F;

                if (mouseDownValue + fval < 1.0F) mouseDownValue += fval;
                else mouseDownValue = 1.0F;

                GLCore.glColorRGBA(ColorUtil.CURSOR_COLOR.multiplyAlpha(mouseDownValue));
                GLCore.glTexturedRect(cursorX - 7, cursorY - 7, 35, 115, 15, 15);

                GLCore.glColorRGBA(ColorUtil.DEFAULT_COLOR);
            } else {
                mouseDownValue = 0;

                GLCore.glColorRGBA(ColorUtil.CURSOR_COLOR);
            }

            GLCore.glTexturedRect(cursorX - 7, cursorY - 7, 20, 115, 15, 15);
            GLCore.glBlend(false);
        }

        GLCore.glEndUI(mc);
    }


    @Override
    protected void keyTyped(char ch, int key) {
        if (OptionCore.CURSOR_TOGGLE.isEnabled() && isCtrlKeyDown()) lockCursor = !lockCursor;
        super.keyTyped(ch, key);

        ElementDispatcher.getElements().stream().anyMatch(e -> e.keyTyped(mc, ch, key));
        LogCore.logDebug("ch - " + ch + " key - " + key);

        //elements.menuElements.keySet().stream().filter(Element::isFocus).forEach(element -> actionPerformed(element, Actions.KEY_TYPED, key));
    }

    @Override
    protected void mouseClicked(int cursorX, int cursorY, int button) {
        super.mouseClicked(cursorX, cursorY, button);
        mouseDown |= (0x1 << button);

        try {
            if (ElementDispatcher.getElements().stream().noneMatch(controller -> controller.mousePressed(mc, cursorX, cursorY, button)))
                backgroundClicked(cursorX, cursorY, button);
        } catch (ConcurrentModificationException e) {
            //Do Nothing
            LogCore.logDebug("mouseClicked ended unexpectedly");
        }
    }

    @Override
    protected void mouseReleased(int cursorX, int cursorY, int button) {
        super.mouseReleased(cursorX, cursorY, button);
        mouseDown &= ~(0x1 << button);

        boolean found = false;

        try {
            for (ElementController elementController : ElementDispatcher.getElements())
                if (found) break;
                else for (Element element : elementController.elements)
                    if (element.isOpen() && element.mouseReleased(mc, cursorX, cursorY, button) ||
                            element.isFocus() && elementController.mouseOver(cursorX, cursorY, button) && element.mouseReleased(mc, cursorX, cursorY, button)) {
                        ElementController.actionPerformed(element, Actions.LEFT_RELEASED, button);
                        found = true;
                        break;
                    }

        } catch (ConcurrentModificationException e) {
            //Do Nothing
            LogCore.logWarn("mouseClicked ended unexpectedly");
        }
    }

    private void backgroundClicked(int cursorX, int cursorY, int button) {
        LogCore.logDebug("Background Clicked");

        try {
            ThemeLoader.load();
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    private void mouseWheel(int cursorX, int cursorY, int delta) {
        boolean found = false;

        try {
            ElementDispatcher.getElements().stream().anyMatch(e -> e.mouseWheel(mc, cursorX, cursorY, delta));
        } catch (ConcurrentModificationException e) {
            //Do Nothing
            LogCore.logWarn("mouseWheel ended unexpectedly");
        }
    }

    @Override
    public void handleMouseInput() {
        super.handleMouseInput();

        if (Mouse.hasWheel()) {
            final int x = Mouse.getEventX() * width / mc.displayWidth;
            final int y = height - Mouse.getEventY() * height / mc.displayHeight - 1;
            final int delta = Mouse.getEventDWheel();

            if (delta != 0) mouseWheel(x, y, delta);
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return OptionCore.GUI_PAUSE.isEnabled();
    }

    @Override
    public void onGuiClosed() {
        showCursor();

        close();
    }

    protected void close() {
        ElementDispatcher.close();
    }

    private void hideCursor() {
        if (!cursorHidden) toggleHideCursor();
    }

    private void showCursor() {
        if (cursorHidden) toggleHideCursor();
    }

    private void toggleHideCursor() {
        cursorHidden = !cursorHidden;
        try {
            Mouse.setNativeCursor(cursorHidden ? emptyCursor : null);
        } catch (LWJGLException ignored) {
        }
    }
}
