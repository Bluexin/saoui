package com.saomc.saoui.screens.util

import com.saomc.saoui.api.elements.neo.NeoIconElement

object Notification {

    // ****** A list of notifications queued ******
    val data = linkedSetOf<NotificationData>()

    fun create(title: String, text: List<String>, buttons: Set<NeoIconElement>){
        data.add(NotificationData(title, text, buttons))
    }
}

data class NotificationData(var title: String, var text: List<String>, private val buttons: Set<NeoIconElement>)
