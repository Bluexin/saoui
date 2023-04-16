package com.tencao.saoui.themes.util.typeadapters

@Suppress("unused")
enum class JelType(
    val typeName: String,
    val expressionAdapter: BasicExpressionAdapter<*>,
) {
    STRING("String", StringExpressionAdapter),
    DOUBLE("Double", DoubleExpressionAdapter),
    INT("Int", IntExpressionAdapter),
    BOOLEAN("Boolean", BooleanExpressionAdapter),
    UNIT("Unit", UnitExpressionAdapter),
    ERROR("Error", UnitExpressionAdapter),
}