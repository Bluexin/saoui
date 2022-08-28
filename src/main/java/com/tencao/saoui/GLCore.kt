package com.tencao.saoui

/*
object GLCore {

val glFont: FontRenderer get() = Client.minecraft.fontRenderer ?: defaultFont
private val defaultFont: FontRenderer by lazy { Client.minecraft.fontResourceMananger.defaultFontRenderer }
private val glTextureManager get() = Client.minecraft.textureManager
val tessellator: Tessellator
    get() = Tessellator.getInstance()
val bufferBuilder: BufferBuilder = tessellator.buffer

/*
val matrixStack: MatrixStack
    get() {
        val matrixStack = MatrixStack()
        val gameRenderer = Client.minecraft.gameRenderer
        matrixStack.last.matrix.mul(gameRenderer.getProjectionMatrix(gameRenderer.activeRenderInfo, Client.minecraft.renderPartialTicks, true))
        return matrixStack
    }*/

val LIGHTMAP_ENABLED = LightmapState(true)
val LIGHTMAP_DISABLED = LightmapState(false)
val DIFFUSE_LIGHTING_ENABLED = DiffuseLightingState(true)
val DIFFUSE_LIGHTING_DISABLED = DiffuseLightingState(false)
val CULL_ENABLED = CullState(true)
val CULL_DISABLED = CullState(false)
val DEPTH_ALWAYS = DepthTestState("always", 519)
val DEPTH_EQUAL = DepthTestState("==", 514)
val DEPTH_LEQUAL = DepthTestState("<=", 515)
val ZERO_ALPHA = AlphaState(0.0f)
val DEFAULT_ALPHA = AlphaState(0.003921569f)
val HALF_ALPHA = AlphaState(0.5f)
val NO_TEXTURE = TextureState()
val COLOR_DEPTH_WRITE = WriteMaskState(true, true)
val COLOR_WRITE = WriteMaskState(true, false)
val DEPTH_WRITE = WriteMaskState(false, true)

@JvmOverloads
fun color(red: Float, green: Float, blue: Float, alpha: Float = 1f) {
    RenderSystem.color4f(red, green, blue, alpha)
}

fun color(color: ColorUtil) {
    color(color.rgba)
}

fun color(rgba: Int) {
    val red = (rgba shr 24 and 0xFF).toFloat() / 0xFF
    val green = (rgba shr 16 and 0xFF).toFloat() / 0xFF
    val blue = (rgba shr 8 and 0xFF).toFloat() / 0xFF
    val alpha = (rgba and 0xFF).toFloat() / 0xFF

    color(red, green, blue, alpha)
}

fun color(rgba: Int, lightLevel: Float) {
    val red = (rgba shr 24 and 0xFF).toFloat() / 0xFF
    val green = (rgba shr 16 and 0xFF).toFloat() / 0xFF
    val blue = (rgba shr 8 and 0xFF).toFloat() / 0xFF
    val alpha = (rgba and 0xFF).toFloat() / 0xFF
    val light = max(lightLevel, 0.15f)

    color(red * light, green * light, blue * light, alpha)
}

/**
 * Returns the color values for rgba, with lighting applied
 */
fun getColor(rgba: Int, lightLevel: Float): Array<Float> {
    val light = max(lightLevel, 0.15f)
    val red = (rgba shr 24 and 0xFF).toFloat() / 0xFF * light
    val green = (rgba shr 16 and 0xFF).toFloat() / 0xFF * light
    val blue = (rgba shr 8 and 0xFF).toFloat() / 0xFF * light
    val alpha = (rgba and 0xFF).toFloat() / 0xFF * light

    return arrayOf(red, green, blue, alpha)
}

private fun glFontColor(rgba: Int): Int {
    val alpha = rgba and 0xFF
    val red = rgba shr 24 and 0xFF
    val blue = rgba shr 8 and 0xFF
    val green = rgba shr 16 and 0xFF

    return alpha shl 24 or (red shl 16) or (blue shl 8) or green
}

@JvmOverloads
fun glString(font: FontRenderer, string: String, x: Int, y: Int, argb: Int, matrixStack: MatrixStack, shadow: Boolean = false, centered: Boolean = false) {
    if (shadow) {
        font.drawStringWithShadow(
            matrixStack, string, x.toFloat(), y.toFloat() - if (centered) font.FONT_HEIGHT / 2f else 0f,
            glFontColor(argb)
        )
    } else font.drawString(
        matrixStack, string, x.toFloat(), y.toFloat() - if (centered) font.FONT_HEIGHT / 2f else 0f,
        glFontColor(argb)
    )
}

@JvmOverloads
fun glString(string: String, x: Int, y: Int, argb: Int, matrixStack: MatrixStack, shadow: Boolean = false, centered: Boolean = false) {
    glString(glFont, string, x, y, argb, matrixStack = matrixStack, shadow = shadow, centered = centered)
}

@JvmOverloads
fun glString(string: String, pos: Vec2d, argb: Int, matrixStack: MatrixStack, shadow: Boolean = false, centered: Boolean = false) {
    glString(string, pos.xi, pos.yi, argb, matrixStack = matrixStack, shadow = shadow, centered = centered)
}

@JvmOverloads
fun glString(font: FontRenderer, string: ITextComponent, x: Int, y: Int, argb: Int, matrixStack: MatrixStack, shadow: Boolean = false, centered: Boolean = false) {
    if (shadow) {
        font?.drawTextWithShadow(
            matrixStack, string, x.toFloat(), y.toFloat() - if (centered) font.FONT_HEIGHT / 2f else 0f,
            glFontColor(argb)
        )
    } else font?.drawText(
        matrixStack, string, x.toFloat(), y.toFloat() - if (centered) font.FONT_HEIGHT / 2f else 0f,
        glFontColor(argb)
    )
}

@JvmOverloads
fun glString(string: ITextComponent, x: Int, y: Int, argb: Int, matrixStack: MatrixStack, shadow: Boolean = false, centered: Boolean = false) {
    glString(glFont, string, x, y, argb, matrixStack = matrixStack, shadow = shadow, centered = centered)
}

@JvmOverloads
fun glString(string: ITextComponent, pos: Vec2d, argb: Int, matrixStack: MatrixStack, shadow: Boolean = false, centered: Boolean = false) {
    glString(string, pos.xi, pos.yi, argb, matrixStack = matrixStack, shadow = shadow, centered = centered)
}

fun setFont(mc: Minecraft, custom: Boolean) {
    if (mc.textureManager == null) return
    val font = ResourceLocation(SAOCore.MODID, "textures/${ThemeLoader.currentTheme}/ascii.png")
    val fontLocation: ResourceLocation =
        if (custom && checkTexture(font)) {
            font
        } else {
            ResourceLocation("textures/font/ascii.png")
        }
    /*
    mc.fontRenderer = FontRenderer(mc.gameSettings, fontLocation, mc.textureManager, false)
    if (mc.gameSettings.language != null) {
        mc.fontRenderer.unicodeFlag = mc.isUnicode
        mc.fontRenderer.bidiFlag = mc.languageManager.isCurrentLanguageBidirectional
    }
    (mc.resourceManager as IReloadableResourceManager).registerReloadListener(mc.fontRenderer)*/
}

@JvmOverloads
fun glStringWidth(string: String, font: FontRenderer? = glFont): Int {
    return font?.getStringWidth(string) ?: 0
}

@JvmOverloads
fun glStringHeight(font: FontRenderer? = glFont): Int {
    return font?.FONT_HEIGHT ?: 0
}

@JvmOverloads
fun glBindTexture(location: ResourceLocation, textureManager: TextureManager = glTextureManager) {
    textureManager.bindTexture(location)
}

/**
 * Checks to make sure the texture is valid, returning false means the texture is invalid.
 */
fun checkTexture(location: ResourceLocation): Boolean {
    return try {
        Client.resourceManager.getResource(location)
        true
    } catch (e: Exception) {
        false
    }
}

@JvmOverloads
fun glTexturedRectV2(pos: Vector3d, size: Vec2d, srcPos: Vec2d = Vec2d.ZERO, srcSize: Vec2d = size, textureSize: Vec2d = vec(256, 256)) {
    glTexturedRectV2(
        x = pos.x, y = pos.y, z = pos.z,
        width = size.x, height = size.y,
        srcX = srcPos.x, srcY = srcPos.y,
        srcWidth = srcSize.x, srcHeight = srcSize.y,
        textureW = textureSize.xi, textureH = textureSize.yi
    )
}

@JvmOverloads
fun glTexturedRectV2(x: Double, y: Double, z: Double = 0.0, width: Double, height: Double, srcX: Double = 0.0, srcY: Double = 0.0, srcWidth: Double = width, srcHeight: Double = height, textureW: Int = 256, textureH: Int = 256) {
    val f = 1f / textureW
    val f1 = 1f / textureH
    begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
    bufferBuilder.pos(x, y + height, z).tex((srcX.toFloat() * f), ((srcY + srcHeight).toFloat() * f1)).endVertex()
    bufferBuilder.pos(x + width, y + height, z).tex(((srcX + srcWidth).toFloat() * f), ((srcY + srcHeight).toFloat() * f1)).endVertex()
    bufferBuilder.pos(x + width, y, z).tex(((srcX + srcWidth).toFloat() * f), (srcY.toFloat() * f1)).endVertex()
    bufferBuilder.pos(x, y, z).tex((srcX.toFloat() * f), (srcY.toFloat() * f1)).endVertex()
    draw()
}

fun addVertex(x: Double, y: Double, z: Double) {
    bufferBuilder.pos(x, y, z).endVertex()
}

fun addVertex(x: Double, y: Double, z: Double, srcX: Double, srcY: Double) {
    bufferBuilder.pos(x, y, z).tex(srcX.toFloat(), srcY.toFloat()).endVertex()
}

fun addVertex(builder: IVertexBuilder, x: Float, y: Float, z: Float, srcX: Float, srcY: Float, rgba: Int, lightLevel: Float) {
    builder.pos(x.toDouble(), y.toDouble(), z.toDouble()).color(rgba).tex(srcX, srcY).lightmap(0xF000F0).endVertex()
}

fun addVertex(builder: IVertexBuilder, matrixStack: Matrix4f, x: Float, y: Float, z: Float, srcX: Float, srcY: Float, rgba: Int, lightLevel: Float, normal: Vector3f) {
    builder.pos(matrixStack, x, y, z).color(rgba).tex(srcX, srcY).normal(normal.x, normal.y, normal.z).endVertex()
}

fun addVertex(x: Double, y: Double, z: Double, srcX: Double, srcY: Double, red: Float, green: Float, blue: Float, alpha: Float) {
    bufferBuilder.pos(x, y, z).tex(srcX.toFloat(), srcY.toFloat()).color(red, green, blue, alpha).endVertex()
}

fun addVertex(x: Double, y: Double, z: Double, red: Float, green: Float, blue: Float, alpha: Float) {
    bufferBuilder.pos(x, y, z).color(red, green, blue, alpha).endVertex()
}

fun addVertex(x: Float, y: Float, z: Float, srcX: Double, srcY: Double, red: Float, green: Float, blue: Float, alpha: Float) {
    bufferBuilder.normal(x, y, z).tex(srcX.toFloat(), srcY.toFloat()).color(red, green, blue, alpha).endVertex()
}

//

fun addVertex(matrixStack: MatrixStack, x: Float, y: Float, z: Float) {
    bufferBuilder.pos(matrixStack.last.matrix, x, y, z).endVertex()
}

fun addVertex(matrixStack: MatrixStack, x: Float, y: Float, z: Float, normalX: Float, normalY: Float, normalZ: Float) {
    bufferBuilder.pos(matrixStack.last.matrix, x, y, z).normal(normalX, normalY, normalZ).endVertex()
}

fun addVertex(matrixStack: MatrixStack, x: Float, y: Float, z: Float, normalX: Float, normalY: Float, normalZ: Float, srcX: Double, srcY: Double) {
    bufferBuilder.pos(matrixStack.last.matrix, x, y, z).normal(normalX, normalY, normalZ).tex(srcX.toFloat(), srcY.toFloat()).endVertex()
}

fun addVertex(matrixStack: MatrixStack, x: Float, y: Float, z: Float, srcX: Double, srcY: Double) {
    bufferBuilder.pos(matrixStack.last.matrix, x, y, z).tex(srcX.toFloat(), srcY.toFloat()).endVertex()
}

fun addVertex(matrixStack: MatrixStack, x: Float, y: Float, z: Float, srcX: Double, srcY: Double, red: Float, green: Float, blue: Float, alpha: Float) {
    bufferBuilder.pos(matrixStack.last.matrix, x, y, z).tex(srcX.toFloat(), srcY.toFloat()).color(red, green, blue, alpha).endVertex()
}

fun addVertex(matrixStack: MatrixStack, x: Float, y: Float, z: Float, red: Float, green: Float, blue: Float, alpha: Float) {
    bufferBuilder.pos(matrixStack.last.matrix, x, y, z).color(red, green, blue, alpha).endVertex()
}

fun addVertex(matrixStack: MatrixStack, x: Float, y: Float, z: Float, normalX: Float, normalY: Float, normalZ: Float, srcX: Double, srcY: Double, red: Float, green: Float, blue: Float, alpha: Float) {
    bufferBuilder.pos(matrixStack.last.matrix, x, y, z).normal(normalX, normalY, normalZ).tex(srcX.toFloat(), srcY.toFloat()).color(red, green, blue, alpha).endVertex()
}

@JvmOverloads
fun begin(glMode: Int = GL11.GL_QUADS, format: VertexFormat = DefaultVertexFormats.POSITION_TEX_COLOR) {
    bufferBuilder.begin(glMode, format)
}

fun draw() {
    tessellator.draw()
}

fun glRect(x: Int, y: Int, width: Int, height: Int) {
    begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION)
    bufferBuilder.pos(x.toDouble(), (y + height).toDouble(), 0.0)
    bufferBuilder.pos((x + width).toDouble(), (y + height).toDouble(), 0.0)
    bufferBuilder.pos((x + width).toDouble(), y.toDouble(), 0.0)
    bufferBuilder.pos(x.toDouble(), y.toDouble(), 0.0)
    draw()
}

/**
 * Draws a rectangle with a vertical gradient between the specified colors (ARGB format). Args : x1, y1, x2, y2,
 * topColor, bottomColor
 */
fun drawGradientRect(left: Double, top: Double, right: Double, bottom: Double, zLevel: Double, startColor: Int, endColor: Int = startColor) {
    val f = (startColor shr 24 and 255).toFloat() / 255.0f
    val f1 = (startColor shr 16 and 255).toFloat() / 255.0f
    val f2 = (startColor shr 8 and 255).toFloat() / 255.0f
    val f3 = (startColor and 255).toFloat() / 255.0f
    val f4 = (endColor shr 24 and 255).toFloat() / 255.0f
    val f5 = (endColor shr 16 and 255).toFloat() / 255.0f
    val f6 = (endColor shr 8 and 255).toFloat() / 255.0f
    val f7 = (endColor and 255).toFloat() / 255.0f
    glTexture2D(false)
    glBlend(true)
    glAlphaTest(false)
    tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO)
    shadeModel(7425)
    begin(7, DefaultVertexFormats.POSITION_COLOR)
    bufferBuilder.pos(right, top, zLevel).color(f1, f2, f3, f).endVertex()
    bufferBuilder.pos(left, top, zLevel).color(f1, f2, f3, f).endVertex()
    bufferBuilder.pos(left, bottom, zLevel).color(f5, f6, f7, f4).endVertex()
    bufferBuilder.pos(right, bottom, zLevel).color(f5, f6, f7, f4).endVertex()
    draw()
    shadeModel(7424)
    glBlend(false)
    glAlphaTest(true)
    glTexture2D(true)
}

fun glAlphaTest(flag: Boolean) {
    if (flag)
        RenderSystem.enableAlphaTest()
    else
        RenderSystem.disableAlphaTest()
}

fun alphaFunc(src: Int, dst: Int) {
    RenderSystem.alphaFunc(src, dst.toFloat())
}

fun shadeModel(mode: Int) {
    RenderSystem.shadeModel(mode)
}

fun glBlend(flag: Boolean) {
    if (flag)
        RenderSystem.enableBlend()
    else
        RenderSystem.disableBlend()
}

fun blendFunc(src: GlStateManager.SourceFactor, dst: GlStateManager.DestFactor) {
    RenderSystem.blendFunc(src, dst)
}

fun blendFunc(src: Int, dst: Int) {
    RenderSystem.blendFunc(src, dst)
}

fun tryBlendFuncSeparate(a: GlStateManager.SourceFactor, b: GlStateManager.DestFactor, c: GlStateManager.SourceFactor, d: GlStateManager.DestFactor) {
    RenderSystem.blendFuncSeparate(a, b, c, d)
}

fun tryBlendFuncSeparate(a: Int, b: Int, c: Int, d: Int) {
    RenderSystem.blendFuncSeparate(a, b, c, d)
}

fun depthMask(flag: Boolean) {
    RenderSystem.depthMask(flag)
}

fun depth(flag: Boolean) {
    if (flag)
        RenderSystem.enableDepthTest()
    else
        RenderSystem.disableDepthTest()
}

fun glDepthFunc(flag: Int) {
    RenderSystem.depthFunc(flag)
}

fun glRescaleNormal(flag: Boolean) {
    if (flag)
        RenderSystem.enableRescaleNormal()
    else
        RenderSystem.disableRescaleNormal()
}

fun glTexture2D(flag: Boolean) {
    if (flag)
        GL11.glEnable(GL11.GL_TEXTURE_2D)
    else
        GL11.glDisable(GL11.GL_TEXTURE_2D)
}

fun glCullFace(flag: Boolean) {
    if (flag)
        RenderSystem.enableCull()
    else
        RenderSystem.disableCull()
}

fun glNormal3f(x: Float, y: Float, z: Float) {
    RenderSystem.normal3f(x, y, z)
}

fun glRotate(angle: Quaternion) {
    RenderSystem.rotatef(angle.w, angle.x, angle.y, angle.z)
}

fun glRotate(angle: Float, x: Float, y: Float, z: Float) {
    RenderSystem.rotatef(angle, x, y, z)
}

fun lighting(flag: Boolean) {
    if (flag)
        RenderSystem.enableLighting()
    else
        RenderSystem.disableLighting()
}

fun pushMatrix() {
    RenderSystem.pushMatrix()
}

fun popMatrix() {
    RenderSystem.popMatrix()
}

/**
 * returns an AABB with corners x1, y1, z1 and x2, y2, z2
 */
fun fromBounds(x1: Double, y1: Double, z1: Double, x2: Double, y2: Double, z2: Double): AxisAlignedBB {
    val d0 = min(x1, x2)
    val d1 = min(y1, y2)
    val d2 = min(z1, z2)
    val d3 = max(x1, x2)
    val d4 = max(y1, y2)
    val d5 = max(z1, z2)
    return AxisAlignedBB(d0, d1, d2, d3, d4, d5)
}

fun translate(x: Double, y: Double, z: Double) {
    RenderSystem.translated(x, y, z)
}

fun translate(x: Float, y: Float, z: Float) {
    RenderSystem.translatef(x, y, z)
}

fun scale(x: Double, y: Double, z: Double) {
    RenderSystem.scaled(x, y, z)
}

fun scale(x: Float, y: Float, z: Float) {
    RenderSystem.scalef(x, y, z)
}
}

fun IVertexBuilder.color(rgba: Int, lightLevel: Float): IVertexBuilder {
val light = max(lightLevel, 0.15f)
val red = (rgba shr 24 and 0xFF).toFloat() / 0xFF * light
val green = (rgba shr 16 and 0xFF).toFloat() / 0xFF * light
val blue = (rgba shr 8 and 0xFF).toFloat() / 0xFF * light
val alpha = (rgba and 0xFF).toFloat() / 0xFF * light

return this.color(
    (red * 255.0f).toInt(),
    (green * 255.0f).toInt(),
    (blue * 255.0f).toInt(),
    (alpha * 255.0f).toInt()
)
}

fun IVertexBuilder.color(rgba: Int): IVertexBuilder {
val red = (rgba shr 24 and 0xFF).toFloat() / 0xFF
val green = (rgba shr 16 and 0xFF).toFloat() / 0xFF
val blue = (rgba shr 8 and 0xFF).toFloat() / 0xFF
val alpha = (rgba and 0xFF).toFloat() / 0xFF

return this.color(
    (red * 255.0f).toInt(),
    (green * 255.0f).toInt(),
    (blue * 255.0f).toInt(),
    (alpha * 255.0f).toInt()
)
}
*/
