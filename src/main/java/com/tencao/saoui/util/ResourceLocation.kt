package com.tencao.saoui.util

import net.minecraft.util.ResourceLocation

fun ResourceLocation.append(suffix: String) = ResourceLocation(resourceDomain, "$resourcePath$suffix")
