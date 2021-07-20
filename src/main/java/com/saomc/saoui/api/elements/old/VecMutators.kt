package com.saomc.saoui.api.elements.old

import com.teamwizardry.librarianlib.core.util.vec
import com.teamwizardry.librarianlib.math.Vec2d
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
        ImmutableFieldMutatorHandler.registerProvider(Vec2d::class.java, object : ImmutableFieldMutatorProvider<Vec2d> {
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

        })
        //endregion

        //region Vec3d
        ImmutableFieldMutatorHandler.registerProvider(Vector3d::class.java, object : ImmutableFieldMutatorProvider<Vector3d> {
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

        })
        //endregion
    }
}

fun Vec2d.withX(other: Double) = vec(other, y)
fun Vec2d.withY(other: Double) = vec(x, other)

fun Vector3d.withX(other: Double) = vec(other, y, z)
fun Vector3d.withY(other: Double) = vec(x, other, z)
fun Vector3d.withZ(other: Double) = vec(x, y, other)