package com.saomc.saoui.social.friends;

import com.saomc.saoui.communication.CommandType;
import com.saomc.saoui.communication.Communicator;
import com.saomc.saoui.events.ConfigHandler;
import com.saomc.saoui.social.StaticPlayerHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Part of SAOUI
 *
 * @author Bluexin
 */
public class FriendsHandler {

    private static FriendsHandler instance;
    private final File friendsFile;
    private final List<FriendRequest> friendRequests = new ArrayList<>();
    private String[] friends;

    private FriendsHandler(FMLPreInitializationEvent event) {
        this.friendsFile = new File(Minecraft.getMinecraft().mcDataDir.getAbsolutePath() + "/saouifriends");
        if (!friendsFile.exists()) writeFriends(friends);
        friends = loadFriends();
    }

    public static FriendsHandler instance() {
        return instance;
    }

    public static void preInit(FMLPreInitializationEvent event) {
        instance = new FriendsHandler(event);
    }

    public String[] loadFriends() {
        try {
            final FileInputStream stream = new FileInputStream(friendsFile);
            final String[] friends;

            if (stream.available() != 0) {
                final int count = (stream.read() & 0xFF);

                friends = new String[count];

                for (int i = 0; i < count; i++) {
                    final int length = (stream.read() & 0xFF);
                    final byte[] bytes = new byte[length];

                    stream.read(bytes, 0, length);

                    friends[i] = new String(bytes);
                }
            } else friends = new String[0];

            stream.close();

            return friends;
        } catch (IOException e) {
            return new String[0];
        }
    }

    public String[] listFriends() {
        if (friends == null) friends = loadFriends();

        return friends;
    }

    public void addFriendRequests(String... names) {
        synchronized (friendRequests) {
            final Minecraft mc = Minecraft.getMinecraft();
            for (final String name : names)
                if (!friendRequests.contains(new FriendRequest(name, 10000)) && !isFriend(name)) {
                    friendRequests.add(new FriendRequest(name, 10000));
                    Communicator.INSTANCE.send(CommandType.ADD_FRIEND_REQUEST, StaticPlayerHelper.findOnlinePlayer(mc, name));
                }
        }
    }

    public boolean addFriends(EntityPlayer... players) {
        friends = listFriends();
        final ArrayList<String> newNames = new ArrayList<>();

        Stream.of(players).map(EntityPlayer::getDisplayNameString).forEach(player -> {
            if (Stream.of(friends).noneMatch(friend -> friend.equals(player))) newNames.add(player);
        });

        String[] bb = new String[newNames.size()];
        System.arraycopy(newNames.toArray(new String[0]), 0, bb, 0, bb.length);
        return newNames.size() <= 0 || addRawFriends(bb);
    }

    public boolean isFriend(String name) {
        return Stream.of(listFriends()).anyMatch(friend -> friend.equals(name));
    }

    public boolean isFriend(EntityPlayer player) {
        return isFriend(StaticPlayerHelper.getName(player));
    }

    public boolean addRawFriends(String[] names) {
        friends = listFriends();

        final String[] resized = new String[friends.length + names.length];

        System.arraycopy(friends, 0, resized, 0, friends.length);
        System.arraycopy(names, 0, resized, friends.length, names.length);

        if (writeFriends(resized)) {
            friends = resized;
            return true;
        } else return false;
    }

    public boolean writeFriends(String[] friends) {
        final String[] data = friends == null ? new String[0] : friends;

        synchronized (friendsFile) {
            try (FileOutputStream stream = new FileOutputStream(friendsFile)) {
                final int count = (data.length % 0x100);
                stream.write(count);

                for (int i = 0; i < count; i++) {
                    final byte[] bytes = data[i].getBytes();
                    final int length = (bytes.length % 0x100);

                    stream.write(length);
                    stream.write(bytes, 0, length);
                }

                stream.flush();
                stream.close();

                return true;
            } catch (IOException e) {
                if (ConfigHandler.DEBUG) e.printStackTrace();

                return false;
            }
        }
    }

    public void acceptAddFriend(EntityPlayer player) {
        synchronized (friendRequests) {
            int index = -1;

            for (int i = 0; i < friendRequests.size(); i++)
                if (friendRequests.get(i).equals(player.getDisplayNameString())) {
                    index = i;
                    break;
                }

            if (index >= 0 && (isFriend(player) || addFriends(player))) friendRequests.remove(index);
        }
    }

    public void cancelAddFriend(EntityPlayer player) {
        synchronized (friendRequests) {
            int index = -1;

            for (int i = 0; i < friendRequests.size(); i++)
                if (friendRequests.get(i).equals(player.getDisplayNameString())) {
                    index = i;
                    break;
                }

            if (index >= 0) friendRequests.remove(index);
        }
    }

    public void addFriendRequest(EntityPlayer player) {
        if (!FriendsHandler.instance().isFriend(player)) {
            final Minecraft mc = Minecraft.getMinecraft();
            final GuiScreen keepScreen = mc.currentScreen;
            final boolean ingameFocus = mc.inGameHasFocus;

            final String text = I18n.format("friend.request.text", player.getDisplayNameString());
/*
            mc.displayGuiScreen(WindowView.viewConfirm(ConfigHandler._FRIEND_REQUEST_TITLE, text, (element, action, data) -> {
                final Categories id = element.ID();

                if (id == Categories.CONFIRM && (FriendsHandler.instance().isFriend(player) || FriendsHandler.instance().addFriends(player)))
                    Communicator.send(CommandType.ACCEPT_ADD_FRIEND, player);
                else Communicator.send(CommandType.CANCEL_ADD_FRIEND, player);

                mc.displayGuiScreen(keepScreen);

                if (ingameFocus) mc.setIngameFocus();
                else mc.setIngameNotInFocus();
            }));*/

            if (ingameFocus) mc.setIngameNotInFocus();
        } else Communicator.INSTANCE.send(CommandType.ACCEPT_ADD_FRIEND, player);
    }
}
