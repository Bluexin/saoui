package com.tencao.saoui.api.elements.animator.internal

import com.tencao.saoui.util.math.vec
import org.joml.Vector2d
import org.joml.Vector3d

/**
 * This is a copy from the library LibrarianLib
 * This code is covered under GNU Lesser General Public License v3.0
 */


/**
 * TODO: Document file VecMutators
 *
 * Created by TheCodeWarrior
 */
@Deprecated("")
object VecMutators {
    fun doubleFrom(any: Any): Double {
        any as? Number ?: throw IllegalArgumentException("Cannot cast `${any.javaClass.canonicalName}` to a Number")
        return any.toDouble()
    }

    init {

        //region Vector2d
        ImmutableFieldMutatorHandler.registerProvider(
            Vector2d::class.java,
            object : ImmutableFieldMutatorProvider<Vector2d> {
                override fun getMutatorForImmutableField(name: String): ImmutableFieldMutator<Vector2d>? {
                    when (name) {
                        "x" -> {
                            return object : ImmutableFieldMutator<Vector2d> {
                                override fun mutate(target: Vector2d, value: Any?): Vector2d {
                                    value ?: throw IllegalArgumentException("Cannot set Vector2d.x to null")
                                    return target.withX(doubleFrom(value))
                                }
                            }
                        }
                        "y" -> {
                            return object : ImmutableFieldMutator<Vector2d> {
                                override fun mutate(target: Vector2d, value: Any?): Vector2d {
                                    value ?: throw IllegalArgumentException("Cannot set Vector2d.y to null")
                                    return target.withY(doubleFrom(value))
                                }
                            }
                        }
                    }
                    return null
                }
            }
        )
        //endregion

        //region Vector3d
        ImmutableFieldMutatorHandler.registerProvider(
            Vector3d::class.java,
            object : ImmutableFieldMutatorProvider<Vector3d> {
                override fun getMutatorForImmutableField(name: String): ImmutableFieldMutator<Vector3d>? {
                    when (name) {
                        "x" -> {
                            return object : ImmutableFieldMutator<Vector3d> {
                                override fun mutate(target: Vector3d, value: Any?): Vector3d {
                                    value ?: throw IllegalArgumentException("Cannot set Vector3d.x to null")
                                    return target.withX(doubleFrom(value))
                                }
                            }
                        }
                        "y" -> {
                            return object : ImmutableFieldMutator<Vector3d> {
                                override fun mutate(target: Vector3d, value: Any?): Vector3d {
                                    value ?: throw IllegalArgumentException("Cannot set Vector3d.y to null")
                                    return target.withX(doubleFrom(value))
                                }
                            }
                        }
                        "z" -> {
                            return object : ImmutableFieldMutator<Vector3d> {
                                override fun mutate(target: Vector3d, value: Any?): Vector3d {
                                    value ?: throw IllegalArgumentException("Cannot set Vector3d.z to null")
                                    return target.withZ(doubleFrom(value))
                                }
                            }
                        }
                    }
                    return null
                }
            }
        )
        //endregion
    }
}

fun Vector2d.withX(other: Double) = vec(other, y)
fun Vector2d.withY(other: Double) = vec(x, other)

operator fun Vector3d.times(other: Vector3d): Vector3d = vec(x * other.x, y * other.y, z * other.z)
operator fun Vector3d.times(other: Double): Vector3d = this.mul(other)
operator fun Vector3d.times(other: Float): Vector3d = this * other.toDouble()
operator fun Vector3d.times(other: Int): Vector3d = this * other.toDouble()

operator fun Vector3d.div(other: Vector3d) = vec(x / other.x, y / other.y, z / other.z)
operator fun Vector3d.div(other: Double): Vector3d = this * (1 / other)
operator fun Vector3d.div(other: Float): Vector3d = this / other.toDouble()
operator fun Vector3d.div(other: Int): Vector3d = this / other.toDouble()

operator fun Vector3d.plus(other: Vector3d): Vector3d = add(other)
operator fun Vector3d.minus(other: Vector3d): Vector3d = this.sub(other)
operator fun Vector3d.unaryMinus(): Vector3d = this * -1.0

fun Vector3d.withX(other: Double) = vec(other, y, z)
fun Vector3d.withY(other: Double) = vec(x, other, z)
fun Vector3d.withZ(other: Double) = vec(x, y, other)

fun Vector3d.withX(other: Float) = withX(other.toDouble())
fun Vector3d.withY(other: Float) = withY(other.toDouble())
fun Vector3d.withZ(other: Float) = withZ(other.toDouble())

fun Vector3d.withX(other: Int) = withX(other.toDouble())
fun Vector3d.withY(other: Int) = withY(other.toDouble())
fun Vector3d.withZ(other: Int) = withZ(other.toDouble())


operator fun Vector3d.component1() = x
operator fun Vector3d.component2() = y
operator fun Vector3d.component3() = z
