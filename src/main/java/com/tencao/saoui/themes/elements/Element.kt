/*
 * Copyright (C) 2016-2019 Arnaud 'Bluexin' Solé
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.tencao.saoui.themes.elements

import com.mojang.blaze3d.matrix.MatrixStack
import com.tencao.saoui.SAOCore
import com.tencao.saoui.api.themes.IHudDrawContext
import com.tencao.saoui.themes.util.CBoolean
import com.tencao.saoui.themes.util.CDouble
import java.lang.ref.WeakReference
import javax.annotation.OverridingMethodsMustInvokeSuper
import javax.xml.bind.annotation.XmlAttribute
import javax.xml.bind.annotation.XmlSeeAlso

/**
 * Used to map an xml element with x, y, z coordinates and an "enabled" toggle.
 *
 * @author Bluexin
 */
@XmlSeeAlso(GLRectangle::class, ElementGroup::class, RawElement::class) // Instructs JAXB to also bind other classes when binding this class
abstract class Element {

    companion object {
        const val DEFAULT_NAME = "anonymous"
    }

    /**
     * X position.
     */
    protected var x: CDouble? = null

    /**
     * Y position.
     */
    protected var y: CDouble? = null

    /**
     * Z position.
     */
    protected var z: CDouble? = null

    /**
     * Whether this element should be enabled.
     */
    protected var enabled: CBoolean? = null

    /**
     * Parent element for this element.
     */
    @Transient
    protected lateinit var parent: WeakReference<ElementParent>

    /**
     * Friendly name for this element. Mostly used for debug purposes.
     */
    @XmlAttribute
    var name: String = DEFAULT_NAME
        @Deprecated("Has no effect")
        private set(value) = Unit // Effectively making it val, but avoiding warnings

    /**
     * Draw this element on the screen.
     * This method should handle all the GL calls.
     *
     * @param ctx additional info about the draws
     */
    abstract fun draw(ctx: IHudDrawContext, stack: MatrixStack)

    /**
     * Called during setup, used to initialize anything extra (after it has finished loading)
     * and returns whether this is an anonymous element.
     *
     * @param parent the parent to this element
     * @return whether this is an anonymous element
     */
    @OverridingMethodsMustInvokeSuper
    open fun setup(parent: ElementParent): Boolean {
        this.parent = WeakReference(parent)
        return if (name != DEFAULT_NAME) {
            SAOCore.LOGGER.info("Set up $this in ${parent.name}"); false
        } else true
    }

    override fun toString() = "$name ($javaClass)"
}
