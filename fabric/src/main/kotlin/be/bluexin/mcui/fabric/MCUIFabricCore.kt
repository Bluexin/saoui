package be.bluexin.mcui.fabric

import be.bluexin.mcui.CommonClass
import be.bluexin.mcui.commands.SaouiCommand
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback

@Suppress("unused")
object MCUIFabricCore : ClientModInitializer {
    override fun onInitializeClient() {
        CommonClass.init()
        CommandRegistrationCallback.EVENT.register { commandDispatcher, _, _ ->
            SaouiCommand.setup(commandDispatcher)
        }
    }
}
