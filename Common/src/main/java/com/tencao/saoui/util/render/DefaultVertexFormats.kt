package com.tencao.saoui.util.render

import com.google.common.collect.ImmutableList
import com.mojang.blaze3d.vertex.VertexFormat
import com.mojang.blaze3d.vertex.VertexFormatElement

/**
 * Copy from forge, net.minecraft.client.renderer.vertex.DefaultVertexFormats
 */
object DefaultVertexFormats {

    val ELEMENT_POSITION: VertexFormatElement =
        VertexFormatElement(
            0,
            VertexFormatElement.Type.FLOAT,
            VertexFormatElement.Usage.POSITION,
            3
        )
    val ELEMENT_COLOR: VertexFormatElement =
        VertexFormatElement(
            0,
            VertexFormatElement.Type.UBYTE,
            VertexFormatElement.Usage.COLOR,
            4
        )
    val ELEMENT_UV0: VertexFormatElement =
        VertexFormatElement(
            0,
            VertexFormatElement.Type.FLOAT,
            VertexFormatElement.Usage.UV,
            2
        )
    val ELEMENT_UV1: VertexFormatElement =
        VertexFormatElement(
            1,
            VertexFormatElement.Type.SHORT,
            VertexFormatElement.Usage.UV,
            2
        )
    val ELEMENT_UV2: VertexFormatElement =
        VertexFormatElement(
            2,
            VertexFormatElement.Type.SHORT,
            VertexFormatElement.Usage.UV,
            2
        )
    val ELEMENT_NORMAL: VertexFormatElement =
        VertexFormatElement(
            0,
            VertexFormatElement.Type.BYTE,
            VertexFormatElement.Usage.NORMAL,
            3
        )
    val ELEMENT_PADDING: VertexFormatElement =
        VertexFormatElement(
            0,
            VertexFormatElement.Type.BYTE,
            VertexFormatElement.Usage.PADDING,
            1
        )
    val BLOCK: VertexFormat = VertexFormat(
        ImmutableList.builder<VertexFormatElement>()
            .add(ELEMENT_POSITION)
            .add(ELEMENT_COLOR)
            .add(ELEMENT_UV0)
            .add(ELEMENT_UV2)
            .add(ELEMENT_NORMAL)
            .add(ELEMENT_PADDING).build())
    val NEW_ENTITY: VertexFormat =
        VertexFormat(
            ImmutableList.builder<VertexFormatElement>().add(ELEMENT_POSITION)
                .add(ELEMENT_COLOR).add(ELEMENT_UV0).add(ELEMENT_UV1).add(ELEMENT_UV2).add(ELEMENT_NORMAL)
                .add(ELEMENT_PADDING).build()
        )

    @Deprecated("")
    val PARTICLE: VertexFormat = VertexFormat(
        ImmutableList.builder<VertexFormatElement>().add(ELEMENT_POSITION)
            .add(ELEMENT_UV0).add(ELEMENT_COLOR).add(ELEMENT_UV2).build()
    )
    val POSITION: VertexFormat = VertexFormat(
        ImmutableList.builder<VertexFormatElement>().add(ELEMENT_POSITION).build()
    )
    val POSITION_COLOR: VertexFormat =
        VertexFormat(
            ImmutableList.builder<VertexFormatElement>().add(ELEMENT_POSITION)
                .add(ELEMENT_COLOR).build()
        )
    val POSITION_COLOR_LIGHTMAP: VertexFormat =
        VertexFormat(
            ImmutableList.builder<VertexFormatElement>().add(ELEMENT_POSITION)
                .add(ELEMENT_COLOR).add(ELEMENT_UV2).build()
        )
    val POSITION_TEX: VertexFormat =
        VertexFormat(
            ImmutableList.builder<VertexFormatElement>().add(ELEMENT_POSITION)
                .add(ELEMENT_UV0).build()
        )
    val POSITION_COLOR_TEX: VertexFormat =
        VertexFormat(
            ImmutableList.builder<VertexFormatElement>().add(ELEMENT_POSITION)
                .add(ELEMENT_COLOR).add(ELEMENT_UV0).build()
        )

    @Deprecated("")
    val POSITION_TEX_COLOR: VertexFormat =
        VertexFormat(
            ImmutableList.builder<VertexFormatElement>().add(ELEMENT_POSITION)
                .add(ELEMENT_UV0).add(ELEMENT_COLOR).build()
        )
    val POSITION_COLOR_TEX_LIGHTMAP: VertexFormat =
        VertexFormat(
            ImmutableList.builder<VertexFormatElement>().add(ELEMENT_POSITION)
                .add(ELEMENT_COLOR).add(ELEMENT_UV0).add(ELEMENT_UV2).build()
        )

    @Deprecated("")
    val POSITION_TEX_LIGHTMAP_COLOR: VertexFormat =
        VertexFormat(
            ImmutableList.builder<VertexFormatElement>().add(ELEMENT_POSITION)
                .add(ELEMENT_UV0).add(ELEMENT_UV2).add(ELEMENT_COLOR).build()
        )

    @Deprecated("")
    val POSITION_TEX_COLOR_NORMAL: VertexFormat =
        VertexFormat(
            ImmutableList.builder<VertexFormatElement>().add(ELEMENT_POSITION)
                .add(ELEMENT_UV0).add(ELEMENT_COLOR).add(ELEMENT_NORMAL).add(ELEMENT_PADDING).build()
        )


}