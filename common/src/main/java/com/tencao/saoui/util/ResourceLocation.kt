package com.tencao.saoui.util

import net.minecraft.resources.ResourceLocation


fun ResourceLocation.append(suffix: String) = ResourceLocation(namespace, "$path$suffix")