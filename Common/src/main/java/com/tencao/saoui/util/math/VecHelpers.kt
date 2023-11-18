package com.tencao.saoui.util.math

import org.joml.Vector2d
import org.joml.Vector3d

object Vec2dPool {
    private const val poolBits = 7
    private val poolMask = (1 shl poolBits) - 1
    private val poolMax = (1 shl poolBits - 1) - 1
    private val poolMin = -(1 shl poolBits -1)

    @JvmStatic
    private val pool = Array(1 shl poolBits * 2) {
        val x = (it shr poolBits) + poolMin
        val y = (it and poolMask) + poolMin
        Vector2d(x.toDouble(), y.toDouble())
    }

    @JvmStatic
    fun getPooled(x: Double, y: Double): Vector2d {
        val xi = x.toInt()
        val yi = y.toInt()
        if (xi.toDouble() == x && xi in poolMin..poolMax &&
            yi.toDouble() == y && yi in poolMin..poolMax
        ) {
            return pool[
                    (xi - poolMin) shl poolBits or (yi - poolMin)
            ]
        }
        return Vector2d(x, y)
    }
}

/**
 * Get `Vec3d` instances, selecting from a pool of small integer instances when possible. This can vastly reduce the
 * number of Vec3d allocations when they are used as intermediates, e.g. when adding one Vec3d to another to offset it,
 * this allocates no objects: `Vec3dPool.get(1, 0, 0)`
 */
object Vec3dPool {
    private const val poolBits = 5
    private val poolMask = (1 shl poolBits) - 1
    private val poolMax = (1 shl poolBits - 1) - 1
    private val poolMin = -(1 shl poolBits-1)
    private val vec3dPool = Array(1 shl poolBits * 3) {
        val x = (it shr poolBits * 2) + poolMin
        val y = (it shr poolBits and poolMask) + poolMin
        val z = (it and poolMask) + poolMin
        Vector3d(x.toDouble(), y.toDouble(), z.toDouble())
    }

    @JvmStatic
    fun create(x: Double, y: Double, z: Double): Vector3d {
        val xi = x.toInt()
        val yi = y.toInt()
        val zi = z.toInt()
        if (xi.toDouble() == x && xi in poolMin..poolMax &&
            yi.toDouble() == y && yi in poolMin..poolMax &&
            zi.toDouble() == z && zi in poolMin..poolMax
        ) {
            return vec3dPool[
                    ((xi - poolMin) shl poolBits * 2) or ((yi - poolMin) shl poolBits) or (zi - poolMin)
            ]
        }
        val newVec = Vector3d(x, y, z)
        return newVec
    }
}

inline fun vec(x: Number, y: Number) = Vec2dPool.getPooled(x.toDouble(), y.toDouble())
inline fun vec(x: Number, y: Number, z: Number) = Vec3dPool.create(x.toDouble(), y.toDouble(), z.toDouble())


operator fun Vector2d.minus(other: Vector2d): Vector2d {
    return Vec2dPool.getPooled(x - other.x, y - other.y)
}

operator fun Vector2d.plus(other: Vector2d): Vector2d {
    return Vec2dPool.getPooled(x + other.x, y + other.y)
}

operator fun Vector2d.times(amount: Number): Vector2d {
    val number = amount.toDouble()
    return Vec2dPool.getPooled(x * number, y * number)
}

val Vector2d.xi: Int
    get() = this.x.toInt()

val Vector2d.xf: Float
    get() = this.x.toFloat()

val Vector2d.yi: Int
    get() = this.y.toInt()

val Vector2d.yf: Float
    get() = this.y.toFloat()


fun Int.clamp(min: Int, max: Int): Int = if (this < min) min else if (this > max) max else this