package com.saomc.saoui.screens.util

import com.saomc.saoui.api.elements.IconElement

object Notification {

    // ****** A list of notifications queued ******
    val data = linkedSetOf<NotificationData>()

    fun create(title: String, text: List<String>, buttons: Set<IconElement>){
        data.add(NotificationData(title, text, buttons))
    }
}

data class NotificationData(var title: String, var text: List<String>, private val buttons: Set<IconElement>)
