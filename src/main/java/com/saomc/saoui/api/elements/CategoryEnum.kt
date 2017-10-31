package com.saomc.saoui.api.elements

import com.saomc.saoui.SAOCore
import java.lang.IllegalArgumentException

enum class CategoryEnum constructor(var parent: CategoryEnum?) {

    MAIN(null);

    fun getPath(): String {
        return if (parent != null)
            parent!!.getPath() + "/" + this.name
        else
            this.name
    }

    companion object {
        fun getOrAdd(name: String, parent: CategoryEnum?): CategoryEnum {
            return try {
                CategoryEnum.valueOf(name.toUpperCase())
            } catch (e: IllegalArgumentException) {
                try {
                    CategoryHelper.addCategory(name.toUpperCase(), parent)!!
                } catch (e: NullPointerException) {
                    SAOCore.LOGGER.fatal("Failed to make category for $name with category: $name and parent category: $parent")
                    return MAIN
                }
            }
        }
    }
}
