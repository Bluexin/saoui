package be.bluexin.mcui.util

import be.bluexin.mcui.platform.Services

object ModHelper {

    val isTogetherForeverLoaded by lazy { Services.PLATFORM.isModLoaded("togetherforever") }

    val isFTBLibLoaded by lazy { Services.PLATFORM.isModLoaded("ftblib") }
}
