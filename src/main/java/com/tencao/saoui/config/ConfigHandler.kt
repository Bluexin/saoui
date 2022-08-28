/*
 * Copyright (C) 2016-2019 Arnaud 'Bluexin' Solé
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.tencao.saoui.config

import com.tencao.saoui.SAOCore
import net.minecraftforge.common.ForgeConfigSpec
import net.minecraftforge.fml.ModContainer
import net.minecraftforge.fml.config.ModConfig

/**
 * Part of SAOUI

 * @author Bluexin
 */

class ConfigHandler(container: ModContainer) : ModConfig(Type.CLIENT, config, container, "${SAOCore.NAME}/Main.cfg") {

    var DEBUG = false

    fun saveAllOptions() {
        config.save()
    }

    companion object {
        lateinit var debugFakePT: ForgeConfigSpec.ConfigValue<Int>
        lateinit var lastThemeUsed: ForgeConfigSpec.ConfigValue<String>

        val config: ForgeConfigSpec by lazy {
            val builder = ForgeConfigSpec.Builder()
            builder.push("options")
            builder.comment("***ONLY MODIFY THESE SETTINGS INGAME***")
            OptionCategory.tlOptionCategory.forEach {
                buildOptionCategory(builder, it)
            }
            builder.push("debug")
            debugFakePT = builder.comment("Debug Party Members", "Amount of fake party members, 0 to disable.").defineInRange("Amount", 0, 0, 6)
            lastThemeUsed = builder.comment("Last Theme Used", "The last used theme loaded. If invalid, defaults to sao's theme").define("Theme", "sao")
            builder.pop()
            builder.build()
        }

        fun buildOptionCategory(builder: ForgeConfigSpec.Builder, category: OptionCategory) {
            builder.push(category.name)
            category.getOptions().forEach {
                buildOption(builder, it)
            }
            category.getSubCategories().forEach {
                buildOptionCategory(builder, it)
            }
            builder.pop()
        }

        fun buildOption(builder: ForgeConfigSpec.Builder, option: OptionCore) {
            // option.value = builder.comment(option.description.toString()).define(option.name, option.defaultValue)
            option.value = builder.define(option.name, option.defaultValue)
            OptionCore.values()
        }
    }
}
