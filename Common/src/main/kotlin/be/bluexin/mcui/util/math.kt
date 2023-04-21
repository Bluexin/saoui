package be.bluexin.mcui.util

import org.joml.Intersectiond
import org.joml.Vector2d

typealias BoundingBox2D = Pair<Vector2d, Vector2d>

operator fun BoundingBox2D.contains(point: Vector2d) = Intersectiond.testPointAar(
    point.x, point.y,
    first.x, first.y,
    second.x, second.y
)

operator fun Vector2d.plus(other: Vector2d) = this.add(other)
