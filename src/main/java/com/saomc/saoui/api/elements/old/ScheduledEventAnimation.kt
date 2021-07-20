package com.saomc.saoui.api.elements.old

/**
 * TODO: Document file ScheduledEventAnimation
 *
 * Created by TheCodeWarrior
 */
class ScheduledEventAnimation(time: Float, callback: Runnable) :
    Animation<ScheduledEventAnimation.PointlessAnimatableObject>(PointlessAnimatableObject,
        AnimatableProperty.get(PointlessAnimatableObject::class.java, "field")) {
    constructor(time: Float, callback: () -> Unit) : this(time, Runnable(callback))

    init {
        start = time
        duration = 0f
        completion = callback
    }

    override fun update(time: Float) {}

    object PointlessAnimatableObject {
        var field = 0
    }
}
