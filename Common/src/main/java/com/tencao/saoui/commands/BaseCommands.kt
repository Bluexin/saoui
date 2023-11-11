package com.tencao.saoui.commands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import net.minecraft.command.CommandSource

object BaseCommands {

    const val ROOT = "saoui"

    fun setup(dispatcher: CommandDispatcher<CommandSource>) {
        dispatcher.register(
            DebugCommands.values.fold(LiteralArgumentBuilder.literal(ROOT)) { builder, command -> builder.then(command.register()) }
        )
    }

    fun useCommand(command: DebugCommands) = "/$ROOT ${command.id}"
}