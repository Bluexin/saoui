package com.tencao.saoui.commands

import com.tencao.saomclib.commands.CommandBase
import com.tencao.saoui.themes.AbstractThemeLoader
import net.minecraft.command.ICommandSender
import net.minecraft.server.MinecraftServer
import net.minecraft.util.text.TextComponentString

@Suppress("unused") // automatic
enum class GeneralCommands : CommandBase {
    PRINT_ERRORS {
        override fun execute(server: MinecraftServer, sender: ICommandSender, params: Array<String>) {
            AbstractThemeLoader.Reporter.errors.forEach {
                sender.sendMessage(TextComponentString(it))
            }
        }
    };

    private val id = "general.${name.lowercase()}"

    override fun getID() = id

    override fun getUsage(sender: ICommandSender): String {
        return "commands.general.${name.lowercase()}.usage"
    }

    companion object {
        val values = values()
        val indexedValues = values.associateBy(GeneralCommands::id)
    }
}