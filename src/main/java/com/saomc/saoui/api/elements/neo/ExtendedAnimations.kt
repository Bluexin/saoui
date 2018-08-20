package com.saomc.saoui.api.elements.neo

import com.teamwizardry.librarianlib.features.animator.*
import com.teamwizardry.librarianlib.features.animator.animations.BasicAnimation
import com.teamwizardry.librarianlib.features.animator.animations.ScheduledEventAnimation

/**
 * Part of saoui by Bluexin, released under GNU GPLv3.
 *
 * @author Bluexin
 */
open class IndexedScheduledCounter(val delay: Float, maxIdx: Int = 0, private val callBack: (Int) -> Unit) : Animation<ScheduledEventAnimation.PointlessAnimatableObject>(ScheduledEventAnimation.PointlessAnimatableObject,
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

fun <T : Any> basicAnimation(target: T, property: AnimatableProperty<T>, init: (BasicAnimation<T>.() -> Unit)? = null): BasicAnimation<T> {
    val anim = BasicAnimation(target, property)
    if (init != null) anim.init()
    return anim
}

fun <T : Any> basicAnimation(target: T, property: String, init: (BasicAnimation<T>.() -> Unit)? = null) = basicAnimation(target, AnimatableProperty.get(target.javaClass, property), init)

operator fun Animator.plusAssign(animation: Animation<*>) = this.add(animation)
