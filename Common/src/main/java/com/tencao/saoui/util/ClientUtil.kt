package com.tencao.saoui.util

import com.tencao.saomclib.SAOMCLib

object ClientUtil {

    // val saoConfDir: File get() = confDir(File(Client.minecraft.gameDir, "config"))
    val isSAOMCLibServerSide: Boolean
        get() = SAOMCLib.proxy.isServerSideLoaded
}