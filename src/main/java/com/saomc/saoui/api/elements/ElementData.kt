package com.saomc.saoui.api.elements

import com.saomc.saoui.api.screens.IIcon

data class ElementData internal constructor(internal var category: String, internal var parentCategory: String?, internal var type: MenuDefEnum, internal var icon: IIcon, internal var name: String, internal var displayName: String, internal var isCategory: Boolean)
