package com.saomc.saoui.themes.elements.menus

import com.saomc.saoui.SAOCore
import com.saomc.saoui.api.themes.IHudDrawContext
import com.saomc.saoui.themes.util.CBoolean
import com.saomc.saoui.themes.util.CInt
import java.lang.ref.WeakReference
import javax.annotation.OverridingMethodsMustInvokeSuper
import javax.xml.bind.annotation.XmlAttribute
import javax.xml.bind.annotation.XmlSeeAlso

/**
 * Used to map an xml element with x, y, z coordinates and an "enabled" toggle.
 *
 * @author Bluexin
 */
@XmlSeeAlso(MenuElementGroup::class) //Instructs JAXB to also bind other classes when binding this class
abstract class MenuElement {

    companion object {
        val DEFAULT_NAME = "anonymous"
    }

    /**
     * X position.
     */
    protected var x: CInt? = null
    /**
     * Y position.
     */
    protected var y: CInt? = null
    /**
     * Z position.
     */
    protected var z: CInt? = null
    /**
     * Whether this element should be enabled.
     */
    protected var enabled: CBoolean? = null
    /**
     * Parent element for this element.
     */
    @Transient lateinit protected var parent: WeakReference<MenuElementParent>

    /**
     * Friendly name for this element. Mostly used for debug purposes.
     */
    @XmlAttribute val name: String = DEFAULT_NAME

    /**
     * Draw this element on the screen.
     * This method should handle all the GL calls.

     * @param ctx additional info about the draws
     */
    abstract fun draw(ctx: IHudDrawContext)

    /**
     * Called during setup, used to initialize anything extra (after it has finished loading)
     * and returns whether this is an anonymous element.

     * @param parent the parent to this element
     * @return whether this is an anonymous element
     */
    @OverridingMethodsMustInvokeSuper
    open fun setup(parent: MenuElementParent): Boolean {
        this.parent = WeakReference(parent)
        return if (name != DEFAULT_NAME) {
            SAOCore.LOGGER.info("Set up $this in ${parent.name}"); false
        } else true
    }

    override fun toString() = "$name ($javaClass)"
}
