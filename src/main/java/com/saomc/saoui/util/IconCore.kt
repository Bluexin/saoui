package com.saomc.saoui.util

import com.saomc.saoui.GLCore
import com.saomc.saoui.api.screens.IIcon
import com.saomc.saoui.config.OptionCore
import com.saomc.saoui.resources.StringNames
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

    private val srcX: Int
        get() = index() % 16 * SRC_SIZE

    private val srcY: Int
        get() = index() / 16 * SRC_SIZE

    private fun index(): Int {
        return ordinal - 1
    }

    override fun glDraw(x: Int, y: Int) {
        if (index() >= 0) {
            GLCore.glColor(1f, 1f, 1f, 1f)
            GLCore.glBlend(true)
            GLCore.glBindTexture(if (OptionCore.SAO_UI.isEnabled) StringNames.icons else StringNames.iconsCustom)
            GLCore.glTexturedRect(x.toDouble(), y.toDouble(), srcX.toDouble(), srcY.toDouble(), SRC_SIZE.toDouble(), SRC_SIZE.toDouble())
            GLCore.glBlend(false)
        }
    }

    override fun glDrawUnsafe(x: Int, y: Int) {
        GLCore.glTexturedRect(x.toDouble(), y.toDouble(), srcX.toDouble(), srcY.toDouble(), SRC_SIZE.toDouble(), SRC_SIZE.toDouble())
    }

    companion object {

        private val SRC_SIZE = 16
    }

}
