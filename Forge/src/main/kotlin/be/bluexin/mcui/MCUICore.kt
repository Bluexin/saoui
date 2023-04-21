package be.bluexin.mcui

import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.util.ObfuscationReflectionHelper

@Mod(Constants.MOD_ID)
object MCUICore {
    init {

        // This method is invoked by the Forge mod loader when it is ready
        // to load your mod. You can access Forge and Common code in this
        // project.

        // Use Forge to bootstrap the Common mod.
        Constants.LOG.info("Hello Kotlin Forge world!")
        CommonClass.init()
    }
}