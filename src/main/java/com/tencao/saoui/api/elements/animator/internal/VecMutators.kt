package com.tencao.saoui.api.elements.animator.internal

/**
 * This is a copy from the library LibrarianLib
 * This code is covered under GNU Lesser General Public License v3.0
 */

import com.tencao.saomclib.utils.math.Vec2d
import com.tencao.saomclib.utils.math.vec
import net.minecraft.util.math.vector.Vector3d

/**
 * TODO: Document file VecMutators
 *
 * Created by TheCodeWarrior
 */
object VecMutators {
    fun doubleFrom(any: Any): Double {
        any as? Number ?: throw IllegalArgumentException("Cannot cast `${any.javaClass.canonicalName}` to a Number")
        return any.toDouble()
    }

    init {

        //region Vec2d
        ImmutableFieldMutatorHandler.registerProvider(
            Vec2d::class.java,
            object : ImmutableFieldMutatorProvider<Vec2d> {
                override fun getMutatorForImmutableField(name: String): ImmutableFieldMutator<Vec2d>? {
                    when (name) {
                        "x" -> {
                            return object : ImmutableFieldMutator<Vec2d> {
                                override fun mutate(target: Vec2d, value: Any?): Vec2d {
                                    value ?: throw IllegalArgumentException("Cannot set Vec2d.x to null")
                                    return target.withX(doubleFrom(value))
                                }
                            }
                        }
                        "y" -> {
                            return object : ImmutableFieldMutator<Vec2d> {
                                override fun mutate(target: Vec2d, value: Any?): Vec2d {
                                    value ?: throw IllegalArgumentException("Cannot set Vec2d.y to null")
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

        //region Vec3d
        ImmutableFieldMutatorHandler.registerProvider(
            Vector3d::class.java,
            object : ImmutableFieldMutatorProvider<Vector3d> {
                override fun getMutatorForImmutableField(name: String): ImmutableFieldMutator<Vector3d>? {
                    when (name) {
                        "x" -> {
                            return object : ImmutableFieldMutator<Vector3d> {
                                override fun mutate(target: Vector3d, value: Any?): Vector3d {
                                    value ?: throw IllegalArgumentException("Cannot set Vec3d.x to null")
                                    return target.withX(doubleFrom(value))
                                }
                            }
                        }
                        "y" -> {
                            return object : ImmutableFieldMutator<Vector3d> {
                                override fun mutate(target: Vector3d, value: Any?): Vector3d {
                                    value ?: throw IllegalArgumentException("Cannot set Vec3d.y to null")
                                    return target.withX(doubleFrom(value))
                                }
                            }
                        }
                        "z" -> {
                            return object : ImmutableFieldMutator<Vector3d> {
                                override fun mutate(target: Vector3d, value: Any?): Vector3d {
                                    value ?: throw IllegalArgumentException("Cannot set Vec3d.z to null")
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

fun Vec2d.withX(other: Double) = vec(other, y)
fun Vec2d.withY(other: Double) = vec(x, other)

operator fun Vector3d.times(other: Vector3d): Vector3d = vec(x * other.x, y * other.y, z * other.z)
operator fun Vector3d.times(other: Double): Vector3d = scale(other)
operator fun Vector3d.times(other: Float): Vector3d = this * other.toDouble()
operator fun Vector3d.times(other: Int): Vector3d = this * other.toDouble()

operator fun Vector3d.div(other: Vector3d) = vec(x / other.x, y / other.y, z / other.z)
operator fun Vector3d.div(other: Double): Vector3d = this * (1 / other)
operator fun Vector3d.div(other: Float): Vector3d = this / other.toDouble()
operator fun Vector3d.div(other: Int): Vector3d = this / other.toDouble()

operator fun Vector3d.plus(other: Vector3d): Vector3d = add(other)
operator fun Vector3d.minus(other: Vector3d): Vector3d = subtract(other)
operator fun Vector3d.unaryMinus(): Vector3d = this * -1.0

infix fun Vector3d.dot(other: Vector3d) = dotProduct(other)

infix fun Vector3d.cross(other: Vector3d): Vector3d = crossProduct(other)

infix fun Vector3d.angle(other: Vector3d) = kotlin.math.acos((this dot other) / (length() * other.length()))

fun Vector3d.withX(other: Double) = vec(other, y, z)
fun Vector3d.withY(other: Double) = vec(x, other, z)
fun Vector3d.withZ(other: Double) = vec(x, y, other)

fun Vector3d.withX(other: Float) = withX(other.toDouble())
fun Vector3d.withY(other: Float) = withY(other.toDouble())
fun Vector3d.withZ(other: Float) = withZ(other.toDouble())

fun Vector3d.withX(other: Int) = withX(other.toDouble())
fun Vector3d.withY(other: Int) = withY(other.toDouble())
fun Vector3d.withZ(other: Int) = withZ(other.toDouble())

val Vector3d.crossMatrix get() = Matrix3d(
    0.0, -z, y,
    z, 0.0, -x,
    -y, x, 0.0
)
val Vector3d.tensorMatrix get() = Matrix3d(
    x * x, x * y, x * z,
    y * x, y * y, y * z,
    z * x, z * y, z * z
)

operator fun Vector3d.component1() = x
operator fun Vector3d.component2() = y
operator fun Vector3d.component3() = z
