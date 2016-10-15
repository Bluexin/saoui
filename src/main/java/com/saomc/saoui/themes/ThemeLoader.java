package com.saomc.saoui.themes;

import com.saomc.saoui.SAOCore;
import com.saomc.saoui.themes.elements.Hud;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;

/**
 * Part of saoui by Bluexin.
 *
 * @author Bluexin
 */
public class ThemeLoader {

    public static Hud HUD;

    public static void load() throws JAXBException {
        ResourceLocation hudRL = new ResourceLocation(SAOCore.MODID, "themes/hud.xml");

        JAXBContext context = JAXBContext.newInstance(Hud.class);
        Unmarshaller um = context.createUnmarshaller();

        try (java.io.InputStream is = Minecraft.getMinecraft().getResourceManager().getResource(hudRL).getInputStream()) {
            HUD = (Hud) um.unmarshal(is);
        } catch (IOException e) {
            e.printStackTrace();
        }

        HUD.setup();
    }
}
