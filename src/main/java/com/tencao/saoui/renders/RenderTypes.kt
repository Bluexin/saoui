package com.tencao.saoui.renders

import com.google.common.collect.ImmutableList
import com.tencao.saomclib.GLCore
import com.tencao.saoui.SAOCore
import com.tencao.saoui.resources.StringNames
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.client.renderer.vertex.VertexFormat
import org.lwjgl.opengl.GL11

@Suppress("INACCESSIBLE_TYPE")
object RenderTypes : RenderType("", VertexFormat(ImmutableList.of()), 0, 0, false, false, Runnable { }, Runnable { }) {

    @JvmStatic
    internal val crystalRender = makeType(
        "${SAOCore.MODID}:crystal_render",
        DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL,
        GL11.GL_QUADS,
        256,
        true,
        true,
        State.getBuilder()
            .depthTest(GLCore.DEPTH_ALWAYS)
            .texture(TextureState(StringNames.entities, false, true))
            .transparency(TRANSLUCENT_TRANSPARENCY)
            .build(false)
    )
}
