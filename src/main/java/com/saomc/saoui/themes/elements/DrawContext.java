package com.saomc.saoui.themes.elements;

import net.minecraft.client.Minecraft;

/**
 * Part of saoui by Bluexin.
 * Provides extra info for what's about to be drawn.
 *
 * @author Bluexin
 */
public class DrawContext {
    /*
    Feel free to add anything you'd need here.
     */
    private final String username;
    private final Minecraft mc;
    private final double usernameWidth;
    private double z;
    private double hp;

    public DrawContext(String username, Minecraft mc, double z) {
        this.username = username;
        this.mc = mc;
        this.z = z;

        this.usernameWidth = (1 + (mc.fontRendererObj.getStringWidth(username) + 4) / 5) * 5;
    }

    public String getUsername() {
        return username;
    }

    public Minecraft getMc() {
        return mc;
    }

    public double getZ() {
        return z;
    }

    public void setZ(float z) {
        this.z = z;
    }

    public double getUsernameWidth() {
        return usernameWidth;
    }

    public double getHp() {
        return hp;
    }

    public void setHp(double hp) {
        this.hp = hp;
    }
}
