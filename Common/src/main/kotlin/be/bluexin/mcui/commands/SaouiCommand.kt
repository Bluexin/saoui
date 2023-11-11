package be.bluexin.mcui.commands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import net.minecraft.commands.CommandSourceStack

object SaouiCommand {

    const val ROOT = "saoui"

    private fun CommandDispatcher<CommandSourceStack>.register(
        vararg commands: Command.Commands
    ) {
        register(
            commands.asSequence()
                .flatMap(Command.Commands::values)
                .fold(literal(ROOT)) { builder, command ->
                    builder.then(command.register())
                }
        )
    }

    fun setup(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher.register(GeneralCommands, DebugCommands)
    }

    fun useCommand(command: GeneralCommands) = "/$ROOT ${command.id}"

//    override fun getUsage(sender: ICommandSender) = "commands.saoui.usage"
}
