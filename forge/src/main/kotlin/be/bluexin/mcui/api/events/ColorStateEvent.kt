package be.bluexin.mcui.api.events

import net.minecraft.world.entity.LivingEntity
import net.minecraftforge.eventbus.api.Event
import javax.annotation.Nonnull

class ColorStateEvent(entityLivingBase: LivingEntity) : Event() {
    private val theEnt: LivingEntity
//    var state: IColorStateHandler? = null
//    var provider: ICustomizationProvider? = null

    init {
        theEnt = entityLivingBase
    }

    @get:Nonnull
    val entity: LivingEntity
        get() = theEnt
}
