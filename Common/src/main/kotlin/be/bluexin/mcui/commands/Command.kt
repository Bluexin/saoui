package be.bluexin.mcui.commands

import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandSourceStack

interface Command {
    val arguments: Array<out ArgumentBuilder<CommandSourceStack, *>>
    val id: String
    fun execute(c: CommandContext<CommandSourceStack>): Int
    fun register(): LiteralArgumentBuilder<CommandSourceStack> = arguments.fold(
        LiteralArgumentBuilder.literal(id),
        LiteralArgumentBuilder<CommandSourceStack>::then
    ).executes(::execute)

    interface Commands {
        val values: Iterable<Command>
    }
}