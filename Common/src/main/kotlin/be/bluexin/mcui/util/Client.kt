package be.bluexin.mcui.util

import net.minecraft.client.Minecraft
import net.minecraft.server.packs.resources.ResourceManager

object Client {
    val mc: Minecraft get() = Minecraft.getInstance()
    val resourceManager: ResourceManager get() = mc.resourceManager
}