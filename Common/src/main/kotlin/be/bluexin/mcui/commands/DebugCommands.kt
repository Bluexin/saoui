package be.bluexin.mcui.commands

import be.bluexin.mcui.Constants
import be.bluexin.mcui.screens.LuaTestScreen
import be.bluexin.mcui.themes.AbstractThemeLoader
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import com.mojang.brigadier.context.CommandContext
import net.minecraft.client.Minecraft
import net.minecraft.commands.CommandSourceStack
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Player

@Suppress("unused") // automatic
enum class DebugCommands(
    override vararg val arguments: ArgumentBuilder<CommandSourceStack, *>
) : Command {
    OPEN_TEST_GUI {
        override fun execute(c: CommandContext<CommandSourceStack>): Int {
            Minecraft.getInstance().tell {
                Minecraft.getInstance().setScreen(LuaTestScreen())
            }
            return 1
        }
    };

    override val id = "debug.${name.lowercase()}"

    /*fun getUsage(sender: ICommandSender): String {
        return "commands.general.${name.lowercase()}.usage"
    }*/

    companion object : Command.Commands {
        override val values = values().asIterable()
        val indexedValues = values.associateBy(DebugCommands::id)
    }
}