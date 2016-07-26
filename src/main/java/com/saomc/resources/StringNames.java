package com.saomc.resources;

import com.saomc.SAOCore;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class StringNames {

    public static final ResourceLocation gui = new ResourceLocation(SAOCore.MODID, "textures/gui.png");
    public static final ResourceLocation guiCustom = new ResourceLocation(SAOCore.MODID, "textures/guiCustom.png");
    public static final ResourceLocation slot = new ResourceLocation(SAOCore.MODID, "textures/slot.png");
    public static final ResourceLocation icons = new ResourceLocation(SAOCore.MODID, "textures/icons.png");
    public static final ResourceLocation iconsCustom = new ResourceLocation(SAOCore.MODID, "textures/iconsCustom.png");
    public static final ResourceLocation effects = new ResourceLocation(SAOCore.MODID, "textures/gui.png");
    public static final ResourceLocation effectsCustom = new ResourceLocation(SAOCore.MODID, "textures/guiCustom.png");
    public static final ResourceLocation entities = new ResourceLocation(SAOCore.MODID, "textures/entities.png");
    public static final ResourceLocation entitiesCustom = new ResourceLocation(SAOCore.MODID, "textures/entitiesCustom.png");
    public static final ResourceLocation particleLarge = new ResourceLocation(SAOCore.MODID, "textures/particleLarge.png");
    public static final String FRIEND_REQUEST_TITLE = I18n.translateToLocal("guiFriendRequestTitle");
    public static final String FRIEND_REQUEST_TEXT = "guiFriendRequestText";
    public static final String PARTY_INVITATION_TITLE = I18n.translateToLocal("guiPartyInviteTitle");
    public static final String PARTY_INVITATION_TEXT = "guiPartyInviteText";
    public static final String PARTY_DISSOLVING_TITLE = I18n.translateToLocal("guiPartyDissolvingTitle");
    public static final String PARTY_DISSOLVING_TEXT = I18n.translateToLocal("guiPartyDissolvingText");
    public static final String PARTY_LEAVING_TITLE = I18n.translateToLocal("guiPartyLeaveTitle");
    public static final String PARTY_LEAVING_TEXT = I18n.translateToLocal("guiPartyLeaveText");
    public static final String MESSAGE_TITLE = I18n.translateToLocal("guiMessageTitle");
    public static final String MESSAGE_FROM = "guiMessageFrom";
    public static final String DEAD_ALERT = I18n.translateToLocal("guiDeadAlert");

    private StringNames() {
    }

}
