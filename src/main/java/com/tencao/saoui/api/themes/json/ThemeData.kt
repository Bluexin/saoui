package com.tencao.saoui.api.themes.json

import com.google.gson.annotations.SerializedName

data class ThemeData(
    @SerializedName("theme name")
    val name: String,
    @SerializedName("author")
    val author: String,
    val parts: Map<ElementList, GroupPart>
) {
    companion object {
    }
}
