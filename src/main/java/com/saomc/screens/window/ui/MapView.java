package com.saomc.screens.window.ui;

import com.saomc.GLCore;
import com.saomc.resources.StringNames;
import com.saomc.screens.Elements;
import com.saomc.screens.ParentElement;
import com.saomc.util.ColorUtil;
import com.saomc.util.OptionCore;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class MapView extends Elements {

    private static final int MAP_SIZE = 16;
    private static final int MAP_DATA_SIZE = (MAP_SIZE * 2 + 1);

    private final EntityPlayer character;
    public int zoom;
    private int[][] map;
    private long medium;
    private int min, max;
    private int scan;

    public MapView(ParentElement gui, int xPos, int yPos, int size, EntityPlayer player) {
        super(gui, xPos, yPos, MAP_DATA_SIZE * size, MAP_DATA_SIZE * size + 16);
        character = player;
        map = null;
        scan = 16;
        zoom = 1;
    }

    private void scanMap() {
        if (character != null && character.worldObj != null) {
            int i, j;

            map = new int[MAP_DATA_SIZE][MAP_DATA_SIZE];
            medium = 0L;
            min = character.worldObj.getActualHeight();
            max = 0;

            final int originY = (int) (character.posY - 1.0);
            int count = 0;

            for (i = -16; i <= 16; i++) {
                for (j = -16; j <= 16; j++) {
                    final int x = (int) ((Math.round(character.posX) / zoom + i) * zoom);
                    final int z = (int) ((Math.round(character.posZ) / zoom + j) * zoom);

                    int endScan = 0x0;
                    int y = 0;

                    do {
                        if (originY + y < character.worldObj.getActualHeight() &&
                                character.worldObj.isAirBlock(new BlockPos(x, originY + y, z)) != character.worldObj.isAirBlock(new BlockPos(x, originY + y + 1, z))) {
                            break;
                        } else endScan |= 0x1;

                        if (originY - y > 0 &&
                                character.worldObj.isAirBlock(new BlockPos(x, originY - y, z)) != character.worldObj.isAirBlock(new BlockPos(x, originY - (y + 1), z))) {
                            y *= -1;
                            break;
                        } else endScan |= 0x2;

                        y++;
                    } while (y < scan);

                    if (y == scan) map[16 + i][16 + j] = -endScan;
                    else {
                        map[16 + i][16 + j] = (originY + y);

                        medium += map[16 + i][16 + j];
                        count++;

                        if (map[16 + i][16 + j] < min) min = map[16 + i][16 + j];

                        if (map[16 + i][16 + j] > max) max = map[16 + i][16 + j];
                    }
                }
            }

            for (i = -16; i <= 16; i++)
                for (j = -16; j <= 16; j++)
                    if (map[16 + i][16 + j] < 0) {
                        map[16 + i][16 + j] = (map[16 + i][16 + j] == -2 ? min : max);

                        medium += map[16 + i][16 + j];
                        count++;
                    }

            if (count > 0) medium /= count;
        } else map = null;
    }

    @Override
    public void update(Minecraft mc) {
        super.update(mc);

        if (mc.thePlayer != character) {
            final int zoom_factor = (int) (character.getDistanceSqToEntity(mc.thePlayer) / (MAP_DATA_SIZE * MAP_DATA_SIZE));

            if (zoom_factor > zoom) {
                zoom = zoom_factor;
            }
        }

        if (map == null) {
            scanMap();
        }
    }

    @Override
    public void draw(Minecraft mc, int cursorX, int cursorY) {
        super.draw(mc, cursorX, cursorY);

        if ((visibility > 0) && (map != null)) {
            final int left = getX(false) + width / 2;
            final int top = getY(false) + height / 2;

            final int size = (width / map.length + (height - 16) / map.length) / 2;

            if (size > 0) {
                int i, j;

                GLCore.glBlend(true);
                GLCore.glTexture2D(false);

                GLCore.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

                final int direction = (Math.round(mc.thePlayer.rotationYaw / 90) % 4);

                for (i = -16; i <= 16; i++) {
                    for (j = -16; j <= 16; j++) {
                        final int x, z;

                        if (direction == 0) {
                            x = -i;
                            z = -j;
                        } else if (direction == 1) {
                            x = j;
                            z = -i;
                        } else if (direction == 2) {
                            x = i;
                            z = j;
                        } else {
                            x = -j;
                            z = i;
                        }

                        final int y = (int) (map[16 + x][16 + z] - medium);

                        final float valueY;

                        valueY = (y < 0) && (medium != min) ? (float) y / (medium - min) : (y >= 0) && (medium != max) ? (float) y / (max - medium) : y;

                        final float blue = (1.0F + valueY) / 2;

                        GLCore.glColor(0, 0, blue, visibility);
                        GLCore.glRect(left + i * size, top + j * size, size, size);
                    }
                }

                GLCore.glTexture2D(true);
                GLCore.glBlend(true);

                GLCore.glBindTexture(OptionCore.SAO_UI.getValue() ? StringNames.gui : StringNames.guiCustom);

                if (mc.thePlayer != character) {
                    GLCore.glColorRGBA(ColorUtil.CANCEL_COLOR.multiplyAlpha(visibility));
                    GLCore.glTexturedRect(left - size, top - size, size * 2, size * 2, 0, 25, 20, 20);

                    final int offsetX = (int) ((character.posZ - mc.thePlayer.posZ) / zoom);
                    final int offsetY = (int) ((character.posX - mc.thePlayer.posX) / zoom);

                    final int x, y;

                    switch (direction) {
                        case 0:
                            x = offsetY;
                            y = offsetX;
                            break;
                        case 1:
                            x = offsetX;
                            y = -offsetY;
                            break;
                        case 2:
                            x = -offsetY;
                            y = -offsetX;
                            break;
                        default:
                            x = -offsetX;
                            y = offsetY;
                            break;
                    }

                    if ((Math.abs(x) < MAP_SIZE) && (Math.abs(y) < MAP_SIZE)) {
                        GLCore.glColorRGBA(ColorUtil.HOVER_COLOR.multiplyAlpha(visibility));
                        GLCore.glTexturedRect(left - size + x * size, top - size + y * size, size * 2, size * 2, 0, 25, 20, 20);
                    }
                } else {
                    GLCore.glColorRGBA(ColorUtil.HOVER_COLOR.multiplyAlpha(visibility));
                    GLCore.glTexturedRect(left - size, top - size, size * 2, size * 2, 0, 25, 20, 20);
                }
            }
        }
    }

}
