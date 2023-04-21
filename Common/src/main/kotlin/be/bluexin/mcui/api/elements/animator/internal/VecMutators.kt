package be.bluexin.mcui.api.elements.animator.internal

/**
 * This is a copy from the library LibrarianLib
 * This code is covered under GNU Lesser General Public License v3.0
 */

import org.joml.Vector2d
import com.tencao.saomclib.utils.math.vec
import net.minecraft.util.math.Vec3d

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

        //region Vec3d
        ImmutableFieldMutatorHandler.registerProvider(
            Vec3d::class.java,
            object : ImmutableFieldMutatorProvider<Vec3d> {
                override fun getMutatorForImmutableField(name: String): ImmutableFieldMutator<Vec3d>? {
                    when (name) {
                        "x" -> {
                            return object : ImmutableFieldMutator<Vec3d> {
                                override fun mutate(target: Vec3d, value: Any?): Vec3d {
                                    value ?: throw IllegalArgumentException("Cannot set Vec3d.x to null")
                                    return target.withX(doubleFrom(value))
                                }
                            }
                        }
                        "y" -> {
                            return object : ImmutableFieldMutator<Vec3d> {
                                override fun mutate(target: Vec3d, value: Any?): Vec3d {
                                    value ?: throw IllegalArgumentException("Cannot set Vec3d.y to null")
                                    return target.withX(doubleFrom(value))
                                }
                            }
                        }
                        "z" -> {
                            return object : ImmutableFieldMutator<Vec3d> {
                                override fun mutate(target: Vec3d, value: Any?): Vec3d {
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

fun Vector2d.withX(other: Double) = vec(other, y)
fun Vector2d.withY(other: Double) = vec(x, other)

operator fun Vec3d.times(other: Vec3d): Vec3d = vec(x * other.x, y * other.y, z * other.z)
operator fun Vec3d.times(other: Double): Vec3d = scale(other)
operator fun Vec3d.times(other: Float): Vec3d = this * other.toDouble()
operator fun Vec3d.times(other: Int): Vec3d = this * other.toDouble()

operator fun Vec3d.div(other: Vec3d) = vec(x / other.x, y / other.y, z / other.z)
operator fun Vec3d.div(other: Double): Vec3d = this * (1 / other)
operator fun Vec3d.div(other: Float): Vec3d = this / other.toDouble()
operator fun Vec3d.div(other: Int): Vec3d = this / other.toDouble()

operator fun Vec3d.plus(other: Vec3d): Vec3d = add(other)
operator fun Vec3d.minus(other: Vec3d): Vec3d = subtract(other)
operator fun Vec3d.unaryMinus(): Vec3d = this * -1.0

infix fun Vec3d.dot(other: Vec3d) = dotProduct(other)

infix fun Vec3d.cross(other: Vec3d): Vec3d = crossProduct(other)

infix fun Vec3d.angle(other: Vec3d) = kotlin.math.acos((this dot other) / (lengthVector() * other.lengthVector()))

fun Vec3d.withX(other: Double) = vec(other, y, z)
fun Vec3d.withY(other: Double) = vec(x, other, z)
fun Vec3d.withZ(other: Double) = vec(x, y, other)

fun Vec3d.withX(other: Float) = withX(other.toDouble())
fun Vec3d.withY(other: Float) = withY(other.toDouble())
fun Vec3d.withZ(other: Float) = withZ(other.toDouble())

fun Vec3d.withX(other: Int) = withX(other.toDouble())
fun Vec3d.withY(other: Int) = withY(other.toDouble())
fun Vec3d.withZ(other: Int) = withZ(other.toDouble())

val Vec3d.crossMatrix get() = javax.vecmath.Matrix3d(
    0.0, -z, y,
    z, 0.0, -x,
    -y, x, 0.0
)
val Vec3d.tensorMatrix get() = javax.vecmath.Matrix3d(
    x * x, x * y, x * z,
    y * x, y * y, y * z,
    z * x, z * y, z * z
)

operator fun Vec3d.component1() = x
operator fun Vec3d.component2() = y
operator fun Vec3d.component3() = z
