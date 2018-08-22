package com.saomc.saoui.util

import com.saomc.saoui.GLCore
import com.saomc.saoui.GLCore.glTexturedRectV2
import com.saomc.saoui.SAOCore
import com.saomc.saoui.api.screens.IIcon
import net.minecraft.util.ResourceLocation
import cpw.mods.fml.relauncher.Side
import cpw.mods.fml.relauncher.SideOnly

@SideOnly(Side.CLIENT)
enum class IconCore : IIcon {

    NONE {
        override fun glDraw(x: Int, y: Int)  = Unit
        override fun glDrawUnsafe(x: Int, y: Int) = Unit
        override fun getRL(): ResourceLocation? = null
    },
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
        GLCore.color(1f, 1f, 1f, 1f)
        GLCore.glBlend(true)
        GLCore.glBindTexture(rl)
        glTexturedRectV2(x.toDouble(), y.toDouble(), width = 16.0, height = 16.0, srcX = 0.0, srcY = 0.0, srcWidth = 256.0, srcHeight = 256.0)
        GLCore.glBlend(false)
    }

    override fun glDrawUnsafe(x: Int, y: Int) {
        glTexturedRectV2(x.toDouble(), y.toDouble(), width = 16.0, height = 16.0, srcX = 0.0, srcY = 0.0, srcWidth = 256.0, srcHeight = 256.0)
    }

    override fun getRL(): ResourceLocation? {
        return rl
    }

    private val rl by lazy { ResourceLocation(SAOCore.MODID, "textures/menu/icons/${name.toLowerCase()}.png") }
}
