package com.saomc.saoui.util

import com.saomc.saoui.GLCore
import com.saomc.saoui.SAOCore
import com.saomc.saoui.api.screens.IIcon
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

@SideOnly(Side.CLIENT)
enum class IconCore : IIcon {

    NONE,
    OPTION,
    HELP,
    LOGOUT,
    CANCEL,
    CONFIRM,
    SETTINGS,
    NAVIGATION,
    MESSAGE,
    SOCIAL,
    PROFILE,
    EQUIPMENT,
    ITEMS,
    SKILLS,
    GUILD,
    PARTY,
    FRIEND,
    CREATE,
    INVITE,
    QUEST,
    FIELD_MAP,
    DUNGEON_MAP,
    ARMOR,
    ACCESSORY,
    MESSAGE_RECEIVED,
    CRAFTING,
    SPRINTING,
    SNEAKING;

    override fun glDraw(x: Int, y: Int) {
        GLCore.glColor(1f, 1f, 1f, 1f)
        GLCore.glBlend(true)
        GLCore.glBindTexture(rl)
        GLCore.glTexturedRect(x.toDouble(), y.toDouble(), 16.0, 16.0, 0.0, 0.0, 256.0, 256.0)
        GLCore.glBlend(false)
    }

    override fun glDrawUnsafe(x: Int, y: Int) {
        GLCore.glTexturedRect(x.toDouble(), y.toDouble(), 16.0, 16.0, 0.0, 0.0, 256.0, 256.0)
    }

    override fun getRL(): ResourceLocation? {
        return rl
    }

    private val rl by lazy { ResourceLocation(SAOCore.MODID, "textures/menu/icons/${name.toLowerCase()}.png") }
}
