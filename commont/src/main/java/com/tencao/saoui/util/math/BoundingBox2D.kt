package com.tencao.saoui.util.math

import org.joml.Vector2d

class BoundingBox2D(val min: Vector2d, val max: Vector2d) {

    constructor(minX: Double, minY: Double, maxX: Double, maxY: Double) : this(vec(minX, minY), vec(maxX, maxY))

    fun union(other: BoundingBox2D): BoundingBox2D {
        return BoundingBox2D(
            min.x.coerceAtMost(other.min.x),
            min.y.coerceAtMost(other.min.y),
            max.x.coerceAtLeast(other.max.x),
            max.y.coerceAtLeast(other.max.y)
        )
    }

    fun offset(pos: Vector2d): BoundingBox2D {
        return BoundingBox2D(min + pos, max + pos)
    }

    fun scale(amount: Double): BoundingBox2D {
        return BoundingBox2D(min * amount, max * amount)
    }

    operator fun contains(other: Vector2d): Boolean {
        return other.x <= max.x && other.x >= min.x && other.y <= max.y && other.y >= min.y
    }

    fun height(): Double {
        return max.y - min.y
    }

    fun width(): Double {
        return max.x - min.x
    }

    fun heightF(): Float {
        return max.y.toFloat() - min.y.toFloat()
    }

    fun widthF(): Float {
        return max.xf - min.xf
    }

    fun heightI(): Int {
        return max.yi - min.yi
    }

    fun widthI(): Int {
        return max.xi - min.xi
    }

    val pos: Vector2d
        get() = min
    val size: Vector2d
        get() = max - min
}
