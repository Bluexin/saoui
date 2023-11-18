package com.tencao.saoui.util

import me.shedaniel.architectury.platform.Platform


object ClientUtil {

    // val saoConfDir: File get() = confDir(File(Client.minecraft.gameDir, "config"))
    val isFTBTeamsLoaded: Boolean
        get() = Platform.isModLoaded("ftbteams")
}