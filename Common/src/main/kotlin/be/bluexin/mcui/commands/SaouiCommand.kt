package be.bluexin.mcui.commands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import net.minecraft.commands.CommandSourceStack

object SaouiCommand {

    const val ROOT = "saoui"

    fun setup(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher.register(
            GeneralCommands.values.fold(literal(ROOT)) { builder, command -> builder.then(command.register()) }
        )
    }

    fun useCommand(command: GeneralCommands) = "/$ROOT ${command.id}"

//    override fun getUsage(sender: ICommandSender) = "commands.saoui.usage"
}
