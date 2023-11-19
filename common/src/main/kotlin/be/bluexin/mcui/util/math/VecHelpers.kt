package be.bluexin.mcui.util.math

/**
 * This is a copy from the library LibrarianLib
 * This code is covered under GNU Lesser General Public License v3.0
 */

import org.joml.Vector3d
import org.joml.Vector3dc

/**
 * Get `Vec3d` instances, selecting from a pool of small integer instances when possible. This can vastly reduce the
 * number of Vec3d allocations when they are used as intermediates, e.g. when adding one Vec3d to another to offset it,
 * this allocates no objects: `Vec3dPool.get(1, 0, 0)`
 */
object Vec3dPool {
    private val poolBits = 5
    private val poolMask = (1 shl poolBits) - 1
    private val poolMax = (1 shl poolBits - 1) - 1
    private val poolMin = -(1 shl poolBits -1)
    private val vec3dPool = Array<Vector3dc>(1 shl poolBits * 3) {
        val x = (it shr poolBits * 2) + poolMin
        val y = (it shr poolBits and poolMask) + poolMin
        val z = (it and poolMask) + poolMin
        Vector3d(x.toDouble(), y.toDouble(), z.toDouble())
    }

    @JvmStatic
    fun create(x: Double, y: Double, z: Double): Vector3dc {
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

fun vec(x: Number, y: Number) = Vec2d.getPooled(x.toDouble(), y.toDouble())
fun vec(x: Number, y: Number, z: Number) = Vec3dPool.create(x.toDouble(), y.toDouble(), z.toDouble())

// inline fun BufferBuilder.pos(x: Number, y: Number, z: Number): BufferBuilder = this.pos(x.toDouble(), y.toDouble(), z.toDouble())
// inline fun BufferBuilder.pos(x: Number, y: Number): BufferBuilder = this.pos(x.toDouble(), y.toDouble(), 0.0)

val Vector3dc.x get() = x()
val Vector3dc.y get() = y()
val Vector3dc.z get() = z()
