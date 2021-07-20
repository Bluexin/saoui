package com.saomc.saoui.api.elements.old

import com.teamwizardry.librarianlib.math.Easing

/**
 * A basic animation from [from] to [to]. Both values default to the current value of this animation's property
 */
@Deprecated("Update with LibrarianLib")
class BasicAnimation<T : Any>(target: T, property: IAnimatable<T>) : Animation<T>(target, property) {
    constructor(target: T, property: String) : this(target, AnimatableProperty.get(target.javaClass, property))
    @PublishedApi internal constructor(target: T, property: AnimatableProperty<T>) : this(target, property as IAnimatable<T>)

    /**
     * The value of the property at [start]
     */
    var from: Any = property.get(target)

    /**
     * The value of the property at [end]
     */
    var to: Any = property.get(target)

    /**
     * The easing function to use to animate between [start] and [end]
     */
    var easing: Easing = Easing.linear

    private var lerper = LerperHandler.getLerperOrError(property.type)

    override fun update(time: Float) {
        val progress = easing.ease(timeFraction(time))
        val new = lerper.lerp(from, to, progress)
        property.set(target, new)
    }

    //region - Builder methods

    @JvmName("from")
    fun fromValue(value: Any): BasicAnimation<T> {
        this.from = value
        return this
    }

    @JvmName("to")
    fun toValue(value: Any): BasicAnimation<T> {
        this.to = value
        return this
    }

    fun ease(easing: Easing): BasicAnimation<T> {
        this.easing = easing
        return this
    }
    //endregion
}
