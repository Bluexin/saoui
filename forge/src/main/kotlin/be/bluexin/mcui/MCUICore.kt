package be.bluexin.mcui

import be.bluexin.mcui.commands.SaouiCommand
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.RegisterCommandsEvent
import net.minecraftforge.fml.common.Mod

@Mod(Constants.MOD_ID)
object MCUICore {
    init {

        // This method is invoked by the Forge mod loader when it is ready
        // to load your mod. You can access Forge and Common code in this
        // project.

        // Use Forge to bootstrap the Common mod.
        Constants.LOG.info("Hello Kotlin Forge world!")
        CommonClass.init()

        MinecraftForge.EVENT_BUS.addListener(::registerCommands)
    }

    private fun registerCommands(event: RegisterCommandsEvent) {
        SaouiCommand.setup(event.dispatcher)
    }
}