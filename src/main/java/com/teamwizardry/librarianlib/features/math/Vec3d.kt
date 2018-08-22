package com.teamwizardry.librarianlib.features.math

data class Vec3d(val x: Double, val y: Double, val z: Double) {
    fun add(other: Vec3d): Vec3d {
        return Vec3d(x + other.x, y + other.y, z + other.z)
    }

    fun add(otherX: Double, otherY: Double, otherZ: Double): Vec3d {
        return Vec3d(x + otherX, y + otherY, z + otherZ)
    }

    fun sub(other: Vec3d): Vec3d {
        return Vec3d(x - other.x, y - other.y, z - other.z)
    }

    fun sub(otherX: Double, otherY: Double, otherZ: Double): Vec3d {
        return Vec3d(x - otherX, y - otherY, z - otherZ)
    }

    fun mul(other: Vec3d): Vec3d {
        return Vec3d(x * other.x, y * other.y, y * other.z)
    }

    fun mul(otherX: Double, otherY: Double, otherZ: Double): Vec3d {
        return Vec3d(x * otherX, y * otherY, z * otherZ)
    }

    fun mul(amount: Double): Vec3d {
        return Vec3d(x * amount, y * amount, z * amount)
    }

    fun divide(other: Vec3d): Vec3d {
        return Vec3d(x / other.x, y / other.y, z / other.z)
    }

    fun divide(otherX: Double, otherY: Double, otherZ: Double): Vec3d {
        return Vec3d(x / otherX, y / otherY, z / otherZ)
    }

    fun divide(amount: Double): Vec3d {
        return Vec3d(x / amount, y / amount, z / amount)
    }

    infix fun dot(point: Vec3d): Double {
        return x * point.x + y * point.y
    }

    infix fun cross(vec: Vec3d): Vec3d {
        return Vec3d(this.y * vec.z - this.z * vec.y, this.z * vec.x - this.x * vec.z, this.x * vec.y - this.y * vec.x)
    }


    @delegate:Transient
    private val len by lazy { Math.sqrt(x * x + y * y + z * z) }

    fun length(): Double {
        return len
    }

    fun normalize(): Vec3d {
        val norm = length()
        return Vec3d(x / norm, y / norm, z / norm)
    }

    fun squareDist(vec: Vec3d): Double {
        val d0 = vec.x - x
        val d1 = vec.y - y
        val d2 = vec.z - z
        return d0 * d0 + d1 * d1 + d2 * d2
    }

    fun projectOnTo(other: Vec3d): Vec3d {
        val norm = other.normalize()
        return norm.mul(this.dot(norm))
    }
}