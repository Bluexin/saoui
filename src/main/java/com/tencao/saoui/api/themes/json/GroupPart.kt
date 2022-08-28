package com.tencao.saoui.api.themes.json

import com.google.gson.annotations.SerializedName
import com.mojang.blaze3d.matrix.MatrixStack
import net.minecraft.util.ResourceLocation

data class GroupPart(
    /**
     * If this element is a subgroup, redirect to the parent
     */
    val parent: GroupPart?,
    /**
     * The x axis to pass onto any render, this will also pass the parent's x as well.
     */
    @SerializedName("x")
    val x: Double,
    /**
     * The y axis to pass onto any render, this will also pass the parent's y as well.
     */
    @SerializedName("y")
    val y: Double,
    /**
     * The texture to pass onto any render, this can be overridden
     */
    @SerializedName("texture")
    val texture: ResourceLocation,
    /**
     * Any subgroups belonging to this group, subgroups will render before parts
     */
    val subGroup: List<GroupPart>,
    /**
     * Individual parts to render
     */
    val renderParts: List<IRenderPart>,
    val conditions: List<Function<*>>
) : IRenderPart {

    fun getXPos(): Double = x + (parent?.getXPos() ?: 0.0)
    fun getYPos(): Double = y + (parent?.getYPos() ?: 0.0)

    override fun render(stack: MatrixStack) {
        subGroup.forEach { it.render(stack) }
        renderParts.forEach { it.render(stack) }
    }
}
