package com.saomc.saoui.api.elements

enum class CategoryEnum constructor(var parent: CategoryEnum?) {

    MAIN(null);

    fun getPath(): String {
        if (parent != null)
            return parent!!.getPath() + "/" + this.name
        else
            return this.name
    }
}
