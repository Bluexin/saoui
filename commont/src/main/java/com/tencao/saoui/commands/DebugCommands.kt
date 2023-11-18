package com.tencao.saoui.commands

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.tencao.saoui.themes.AbstractThemeLoader
import com.tencao.saoui.util.toTextComponent
import com.tencao.saoui.util.translate
import net.minecraft.commands.CommandSource
import net.minecraft.world.entity.player.Player

enum class DebugCommands(
    private vararg val arguments: LiteralArgumentBuilder<CommandSource>
) {
    PRINT_ERRORS {
        override fun execute(c: CommandContext<CommandSource>): Int {
            AbstractThemeLoader.Reporter.errors.forEach {
                (c.source as Player?)?.sendMessage(it.toTextComponent(), (c.source as Player?)?.uuid) ?: c.source.sendMessage(it.translate(), (c.source as Player?)?.uuid)
            }
            return AbstractThemeLoader.Reporter.errors.size
        }
    };

    val id = "general.${name.lowercase()}"

    /*fun getUsage(sender: ICommandSender): String {
        return "commands.general.${name.lowercase()}.usage"
    }*/

    abstract fun execute(c: CommandContext<CommandSource>): Int

    open fun register(): LiteralArgumentBuilder<CommandSource> = arguments.fold(
        LiteralArgumentBuilder.literal<CommandSource>(
            id
        )
    ) { builder, arg ->
        builder.then(arg)
    }.executes(::execute)

    companion object {
        val values = values()
        val indexedValues = values.associateBy(DebugCommands::id)
    }
}