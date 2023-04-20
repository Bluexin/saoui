package be.bluexin.mcui.commands

import be.bluexin.mcui.themes.AbstractThemeLoader
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.network.chat.Component

@Suppress("unused") // automatic
enum class GeneralCommands(
    private vararg val arguments: ArgumentBuilder<CommandSourceStack, *>
) {
    PRINT_ERRORS {
        override fun execute(c: CommandContext<CommandSourceStack>): Int {
            AbstractThemeLoader.Reporter.errors.forEach {
                c.source.sendSystemMessage(Component.literal(it))
            }
            return AbstractThemeLoader.Reporter.errors.size
        }
    };

    val id = "general.${name.lowercase()}"

    /*fun getUsage(sender: ICommandSender): String {
        return "commands.general.${name.lowercase()}.usage"
    }*/

    abstract fun execute(c: CommandContext<CommandSourceStack>): Int

    open fun register(): LiteralArgumentBuilder<CommandSourceStack> = arguments.fold(literal<CommandSourceStack>(id)) { builder, arg ->
        builder.then(arg)
    }.executes(::execute)

    companion object {
        val values = values()
        val indexedValues = values.associateBy(GeneralCommands::id)
    }
}