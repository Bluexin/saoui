@file:JvmMultifileClass
@file:JvmName("CommonUtilMethods")

package com.teamwizardry.librarianlib.features.helpers

import com.teamwizardry.librarianlib.features.math.Vec2d
import com.teamwizardry.librarianlib.features.math.Vec3d
import net.minecraft.util.AxisAlignedBB
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

private class LibHelpers // so we can jump to this file with "go to class"

fun vec(x: Number, y: Number) = Vec2d(x.toDouble(), y.toDouble())

fun vec(x: Number, y: Number, z: Number) = Vec3d(x.toDouble(), y.toDouble(), z.toDouble())

/*
 * Only out of PURE DESPERATION because mojang has a FUCKING @SideOnly(Side.CLIENT) on the FUCKING (Vec3d, Vec3d) AABB constructor
 * What the fuck mojang? What the actual fuck. If it's some automated system please just add a pointless AABB constructor
 * in the server startup routine? One flimsy object allocation and you could be done with it.
 */
fun aabb(a: Vec3d, b: Vec3d) = AxisAlignedBB.getBoundingBox(a.x, a.y, a.z, b.x, b.y, b.z)

internal var modIdOverride: String? = null

fun <T : Any?> threadLocal() = ThreadLocalDelegate<T>(null)
fun <T> threadLocal(initial: () -> T) = ThreadLocalDelegate(initial)

class ThreadLocalDelegate<T>(initial: (() -> T)?) : ReadWriteProperty<Any, T> {
    private val local = if (initial == null) ThreadLocal<T>() else ThreadLocal.withInitial(initial)

    override fun getValue(thisRef: Any, property: KProperty<*>): T = local.get()
    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) = local.set(value)
}
