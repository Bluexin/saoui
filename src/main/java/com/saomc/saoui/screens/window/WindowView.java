package com.saomc.saoui.screens.window;

import com.saomc.saoui.GLCore;
import com.saomc.saoui.SAOCore;
import com.saomc.saoui.api.screens.ParentElement;
import com.saomc.saoui.api.screens.WindowAlign;
import com.saomc.saoui.resources.StringNames;
import com.saomc.saoui.util.ColorUtil;
import com.saomc.saoui.config.OptionCore;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@SideOnly(Side.CLIENT)
public class WindowView extends Gui{

    private String title;
    private boolean multiButton;
    private String[] lines;
    private ParentElement parentElement;
    private WindowAlign horizontal;
    private WindowAlign vertical;
    private Minecraft mc = Minecraft.getMinecraft();
    private int height;
    private int width;
    private int x;
    private int y;

    public WindowView(String title, boolean multiButton, String message, int x, int y, ParentElement parentElement) {
        this.title = title;
        this.multiButton = multiButton;
        this.parentElement = parentElement;
        this.x = x;
        this.y = y;
        this.height = 60;
        this.width = 200;
        this.lines = toLines(message, this.width - 10);
    }

    public WindowView(String title, boolean multiButton, String message, WindowAlign horizontal, WindowAlign vertical, ParentElement parentElement) {
        this.title = title;
        this.multiButton = multiButton;
        this.parentElement = parentElement;
        this.horizontal = horizontal;
        this.vertical = vertical;
        this.height = 60;
        this.width = 200;
        this.lines = toLines(message, this.width - 10);
        SAOCore.LOGGER.info("Popup Launched");

    }

    public void updateScreen(){
        SAOCore.LOGGER.info("Updating");
        int w = lines.length > 0 ? Stream.of(lines).mapToInt(GLCore::glStringWidth).max().getAsInt() + 16 : 0;
        if (w > width) width = w;

        final int linesHeight = lines.length * GLCore.glStringHeight() + 16;
        if (linesHeight > height) height = linesHeight;

    }


    public void drawScreen(int mouseX, int mouseY, float partialTicks){
        //drawDefaultBackground();
        SAOCore.LOGGER.info("Drawing");

        int left;
        int top;
        final int boxSize = 20;
        final int width2 = width /2;
        final int size = height - (boxSize * 2);

        int w = Stream.of(lines).mapToInt(GLCore::glStringWidth).max().getAsInt();

        if (horizontal != null) left = horizontal.getPos(width, parentElement, false, w);
        else left = x + getX(false);

        if (vertical != null) top = vertical.getPos(height, parentElement, false, GLCore.glStringHeight());
        else top = y + getY(false);

        GLCore.glBindTexture(OptionCore.SAO_UI.isEnabled() ? StringNames.gui : StringNames.guiCustom);
        GLCore.glColorRGBA(ColorUtil.DEFAULT_COLOR.multiplyAlpha(1.0F));

        GLCore.glTexturedRect(left, top, width2, boxSize, 0, 65, width2, 20);
        GLCore.glTexturedRect(left + width2, top, width2, boxSize, 200 - width2, 65, width2, 20);

        if (size > 0) {
            final int borderSize = Math.min(size / 2, 10);

            GLCore.glTexturedRect(left, top + boxSize, 0, 85, width2, borderSize);
            GLCore.glTexturedRect(left + width2, top + boxSize, 200 - width2, 85, width2, borderSize);

            if ((size + 1) / 2 > 10)
                GLCore.glTexturedRect(left, top + boxSize + borderSize, width, size - borderSize * 2, 0, 95, 200, 10);

            GLCore.glTexturedRect(left, top + boxSize + size - borderSize, 0, 115 - borderSize, width2, borderSize);
            GLCore.glTexturedRect(left + width2, top + boxSize + size - borderSize, 200 - width2, 115 - borderSize, width2, borderSize);
        }

        GLCore.glTexturedRect(left, top + size + boxSize, width2, boxSize, 0, 65, width2, 20);
        GLCore.glTexturedRect(left + width2, top + size + boxSize, width2, boxSize, 200 - width2, 65, width2, 20);


        for (int i = 0; i < lines.length; i++)
            GLCore.glString(lines[i], left + 8, top + 8 + i * (GLCore.glStringHeight() + 1), ColorUtil.DEFAULT_FONT_COLOR.multiplyAlpha(1.0F));
    }


    private static String[] toLines(String text, int width) {
        if (width <= 0) return text.split("\n");
        else {
            final String[] rawLines = text.split("\n");

            if (rawLines.length <= 0) return rawLines;

            final List<String> lines = new ArrayList<>();

            StringBuilder cut = new StringBuilder();
            String line = rawLines[0];
            int rawIndex = 0;

            while (line != null) {
                int size = GLCore.glStringWidth(line);

                while (size > width - 16) {
                    final int lastIndex = line.lastIndexOf(' ');

                    if (lastIndex != -1) {
                        cut.insert(0, line.substring(lastIndex + 1) + " ");
                        line = line.substring(0, lastIndex);

                        if (rawIndex + 1 < rawLines.length) {
                            rawLines[rawIndex + 1] = cut + rawLines[rawIndex + 1];
                            cut = new StringBuilder();
                        }
                    } else break;

                    size = GLCore.glStringWidth(line);
                }

                if (!line.matches(" *")) lines.add(line);

                if (cut.length() > 0) {
                    line = cut.toString();
                    cut = new StringBuilder();
                } else if (++rawIndex < rawLines.length) line = rawLines[rawIndex];
                else line = null;
            }

            return lines.toArray(new String[lines.size()]);
        }
    }

    public int getY(boolean relative) {
        return relative ? y : y + (parentElement != null ? parentElement.getY(relative) : 0) + (relative ? 0 : height / 2);
    }

    public int getX(boolean relative) {
        return relative ? x : x + (parentElement != null ? parentElement.getX(relative) : 0);
    }

}
