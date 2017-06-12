package com.saomc.saoui.util

import java.util.stream.Stream

object J8String {

    fun join(s0: String, vararg s1: String): String {
        val builder = StringBuilder(s0)
        Stream.of(*s1).forEach { s -> builder.append(s).append(s0) }

        return builder.toString().trim { it <= ' ' }
    }

}
