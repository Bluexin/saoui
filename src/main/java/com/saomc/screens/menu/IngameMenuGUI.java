package com.saomc.screens.menu;

import baubles.api.BaublesApi;
import com.saomc.SoundCore;
import com.saomc.events.ConfigHandler;
import com.saomc.events.FriendsHandler;
import com.saomc.screens.*;
import com.saomc.screens.buttons.*;
import com.saomc.screens.ingame.IngameGUI;
import com.saomc.screens.inventory.InventoryCore;
import com.saomc.screens.inventory.InventoryGUI;
import com.saomc.screens.window.ScreenGUI;
import com.saomc.screens.window.SubWindow;
import com.saomc.screens.window.WindowAlign;
import com.saomc.screens.window.WindowView;
import com.saomc.screens.window.ui.AchievementList;
import com.saomc.social.StaticPlayerHelper;
import com.saomc.social.friends.FriendCore;
import com.saomc.social.friends.FriendList;
import com.saomc.social.party.PartyHandler;
import com.saomc.social.party.PartyHelper;
import com.saomc.util.*;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@SideOnly(Side.CLIENT)
public class IngameMenuGUI extends ScreenGUI {

    private final List<Entry<Categories, MenuGUI>> menus;
    private final Strings[] infoData = new Strings[2];
    private final GuiInventory parentInv;
    private int flowY;
    private int flowX, jumpX;
    private OptionCore openOptCat = null;
    private MenuGUI sub;
    private Panel info;
    private LabelGUI infoCaption;
    private TextGUI infoText;
    private boolean playedSound;

    public IngameMenuGUI(GuiInventory vanillaGUI) {
        super();
        menus = new ArrayList<>();
        parentInv = vanillaGUI;
        info = null;
    }

    @Override
    protected void init() {
        super.init();
        menus.clear();

        IconGUI action1, action2, action;

        elements.add(action1 = new IconGUI(this, Categories.PROFILE, 0, 0, IconCore.PROFILE));
        elements.add(action2 = new IconGUI(action1, Categories.SOCIAL, 0, 24, IconCore.SOCIAL));
        elements.add(action = new IconGUI(action2, Categories.MESSAGE, 0, 24, IconCore.MESSAGE));
        elements.add(action = new IconGUI(action, Categories.NAVIGATION, 0, 24, IconCore.NAVIGATION));
        elements.add(action = new IconGUI(action, Categories.SETTINGS, 0, 24, IconCore.SETTINGS));

        if (parentInv != null) openMenu(action1, action1.ID());

        flowY = -height;
    }

    @Override
    public void drawScreen(int cursorX, int cursorY, float f) {
        super.drawScreen(cursorX, cursorY, f);

        if (!playedSound) {
            SoundCore.play(mc, SoundCore.ORB_DROPDOWN);
            playedSound = true;
        }
    }

    @Override
    public int getX(boolean relative) {
        return super.getX(relative) + width * 2 / 5 + (flowX - jumpX) / 2;
    }

    @Override
    public int getY(boolean relative) {
        return super.getY(relative) + flowY;
    }

    @Override
    public void updateScreen() {
        super.updateScreen();

        if (flowY < height / 2) flowY = (flowY + height / 2 - 32) / 2;
        flowX /= 2;
        if (infoData[0] != null && infoData[1] != null) updateInfo(infoData[0].toString(), infoData[1].toString());
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        SoundCore.play(mc, SoundCore.DIALOG_CLOSE);
    }

    @Override
    public void actionPerformed(Elements element, Actions action, int data) {
        final Categories id = element.ID();

        if (id.isMenu()) {
            if (isMenuOpen(id)) {
                element.click(mc.getSoundHandler(), false);
                closeMenu(element, id);
            } else {
                element.click(mc.getSoundHandler(), true);
                openMenu(element, id);
            }
        } else if (id != Categories.NONE) {
            element.click(mc.getSoundHandler(), false);
            action(element, id, action, data);
        }
    }

    private boolean isMenuOpen(Categories id) {
        return menus.stream().anyMatch(entry -> entry.getKey() == id);
    }

    private void action(Elements element, Categories id, Actions action, int data) {
        if (id == Categories.MENU) {
            mc.displayGuiScreen(new GuiIngameMenu());
        } else if (id == Categories.SLOT && element instanceof Slots && element.parent instanceof InventoryGUI) {
            final Slots slot = (Slots) element;
            final InventoryGUI inventory = (InventoryGUI) element.parent;

            final InventoryCore type = inventory.filter;
            final Container container = inventory.slots;
            final ItemStack stack = slot.getStack();

            if (stack != null) {
                if (action == Actions.LEFT_RELEASED) {
                    final Slot current = findSwapSlot(container, slot.getSlot(), type);

                    if (type == InventoryCore.ACCESSORY) {
                        if (InventoryCore.isBaublesLoaded() && current != null && current.slotNumber != slot.getSlotNumber()) {
                            final IInventory inventoryBauble = BaublesApi.getBaubles(mc.thePlayer);
                            inventoryBauble.openInventory(mc.thePlayer);
                            inventory.handleMouseClick(mc, slot.getSlot(), slot.getSlotNumber(), 0, ClickType.PICKUP);
                            inventory.handleMouseClick(mc, current, current.slotNumber, 0, ClickType.SWAP);
                            inventory.handleMouseClick(mc, slot.getSlot(), slot.getSlotNumber(), 0, ClickType.SWAP);
                            inventoryBauble.closeInventory(mc.thePlayer);

                        }
                    } else if (current != null && current.slotNumber != slot.getSlotNumber()) {
                        inventory.handleMouseClick(mc, slot.getSlot(), slot.getSlotNumber(), 0, ClickType.PICKUP);
                        inventory.handleMouseClick(mc, current, current.slotNumber, 0, ClickType.SWAP);
                        inventory.handleMouseClick(mc, slot.getSlot(), slot.getSlotNumber(), 0, ClickType.SWAP);
                    }

                } else if (action == Actions.RIGHT_RELEASED) {
                    inventory.handleMouseClick(mc, slot.getSlot(), slot.getSlotNumber(), 1, ClickType.THROW);
                } else if (action == Actions.MIDDLE_RELEASED || action == Actions.KEY_TYPED && data == mc.gameSettings.keyBindPickBlock.getKeyCode()) {
                    Strings caption = null;
                    StringBuilder text = new StringBuilder();

                    for (final Object line : stack.getTooltip(mc.thePlayer, false))
                        if (caption != null) text.append(line).append('\n');
                        else caption = new JString(line);

                    setInfo(caption, new JString(text.toString()));
                } else if (action == Actions.KEY_TYPED && data == mc.gameSettings.keyBindDrop.getKeyCode())
                    inventory.handleMouseClick(mc, slot.getSlot(), slot.getSlotNumber(), 0, ClickType.THROW);
            }
        } else if (id == Categories.SKILL && element instanceof SkillButton) {
            ((SkillButton) element).action(mc, parentInv);
        } else if (id == Categories.INVITE_PLAYER && element instanceof ButtonGUI) {
            final String name = ((ButtonGUI) element).caption;

            PartyHelper.instance().invite(mc, name);
        } else if (id == Categories.DISSOLVE) {
            element.enabled = false;

            final boolean isLeader = PartyHelper.instance().isLeader(StaticPlayerHelper.getName(mc));

            final String title = isLeader ? ConfigHandler._PARTY_DISSOLVING_TITLE : ConfigHandler._PARTY_LEAVING_TITLE;
            final String text = isLeader ? ConfigHandler._PARTY_DISSOLVING_TEXT : ConfigHandler._PARTY_LEAVING_TEXT;

            mc.displayGuiScreen(WindowView.viewConfirm(title, text, (element1, action1, data1) -> {
                final Categories id1 = element1.ID();

                if (id1 == Categories.CONFIRM) PartyHelper.instance().sendDissolve(mc);

                mc.displayGuiScreen(null);
                mc.setIngameFocus();
            }));
        } else if (id == Categories.MESSAGE && mc.ingameGUI instanceof IngameGUI) {
            // ((IngameGUI) mc.ingameGUI).viewMessageAuto();
        } else if (id == Categories.MESSAGE_BOX && element.parent instanceof MenuGUI && ((MenuGUI) element.parent).parent instanceof FriendCore) {
            final String username = ((FriendCore) ((MenuGUI) element.parent).parent).caption;

            final String format = I18n.format("commands.message.usage");
            final String cmd = format.substring(0, format.indexOf(' '));

            final String message = J8String.join(" ", cmd, username, "");

            mc.displayGuiScreen(new GuiChat(message));
        } else if ((id == Categories.QUEST) && (element instanceof AchievementList)) {
            final AchievementList quest = (AchievementList) element;
            final Achievement ach0 = quest.getAchievement();

            setInfo(new JString(quest.caption), new JString(ach0.getDescription()));
        } else if (id == Categories.OPTION && element instanceof OptionButton) {
            final OptionButton button = (OptionButton) element;

            if (button.getOption() == OptionCore.VANILLA_OPTIONS) {
                mc.displayGuiScreen(new GuiOptions(this, mc.gameSettings));
            } else if (button.getOption().isRestricted()) {
                reloadList(element, id); // Needs to be called first
                button.action();
            } else {
                button.action();
            }
        } else if (id == Categories.LOGOUT) {
            if (OptionCore.LOGOUT.getValue()) {
                element.enabled = false;
                mc.theWorld.sendQuittingDisconnectingPacket();

                mc.loadWorld(null);
                mc.currentScreen.onGuiClosed();
                mc.displayGuiScreen(new GuiMainMenu());
            }
        }
    }

    private Slot findSwapSlot(Container container, Slot swap, InventoryCore type) {
        if (type == InventoryCore.EQUIPMENT) {
            if (swap.slotNumber < 9) return findEmptySlot(container, 9);
            else {
                for (int i = 5; i < 9; i++)
                    if (container.getSlot(i).isItemValid(swap.getStack())) return container.getSlot(i);

                return null;
            }
        } else if (type == InventoryCore.WEAPONS) {
            return swap.slotNumber >= 36 ? findEmptySlot(container, 9) : container.getSlot(36);
        } else if (type == InventoryCore.BOWS) {
            return swap.slotNumber >= 37 ? findEmptySlot(container, 9) : container.getSlot(37);
        } else if (type == InventoryCore.PICKAXE) {
            return swap.slotNumber >= 38 ? findEmptySlot(container, 9) : container.getSlot(38);
        } else if (type == InventoryCore.AXE) {
            return swap.slotNumber >= 39 ? findEmptySlot(container, 9) : container.getSlot(39);
        } else if (type == InventoryCore.SHOVEL) {
            return swap.slotNumber >= 40 ? findEmptySlot(container, 9) : container.getSlot(40);
        } else if (type == InventoryCore.ACCESSORY) {
            IInventory baubles = InventoryCore.getBaubles(mc.thePlayer);
            if (baubles != null) {
                if (Objects.equals(swap.inventory, baubles)) return findEmptySlot(container, 9);
                else {
                    for (int i = 0; i < baubles.getSizeInventory(); i++) {
                        if (baubles.isItemValidForSlot(i, swap.getStack())) return container.getSlot(i);
                    }
                }
            }
            return null;
        } else if (type == InventoryCore.CONSUMABLES) {
            return swap.slotNumber >= 41 ? findEmptySlot(container, 9) : container.getSlot(41);
        } else if (type == InventoryCore.ITEMS) {
            if (swap.slotNumber >= 42) return findEmptySlot(container, 9);
            else {
                Slot slot = findEmptySlot(container, 42);

                if (slot == null) {
                    return currentSlot(container);
                } else return slot;
            }
        } else return null;
    }

    private Slot currentSlot(Container container) {
        return container.getSlotFromInventory(mc.thePlayer.inventory, mc.thePlayer.inventory.currentItem);
    }

    private Slot findEmptySlot(Container container, int startIndex) {
        @SuppressWarnings("unchecked") // Cuz goddammit can't you guys declare it as List<Slot>? è_é
                Optional<Slot> optSlot = container.inventorySlots.stream().skip(startIndex - 1).filter(obj -> !((Slot) obj).getHasStack()).findFirst();

        return optSlot.orElse(null);
    }

    private void updateInfo(String caption, String text) {
        //System.out.println(caption + " : " + text);

        if (info != null) {
            if (infoCaption == null) {
                info.elements.add(infoCaption = new LabelGUI(info, 15, 0, info.width - 15, caption, WindowAlign.LEFT));
                infoCaption.fontColor = ColorUtil.DEFAULT_BOX_FONT_COLOR;
            } else infoCaption.caption = caption;

            if (infoText == null) {
                info.elements.add(infoText = new TextGUI(info, 15, 0, text, info.width - 15));
                infoText.fontColor = ColorUtil.DEFAULT_BOX_FONT_COLOR;
            } else infoText.setText(text);
        }
    }

    private void setInfo(Strings caption, Strings text) {
        infoData[0] = caption;
        infoData[1] = text;

        updateInfo(infoData[0] != null ? infoData[0].toString() : "", infoData[1] != null ? infoData[1].toString() : "");
    }

    @SuppressWarnings("unchecked")
    private void openMenu(Elements element, Categories id) {
        final int menuOffsetX = element.width + 14;
        final int menuOffsetY = element.height / 2;

        MenuSlotGUI menu = null;
        MenuGUI subMenu = sub;
        // Core Menu
        if (id == Categories.PROFILE) {
            menu = new MenuSlotGUI(element, menuOffsetX, menuOffsetY);

            menu.elements.add(new ButtonGUI(menu, Categories.EQUIPMENT, 0, 0, net.minecraft.util.text.translation.I18n.translateToLocal("guiEquipment"), IconCore.EQUIPMENT));
            menu.elements.add(new ButtonGUI(menu, Categories.ITEMS, 0, 0, net.minecraft.util.text.translation.I18n.translateToLocal("guiItems"), IconCore.ITEMS));
            menu.elements.add(new ButtonGUI(menu, Categories.SKILLS, 0, 0, net.minecraft.util.text.translation.I18n.translateToLocal("guiSkills"), IconCore.SKILLS));

            sub = SubWindow.createMainProfileSub(mc, element, -189, menuOffsetY);
            info = SubWindow.addInfo(sub);

            final Strings[] profile = SubWindow.addProfileContent(mc);

            setInfo(profile[0], profile[1]);
        } else if (id == Categories.SOCIAL) {
            setInfo(null, null);
            menu = new MenuSlotGUI(element, menuOffsetX, menuOffsetY);

            menu.elements.add(new ButtonGUI(menu, Categories.GUILD, 0, 0, net.minecraft.util.text.translation.I18n.translateToLocal("guiGuild"), IconCore.GUILD));
            menu.elements.add(new ButtonGUI(menu, Categories.PARTY, 0, 0, net.minecraft.util.text.translation.I18n.translateToLocal("guiParty"), IconCore.PARTY));
            menu.elements.add(new ButtonGUI(menu, Categories.FRIENDS, 0, 0, net.minecraft.util.text.translation.I18n.translateToLocal("guiFriends"), IconCore.FRIEND));

            sub = SubWindow.createSocialSub(mc, element, -189, menuOffsetY);
            info = SubWindow.addInfo(sub);

            infoCaption = null;
            infoText = null;
        } else if (id == Categories.MESSAGE) {
            setInfo(null, null);
            menu = new MenuSlotGUI(element, menuOffsetX, menuOffsetY);

            menu.elements.add(new ButtonGUI(menu, Categories.MESSAGE_BOX, 0, 0, net.minecraft.util.text.translation.I18n.translateToLocal("guiMessageBox"), IconCore.MESSAGE));

            sub = SubWindow.createSocialSub(mc, element, -189, menuOffsetY);
            info = SubWindow.addInfo(sub);

            infoCaption = null;
            infoText = null;
        } else if (id == Categories.NAVIGATION) {
            setInfo(null, null);
            menu = new MenuSlotGUI(element, menuOffsetX, menuOffsetY);

            menu.elements.add(new ButtonGUI(menu, Categories.QUESTS, 0, 0, net.minecraft.util.text.translation.I18n.translateToLocal("guiQuest"), IconCore.QUEST));
            if (OptionCore.DEBUG_MODE.getValue())
                menu.elements.add(new ButtonGUI(menu, Categories.FIELD_MAP, 0, 0, net.minecraft.util.text.translation.I18n.translateToLocal("guiFieldMap"), IconCore.FIELD_MAP));
            if (OptionCore.DEBUG_MODE.getValue())
                menu.elements.add(new ButtonGUI(menu, Categories.DUNGEON_MAP, 0, 0, net.minecraft.util.text.translation.I18n.translateToLocal("guiDungMap"), IconCore.DUNGEON_MAP));

            sub = SubWindow.createNavigationSub(mc, element, -189, menuOffsetY);
            info = SubWindow.addInfo(sub);

        } else if (id == Categories.SETTINGS) {
            menu = new MenuSlotGUI(element, menuOffsetX, menuOffsetY);

            menu.elements.add(new ButtonGUI(menu, Categories.OPTIONS, 0, 0, net.minecraft.util.text.translation.I18n.translateToLocal("guiOption"), IconCore.OPTION));
            menu.elements.add(new ButtonGUI(menu, Categories.MENU, 0, 0, net.minecraft.util.text.translation.I18n.translateToLocal("guiMenu"), IconCore.HELP));
            menu.elements.add(new ButtonState(menu, Categories.LOGOUT, 0, 0, OptionCore.LOGOUT.getValue() ? net.minecraft.util.text.translation.I18n.translateToLocal("guiLogout") : "", IconCore.LOGOUT, (mc1, button) -> {
                if (OptionCore.LOGOUT.getValue()) {
                    if (button.caption.length() == 0) button.caption = "Logout";
                } else if (button.caption.length() > 0) button.caption = "";

                return button.enabled;
            }));
        }
        //Profile
        else if (id == Categories.EQUIPMENT) {
            menu = new MenuSlotGUI(element, menuOffsetX, menuOffsetY);

            menu.elements.add(new ButtonGUI(menu, Categories.TOOLS, 0, 0, net.minecraft.util.text.translation.I18n.translateToLocal("guiTools"), IconCore.EQUIPMENT));
            menu.elements.add(new ButtonGUI(menu, Categories.ARMOR, 0, 0, net.minecraft.util.text.translation.I18n.translateToLocal("guiEquipped"), IconCore.ARMOR));
            if (InventoryCore.isBaublesLoaded())
                menu.elements.add(new ButtonGUI(menu, Categories.ACCESSORY, 0, 0, net.minecraft.util.text.translation.I18n.translateToLocal("guiAccessory"), IconCore.ACCESSORY));
            menu.elements.add(new ButtonGUI(menu, Categories.CONSUMABLES, 0, 0, net.minecraft.util.text.translation.I18n.translateToLocal("guiConsumable"), IconCore.ITEMS));
        } else if (id == Categories.ITEMS) {
            menu = new InventoryGUI(element, menuOffsetX, menuOffsetY, mc.thePlayer.inventoryContainer, InventoryCore.ITEMS);
        } else if (id == Categories.SKILLS) {
            menu = new ListGUI(element, menuOffsetX, menuOffsetY, 100, 60);

            final MenuGUI mnu = menu;
            Stream.of(Skills.values()).forEach(skill -> mnu.elements.add(new SkillButton(mnu, 0, 0, skill)));
        }
        //Profile -> Equipment
        else if (id == Categories.TOOLS) { // TODO: Some optimization could be done here. Laterz.
            if (OptionCore.COMPACT_INVENTORY.getValue()) {
                menu = new InventoryGUI(element, menuOffsetX, menuOffsetY, mc.thePlayer.inventoryContainer, InventoryCore.COMPATTOOLS);
            } else {
                menu = new MenuSlotGUI(element, menuOffsetX, menuOffsetY);
                if (mc.thePlayer.inventoryContainer.getInventory().stream().filter(st -> st != null).anyMatch(st -> InventoryCore.WEAPONS.isFine((ItemStack) st, true)))
                    menu.elements.add(new ButtonGUI(menu, Categories.WEAPONS, 0, 0, net.minecraft.util.text.translation.I18n.translateToLocal("guiWeapons"), IconCore.EQUIPMENT));
                if (mc.thePlayer.inventoryContainer.getInventory().stream().filter(st -> st != null).anyMatch(st -> InventoryCore.BOWS.isFine((ItemStack) st, true)))
                    menu.elements.add(new ButtonGUI(menu, Categories.BOWS, 0, 0, net.minecraft.util.text.translation.I18n.translateToLocal("guiBows"), IconCore.EQUIPMENT));
                if (mc.thePlayer.inventoryContainer.getInventory().stream().filter(st -> st != null).anyMatch(st -> InventoryCore.PICKAXE.isFine((ItemStack) st, true)))
                    menu.elements.add(new ButtonGUI(menu, Categories.PICKAXE, 0, 0, net.minecraft.util.text.translation.I18n.translateToLocal("guiPickaxes"), IconCore.EQUIPMENT));
                if (mc.thePlayer.inventoryContainer.getInventory().stream().filter(st -> st != null).anyMatch(st -> InventoryCore.AXE.isFine((ItemStack) st, true)))
                    menu.elements.add(new ButtonGUI(menu, Categories.AXE, 0, 0, net.minecraft.util.text.translation.I18n.translateToLocal("guiAxes"), IconCore.EQUIPMENT));
                if (mc.thePlayer.inventoryContainer.getInventory().stream().filter(st -> st != null).anyMatch(st -> InventoryCore.SHOVEL.isFine((ItemStack) st, true)))
                    menu.elements.add(new ButtonGUI(menu, Categories.SHOVEL, 0, 0, net.minecraft.util.text.translation.I18n.translateToLocal("guiShovels"), IconCore.EQUIPMENT));
                if (menu.elements.isEmpty()) menu.elements.add(new EmptySlot(menu, 0, 0));
            }
        } else if (id == Categories.WEAPONS) {
            menu = new InventoryGUI(element, menuOffsetX, menuOffsetY, mc.thePlayer.inventoryContainer, InventoryCore.WEAPONS);
        } else if (id == Categories.BOWS) {
            menu = new InventoryGUI(element, menuOffsetX, menuOffsetY, mc.thePlayer.inventoryContainer, InventoryCore.BOWS);
        } else if (id == Categories.PICKAXE) {
            menu = new InventoryGUI(element, menuOffsetX, menuOffsetY, mc.thePlayer.inventoryContainer, InventoryCore.PICKAXE);
        } else if (id == Categories.AXE) {
            menu = new InventoryGUI(element, menuOffsetX, menuOffsetY, mc.thePlayer.inventoryContainer, InventoryCore.AXE);
        } else if (id == Categories.SHOVEL) {
            menu = new InventoryGUI(element, menuOffsetX, menuOffsetY, mc.thePlayer.inventoryContainer, InventoryCore.SHOVEL);
        } else if (id == Categories.ARMOR) {
            menu = new InventoryGUI(element, menuOffsetX, menuOffsetY, mc.thePlayer.inventoryContainer, InventoryCore.EQUIPMENT);
        } else if (id == Categories.ACCESSORY) {
            if (InventoryCore.isBaublesLoaded())
                menu = new InventoryGUI(element, menuOffsetX, menuOffsetY, mc.thePlayer.inventoryContainer, InventoryCore.ACCESSORY);
            else menu.elements.add(new EmptySlot(menu, 0, 0));
        } else if (id == Categories.CONSUMABLES) {
            menu = new InventoryGUI(element, menuOffsetX, menuOffsetY, mc.thePlayer.inventoryContainer, InventoryCore.CONSUMABLES);
        }
        //Social
        else if (id == Categories.PARTY) {
            menu = new MenuSlotGUI(element, menuOffsetX, menuOffsetY);

            menu.elements.add(new PartyHandler(menu, Categories.INVITE_LIST, 0, 0, net.minecraft.util.text.translation.I18n.translateToLocal("guiInvite"), IconCore.INVITE));
            menu.elements.add(new PartyHandler(menu, Categories.DISSOLVE, 0, 0, net.minecraft.util.text.translation.I18n.translateToLocal("guiDissolve"), IconCore.CANCEL));

            sub = SubWindow.resetPartySub(mc, sub);
            info = SubWindow.addInfo(sub);

        } else if (id == Categories.FRIENDS) {
            setInfo(null, null);
            menu = new FriendList(mc, element, menuOffsetX, menuOffsetY);

            sub = SubWindow.resetFriendsSub(mc, sub);
            info = SubWindow.addInfo(sub);

            infoCaption = null;
            infoText = null;
        }
        //Social -> Party
        else if (id == Categories.INVITE_LIST) { // TODO: make all of these update in real-time (whole class needs probs massive rewrite)
            menu = new ListGUI(element, menuOffsetX, menuOffsetY);

            final MenuGUI mnu = menu;
            if (StaticPlayerHelper.listOnlinePlayers(mc) != null)
                StaticPlayerHelper.listOnlinePlayers(mc, true, 5).stream().map(StaticPlayerHelper::getName).forEach(name -> {
                    final ButtonGUI button = new ButtonState(mnu, Categories.INVITE_PLAYER, 0, 0, name, IconCore.INVITE, (mc1, button1) -> !PartyHelper.instance().isMember(button1.caption));
                    button.enabled = !PartyHelper.instance().isMember(name);
                    mnu.elements.add(button);
                });

        }
        //Social -> Friends
        else if ((id == Categories.FRIEND) && (element instanceof FriendCore)) {
            setInfo(null, null);
            if (((FriendCore) element).highlight) {
                System.out.println("Add friends menu request");
                menu = new MenuSlotGUI(element, menuOffsetX, menuOffsetY);
                menu.elements.add(new ButtonGUI(menu, Categories.POSITION_CHECK, 0, 0, net.minecraft.util.text.translation.I18n.translateToLocal("guiPositionCheck"), IconCore.FIELD_MAP));
                menu.elements.add(new ButtonGUI(menu, Categories.OTHER_PROFILE, 0, 0, net.minecraft.util.text.translation.I18n.translateToLocal("guiProfile"), IconCore.PARTY));
            } else {
                menu = null;
                System.out.println("Add friend request");
                FriendsHandler.instance().addFriendRequests(mc, ((FriendCore) element).caption);
            }
        } else if (id == Categories.OTHER_PROFILE && element.parent instanceof MenuGUI && ((MenuGUI) element.parent).parent instanceof FriendCore) {
            menu = null;

            final EntityPlayer player = StaticPlayerHelper.findOnlinePlayer(mc, ((FriendCore) ((MenuGUI) element.parent).parent).caption);

            if (player != null) {
                sub = SubWindow.resetProfileSub(mc, sub, player);
                info = SubWindow.addInfo(sub);

                infoCaption = null;
                infoText = null;

                final Strings[] profile = SubWindow.addProfileContent(player);

                setInfo(profile[0], profile[1]);
            } else setInfo(null, null);
        } else if (id == Categories.POSITION_CHECK && element.parent instanceof MenuGUI && ((MenuGUI) element.parent).parent instanceof FriendCore) {
            menu = null;

            final EntityPlayer player = StaticPlayerHelper.findOnlinePlayer(mc, ((FriendCore) ((MenuGUI) element.parent).parent).caption);

            if (player != null) {
                sub = SubWindow.resetCheckPositionSub(mc, sub, player, 1, null);
                info = SubWindow.addInfo(sub);

                final Strings[] position = SubWindow.addPositionContent(player, mc.thePlayer);

                setInfo(position[0], position[1]);
            } else setInfo(null, null);
        }
        //Navigation
        else if (id == Categories.QUESTS) {
            setInfo(null, null);
            menu = null;

            sub = SubWindow.resetQuestsSub(mc, sub, mc.thePlayer);
            info = SubWindow.addInfo(sub);
        } else if (id == Categories.FIELD_MAP) {
            menu = null;

            sub = SubWindow.resetCheckPositionSub(mc, sub, mc.thePlayer, 4, '-' + net.minecraft.util.text.translation.I18n.translateToLocal("guiFieldMap") + '-');
            info = SubWindow.addInfo(sub);

            final Strings[] position = SubWindow.addPositionContent(mc.thePlayer, mc.thePlayer);

            setInfo(position[0], position[1]);
        } else if (id == Categories.DUNGEON_MAP) {
            setInfo(null, null);
            menu = null;

            sub = SubWindow.resetCheckPositionSub(mc, sub, mc.thePlayer, 1, '-' + net.minecraft.util.text.translation.I18n.translateToLocal("guiDungMap") + '-');
            info = SubWindow.addInfo(sub);

            final Strings[] position = SubWindow.addPositionContent(mc.thePlayer, mc.thePlayer);

            setInfo(position[0], position[1]);
        }
        //Options
        else if (id == Categories.OPTIONS) {
            menu = new ListGUI(element, menuOffsetX, menuOffsetY);

            final MenuGUI mnu = menu;
            Stream.of(OptionCore.values()).filter(opt -> opt.category == null).forEach(option -> mnu.elements.add(new OptionButton(mnu, 0, 0, option)));
        } else if (id == Categories.OPT_CAT) {
            openOptCat = ((OptionButton) element).getOption();
            menu = new ListGUI(element, menuOffsetX, menuOffsetY);

            final MenuSlotGUI mnu = menu;
            Stream.of(OptionCore.values()).filter(opt -> opt.category == openOptCat).forEach(option -> mnu.elements.add((new OptionButton(mnu, 0, 0, option))));
        }
        //Misc
        if (sub != subMenu && subMenu != null) {
            menus.removeIf(entry -> entry.getValue() == subMenu);

            elements.remove(subMenu);
        }

        if (menu != null) {
            final List<Elements> list;

            list = element.parent != null && element.parent instanceof ContainerGUI ? ((ContainerGUI) element.parent).elements : elements;

            for (final Elements element0 : list) {
                if (element0.ID() == id) {
                    if (element0 instanceof ButtonGUI) {
                        if (id == Categories.OPT_CAT) {
                            OptionCore curr = openOptCat;
                            final OptionCore comp = OptionCore.fromString(((ButtonGUI) element0).caption);
                            while (curr == comp) {
                                ((ButtonGUI) element0).highlight = curr == openOptCat;
                                curr = curr.category;
                            }
                        } else ((ButtonGUI) element0).highlight = true;
                    } else if (element0 instanceof IconGUI) {
                        ((IconGUI) element0).highlight = true;
                    }
                } else element0.enabled = false;
            }

            openMenu(id, menu);

            if (sub != subMenu && sub != null) openMenu(id, sub);
        }

    }

    private void moveX(final int mode, final MenuGUI menu) {
        final int value = menu.x > 0 ? menu.x + menu.width : menu.x;

        jumpX += mode * value;
        flowX += mode * value;
    }

    private void openMenu(final Categories id, final MenuGUI menu) {
        moveX(+1, menu);

        menus.add(new Entry<Categories, MenuGUI>() {

            @Override
            public MenuGUI setValue(MenuGUI none) {
                return null;
            }

            @Override
            public MenuGUI getValue() {
                return menu;
            }

            @Override
            public Categories getKey() {
                return id;
            }

        });

        elements.add(menu);
    }

    private void closeMenu(Elements element, Categories id) {
        for (int i = menus.size() - 1; i >= 0; i--) {
            final Entry<Categories, MenuGUI> entry = menus.get(i);

            if (id != Categories.MENU)
                if ((entry.getKey().hasParent(id)) || (entry.getKey() == id)) {
                    if (entry.getValue().elements.contains(info)) {
                        info = null;
                        infoCaption = null;
                        infoText = null;
                    }

                    if (entry.getValue() == sub) sub = null;

                    moveX(-1, entry.getValue());

                    elements.remove(entry.getValue());
                    menus.remove(i);
                }

        }

        if (id != Categories.MENU)
            if (element != null) {
                final List<Elements> list;

                if (element.parent != null && element.parent instanceof ContainerGUI)
                    list = ((ContainerGUI) element.parent).elements;
                else list = elements;

                for (final Elements element0 : list) {
                    if (element0.ID() == id) {
                        if (element0 instanceof ButtonGUI) ((ButtonGUI) element0).highlight = false;
                        else if (element0 instanceof IconGUI) ((IconGUI) element0).highlight = false;
                    } else element0.enabled = true;
                }
            }

    }

    private void reloadList(Elements element, Categories id) {
        if (element != null) {
            final List<Elements> list;

            if (element.parent != null && element.parent instanceof ContainerGUI)
                list = ((ContainerGUI) element.parent).elements;
            else list = elements;

            for (final Elements element0 : list) {
                if (element0.ID() == id) {
                    if (((ButtonGUI) element0).highlight && !((OptionButton) element).getOption().getValue())
                        ((ButtonGUI) element0).highlight = false;
                } else element0.enabled = true;
            }
        }
    }
}
