package com.saomc.screens;

import com.saomc.GLCore;
import com.saomc.util.ColorUtil;
import com.saomc.util.J8String;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@SideOnly(Side.CLIENT)
public class TextGUI extends Elements {

    public ColorUtil fontColor;
    private String[] lines;

    public TextGUI(ParentElement gui, int xPos, int yPos, String... strings) {
        super(gui, xPos, yPos, 0, 0);
        lines = strings;
        fontColor = ColorUtil.DEFAULT_FONT_COLOR;
    }

    public TextGUI(ParentElement gui, int xPos, int yPos, String text, int width) {
        this(gui, xPos, yPos, toLines(text, width));
    }

    public TextGUI(ParentElement gui, int xPos, int yPos, String text) {
        this(gui, xPos, yPos, text, 0);
    }

    private static String[] toLines(String text, int width) {
        if (width <= 0) return text.split("\n");
        else {
            final String[] rawLines = text.split("\n");

            if (rawLines.length <= 0) return rawLines;

            final List<String> lines = new ArrayList<>();

            String cut = "";
            String line = rawLines[0];
            int rawIndex = 0;

            while (line != null) {
                int size = GLCore.glStringWidth(line);

                while (size > width - 16) {
                    final int lastIndex = line.lastIndexOf(' ');

                    if (lastIndex != -1) {
                        cut = line.substring(lastIndex + 1) + " " + cut;
                        line = line.substring(0, lastIndex);

                        if (rawIndex + 1 < rawLines.length) {
                            rawLines[rawIndex + 1] = cut + rawLines[rawIndex + 1];
                            cut = "";
                        }
                    } else break;

                    size = GLCore.glStringWidth(line);
                }

                if (!line.matches(" *")) lines.add(line);

                if (cut.length() > 0) {
                    line = cut;
                    cut = "";
                } else if (++rawIndex < rawLines.length) line = rawLines[rawIndex];
                else line = null;
            }

            return lines.toArray(new String[lines.size()]);
        }
    }

    @Override
    public void update(Minecraft mc) {
        int w = lines.length > 0 ? Stream.of(lines).mapToInt(GLCore::glStringWidth).max().getAsInt() + 16 : 0;
        if (w > width) width = w;

        final int linesHeight = lines.length * GLCore.glStringHeight() + 16;
        if (linesHeight > height) height = linesHeight;
    }

    @Override
    public void draw(Minecraft mc, int cursorX, int cursorY) {
        super.draw(mc, cursorX, cursorY);

        if (visibility > 0) {
            final int left = getX(false);
            final int top = getY(false);

            for (int i = 0; i < lines.length; i++)
                GLCore.glString(lines[i], left + 8, top + 8 + i * (GLCore.glStringHeight() + 1), fontColor.multiplyAlpha(visibility));
        }
    }

    public final String getText() {
        return J8String.join("\n", lines);
    }

    public final void setText(String text) {
        lines = toLines(text, width);
    }

}
