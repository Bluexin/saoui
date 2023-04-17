package be.bluexin.mcui.api.elements.animator.internal

/**
 * This is a copy from the library LibrarianLib
 * This code is covered under GNU Lesser General Public License v3.0
 */

import com.tencao.saomclib.utils.math.Vec2d
import be.bluexin.mcui.api.elements.animator.LerperHandler
import be.bluexin.mcui.api.elements.animator.registerLerper
import net.minecraft.util.math.Vec3d

/**
 * TODO: Document file VecLerpers
 *
 * Created by TheCodeWarrior
 */
object VecLerpers {
    init {
        LerperHandler.registerLerper(Vec2d::class.javaObjectType) { from, to, frac ->
            from + (to - from) * frac
        }

        LerperHandler.registerLerper(Vec3d::class.javaObjectType) { from, to, frac ->
            from + (to - from) * frac
        }
    }
}
