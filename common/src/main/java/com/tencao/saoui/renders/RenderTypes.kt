package com.tencao.saoui.renders

import com.google.common.collect.ImmutableList
import com.mojang.blaze3d.vertex.VertexFormat
import com.tencao.saoui.Constants.MODID
import com.tencao.saoui.resources.StringNames
import com.tencao.saoui.util.render.DefaultVertexFormats
import net.minecraft.client.renderer.RenderType
import org.lwjgl.opengl.GL11

object RenderTypes : RenderType("", VertexFormat(ImmutableList.of()), 0, 0, false, false, Runnable { }, Runnable { }) {

    @JvmStatic
    internal val crystalRender = create(
        "${MODID}:crystal_render",
        DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL,
        GL11.GL_QUADS,
        256,
        true,
        true,
        CompositeState.builder()
            .setDepthTestState(NO_DEPTH_TEST)
            .setTextureState(TextureStateShard(StringNames.entities, false, true))
            .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
            .createCompositeState(false)
    )
}
