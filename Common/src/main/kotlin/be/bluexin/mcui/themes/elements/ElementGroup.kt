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

package be.bluexin.mcui.themes.elements

import com.google.gson.annotations.SerializedName
import be.bluexin.mcui.GLCore
import be.bluexin.mcui.api.themes.IHudDrawContext
import be.bluexin.mcui.themes.elements.*
import net.minecraft.resources.ResourceLocation
import javax.xml.bind.annotation.*

/**
 * Part of saoui by Bluexin.
 *
 * @author Bluexin
 */
@XmlRootElement
@XmlSeeAlso(RepetitionGroup::class)
open class ElementGroup : CachingElementParent() {

    @field:SerializedName("children")
    @XmlElementWrapper(name = "children")
    @XmlElementRef(type = Element::class)
    protected lateinit var elements: List<Element>

    @Transient
    protected var rl: ResourceLocation? = null

    private var texture: String? = null

    override fun draw(ctx: IHudDrawContext) {
        GLCore.glBlend(true)
        GLCore.color(1f, 1f, 1f, 1f)

        if (enabled?.invoke(ctx) == false) return
        if (this.rl != null) GLCore.glBindTexture(this.rl!!)

        this.elements.forEach { it.draw(ctx) }
    }

    override fun setup(parent: ElementParent, fragments: Map<ResourceLocation, () -> Fragment>): Boolean {
        val res = super.setup(parent, fragments)
        this.rl = this.texture?.let(::ResourceLocation)
        var anonymous = 0
        this.elements.forEach { if (it.name == DEFAULT_NAME) ++anonymous; it.setup(this, fragments) }
//        if (anonymous > 0) Constants.LOG.info("Set up $anonymous anonymous elements in $name.")
        return res
    }
}
