package com.saomc.saoui.api.elements.neo

import com.teamwizardry.librarianlib.features.animator.AnimatableProperty
import com.teamwizardry.librarianlib.features.animator.Animation
import com.teamwizardry.librarianlib.features.animator.Easing
import com.teamwizardry.librarianlib.features.animator.LerperHandler
import com.teamwizardry.librarianlib.features.animator.animations.ScheduledEventAnimation

/**
 * Part of saoui by Bluexin, released under GNU GPLv3.
 *
 * @author Bluexin
 */
abstract class CancelableAnimation<T : Any>(target: T, property: AnimatableProperty<T>) : Animation<T>(target, property) {
    fun cancel() {
        duration = 0f
    }
}

open class IndexedScheduledCounter(val delay: Float, maxIdx: Int = 0, private val callBack: (Int) -> Unit) : CancelableAnimation<ScheduledEventAnimation.PointlessAnimatableObject>(ScheduledEventAnimation.PointlessAnimatableObject,
        AnimatableProperty.get(ScheduledEventAnimation.PointlessAnimatableObject::class.java, "field")) {

    init {
        duration = delay * maxIdx
    }

    var maxIdx = maxIdx
        set(value) {
            field = value
            duration = delay * maxIdx
        }

    private val lerper = LerperHandler.getLerperOrError(Int::class.javaPrimitiveType!!)

    private var currentIdx = -1

    override fun update(time: Float) {
        val progress = Easing.linear(timeFraction(time))
        val new = lerper.lerp(0, maxIdx, progress)
        if (new != currentIdx) {
            currentIdx = new
            callBack(currentIdx)
        }
    }
}
