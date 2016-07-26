package com.saomc.events;

import com.saomc.resources.StringNames;
import com.saomc.util.OptionCore;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.util.stream.Stream;

/**
 * Part of SAOUI
 *
 * @author Bluexin
 */
public class ConfigHandler {
    public static String _PARTY_DISSOLVING_TITLE; // TODO: Change it! This is not pretty. AT ALL!
    public static String _PARTY_DISSOLVING_TEXT;
    public static String _PARTY_LEAVING_TITLE;
    public static String _PARTY_LEAVING_TEXT;
    public static String _MESSAGE_TITLE;
    public static String _MESSAGE_FROM;
    public static String _FRIEND_REQUEST_TITLE;
    public static String _FRIEND_REQUEST_TEXT;
    public static String _PARTY_INVITATION_TITLE;
    public static String _PARTY_INVITATION_TEXT;
    public static String _DEAD_ALERT;
    public static String _LAST_UPDATE;
    public static boolean _IGNORE_UPATE;
    public static boolean DEBUG = false;
    private static Configuration config;

    public static void preInit(FMLPreInitializationEvent event) {
        config = new Configuration(event.getSuggestedConfigurationFile());
        config.load();

        DEBUG = config.get(Configuration.CATEGORY_GENERAL, "debug", DEBUG).getBoolean();

        _FRIEND_REQUEST_TITLE = config.get(Configuration.CATEGORY_GENERAL, "friend.request.title", StringNames.FRIEND_REQUEST_TITLE).getString();
        _FRIEND_REQUEST_TEXT = config.get(Configuration.CATEGORY_GENERAL, "friend.request.text", StringNames.FRIEND_REQUEST_TEXT).getString();

        _PARTY_INVITATION_TITLE = config.get(Configuration.CATEGORY_GENERAL, "party.invitation.title", StringNames.PARTY_INVITATION_TITLE).getString();
        _PARTY_INVITATION_TEXT = config.get(Configuration.CATEGORY_GENERAL, "party.invitation.text", StringNames.PARTY_INVITATION_TEXT).getString();

        _PARTY_DISSOLVING_TITLE = config.get(Configuration.CATEGORY_GENERAL, "party.dissolving.title", StringNames.PARTY_DISSOLVING_TITLE).getString();
        _PARTY_DISSOLVING_TEXT = config.get(Configuration.CATEGORY_GENERAL, "party.dissolving.text", StringNames.PARTY_DISSOLVING_TEXT).getString();

        _PARTY_LEAVING_TITLE = config.get(Configuration.CATEGORY_GENERAL, "party.leaving.title", StringNames.PARTY_LEAVING_TITLE).getString();
        _PARTY_LEAVING_TEXT = config.get(Configuration.CATEGORY_GENERAL, "party.leaving.text", StringNames.PARTY_LEAVING_TEXT).getString();

        _MESSAGE_TITLE = config.get(Configuration.CATEGORY_GENERAL, "message.title", StringNames.MESSAGE_TITLE).getString();
        _MESSAGE_FROM = config.get(Configuration.CATEGORY_GENERAL, "message.from", StringNames.MESSAGE_FROM).getString();

        _DEAD_ALERT = config.get(Configuration.CATEGORY_GENERAL, "dead.alert", StringNames.DEAD_ALERT).getString();

        _LAST_UPDATE = config.get(Configuration.CATEGORY_GENERAL, "last.update", "nothing").getString();
        _IGNORE_UPATE = config.get(Configuration.CATEGORY_GENERAL, "ignore.update", false).getBoolean();

        Stream.of(OptionCore.values()).forEach(option -> {
            if (config.get(Configuration.CATEGORY_GENERAL, "option." + option.name().toLowerCase(), option.getValue()).getBoolean())
                option.enable();
            else option.disable();
        });

        config.save();
    }

    public static void setOption(OptionCore option) {
        config.get(Configuration.CATEGORY_GENERAL, "option." + option.name().toLowerCase(), option.getValue()).set(option.getValue());
        saveAllOptions();
    }

    private static void saveAllOptions() {
        config.save();
    }

    public static void saveVersion(String version) {
        config.get(Configuration.CATEGORY_GENERAL, "last.update", getLastVersion()).set(version);
        config.save();
    }

    public static void setIgnoreVersion(boolean value) {
        config.get(Configuration.CATEGORY_GENERAL, "ignore.update", ignoreVersion()).set(value);
        config.save();
    }

    public static String getLastVersion() {
        return _LAST_UPDATE;
    }

    public static boolean ignoreVersion() {
        return _IGNORE_UPATE;
    }
}
