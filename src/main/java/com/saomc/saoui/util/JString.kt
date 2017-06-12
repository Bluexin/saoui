package com.saomc.saoui.util

class JString : Strings {

    private val string: String

    constructor(`object`: Any) {
        string = `object` as? String ?: `object`.toString()
    }

    constructor() {
        string = ""
    }

    override fun toString(): String {
        return string
    }

}
