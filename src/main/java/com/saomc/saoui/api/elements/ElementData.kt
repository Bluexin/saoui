package com.saomc.saoui.api.elements

import com.saomc.saoui.api.screens.IIcon
import com.saomc.saoui.themes.elements.menus.MenuDefEnum

data class ElementData internal constructor(internal var category: String, internal var type: MenuDefEnum, internal var icon: IIcon, internal var name: String)
