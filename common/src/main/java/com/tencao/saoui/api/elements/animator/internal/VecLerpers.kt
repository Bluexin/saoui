package com.tencao.saoui.api.elements.animator.internal

/**
 * This is a copy from the library LibrarianLib
 * This code is covered under GNU Lesser General Public License v3.0
 */

import com.tencao.saoui.api.elements.animator.LerperHandler
import com.tencao.saoui.api.elements.animator.registerLerper
import com.tencao.saoui.util.math.minus
import com.tencao.saoui.util.math.plus
import com.tencao.saoui.util.math.times
import org.joml.Vector2d

/**
 * TODO: Document file VecLerpers
 *
 * Created by TheCodeWarrior
 */
@Deprecated("")
object VecLerpers {
    init {
        LerperHandler.registerLerper(Vector2d::class.javaObjectType) { from, to, frac ->
            from + (to - from) * frac
        }

        LerperHandler.registerLerper(Vector2d::class.javaObjectType) { from, to, frac ->
            from + (to - from) * frac
        }
    }
}
