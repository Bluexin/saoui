package com.tencao.saoui.commands

import net.minecraft.command.CommandBase
import net.minecraft.command.ICommandSender
import net.minecraft.command.WrongUsageException
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.server.MinecraftServer
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.Style
import net.minecraft.util.text.TextFormatting
import net.minecraftforge.client.IClientCommand
import com.tencao.saomclib.commands.CommandBase as SCommandBase

object SaouiCommand : CommandBase(), IClientCommand {
    override fun getName() = "saoui"
    override fun getUsage(sender: ICommandSender) = "commands.saoui.usage"
    override fun getRequiredPermissionLevel() = 0
    override fun checkPermission(server: MinecraftServer, sender: ICommandSender) = sender is EntityPlayer

    override fun execute(server: MinecraftServer, sender: ICommandSender, params: Array<String>) {
        if (params.isEmpty()) throw WrongUsageException(getUsage(sender))

        GeneralCommands.indexedValues[params[0].lowercase()]
            ?.execute(server, sender, params.drop(1).toTypedArray())
            ?: throw WrongUsageException(getUsage(sender))
    }

    override fun getTabCompletions(server: MinecraftServer, sender: ICommandSender, params: Array<String>, targetPos: BlockPos?): MutableList<String> {
        val command = GeneralCommands.indexedValues[params[0].lowercase()]
            ?.takeIf { it.checkPermission(server, sender) }
            ?: return getListOfStringsMatchingLastWord(
                params, GeneralCommands.values
                    .filter { it.checkPermission(server, sender) }
                    .map(SCommandBase::getID)
            )
        return command.getTabCompletions(server, sender, params.drop(1).toTypedArray(), targetPos)
    }

    fun sendSuccess(sender: ICommandSender, message: ITextComponent) {
        sendMessage(sender, message.setStyle(Style().setParentStyle(message.style).setColor(TextFormatting.GREEN)))
    }

    fun sendError(sender: ICommandSender, message: ITextComponent) {
        sendMessage(sender, message.setStyle(Style().setParentStyle(message.style).setColor(TextFormatting.RED)))
    }

    fun sendMessage(sender: ICommandSender, message: ITextComponent) {
        sender.sendMessage(message)
    }

    override fun allowUsageWithoutPrefix(sender: ICommandSender?, message: String?) = true
}
