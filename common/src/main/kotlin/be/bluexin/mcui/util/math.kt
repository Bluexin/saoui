package be.bluexin.mcui.util

import be.bluexin.mcui.util.math.Vec2d
import org.joml.Intersectiond

typealias BoundingBox2D = Pair<Vec2d, Vec2d>

operator fun BoundingBox2D.contains(point: Vec2d) = Intersectiond.testPointAar(
    point.x, point.y,
    first.x, first.y,
    second.x, second.y
)

operator fun Vec2d.plus(other: Vec2d) = this.add(other)
