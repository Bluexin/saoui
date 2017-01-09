package com.saomc.saoui.themes.elements

import com.saomc.saoui.themes.util.HudDrawContext
import javax.xml.bind.annotation.XmlSeeAlso
import javax.xml.bind.annotation.XmlTransient

/**
 * Part of saoui by Bluexin.

 * @author Bluexin
 */
@XmlTransient //Prevents the mapping of load JavaBean property/type to XML representation
@XmlSeeAlso(GLRectangle::class, GLString::class, GLHotbarItem::class) //Instructs JAXB to also bind other classes when binding this class
interface Element {

    /**
     * Draw this element on the screen.
     * This method should handle all the GL calls.

     * @param ctx additional info about the draws
     */
    fun draw(ctx: HudDrawContext)

    /**
     * Called during setup, used to initialize anything extra (after it has finished loading).

     * @param parent the parent to this element
     */
    fun setup(parent: ElementParent)
}
