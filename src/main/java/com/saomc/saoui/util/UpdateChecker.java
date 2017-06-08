package com.saomc.saoui.util;

import com.google.common.collect.Lists;
import com.saomc.saoui.SAOCore;
import com.saomc.saoui.config.ConfigHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

@Deprecated // For the time being
public class UpdateChecker extends Thread {

    private static final String curseURL = "http://minecraft.curseforge.com/projects/sao-ui/files";
    private static final String changelogURL = "https://raw.githubusercontent.com/Tencao/SAO-UI---1.10.2/master/Changelog.txt";
    private static boolean hasChecked; // This will check for any potential update, and flag true when the update checker has checked
    private static boolean hasUpdate;  // This will mark true when an update has been found
    private static List<String> changes; // This will contain a changelog from the update checker, to be used on the StartupGUI


    /*public static boolean firstRun(){ // This will check in the config if this is the first time the client has been run with this mod, and if so, send a call to initiate the first time setup GUI

    }*/

    public static boolean hasChecked() {
        return hasChecked;
    }

    public static boolean hasUpdate() {
        return hasUpdate;
    }

    public static List<String> fetchChangeLog() {
        return changes;
    }

    @Override
    public void run() {
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        if (!hasChecked) try {
            connection = (HttpURLConnection) new URL(changelogURL).openConnection();

            connection.connect();

            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String line = reader.readLine();

            if (line == null) {
                SAOCore.LOGGER.fatal("Update check failed!");
                throw new IOException("No data from github changelog!");
            }

            String latestVersion;
            changes = Lists.newArrayList();

            latestVersion = line.substring(11);
            latestVersion = latestVersion.trim();

            //Checks to see if this update has already been found before, and if so, check to see if the user opted to ignore this update
            if (ConfigHandler.getLastVersion().equals(latestVersion)) {
                if (ConfigHandler.ignoreVersion()) {
                    hasChecked = true;
                    return;
                }
            }

            //Reset ignore update if the recent update is not the same as the last update
            if (!ConfigHandler.getLastVersion().equals(latestVersion)) {
                ConfigHandler.saveVersion(latestVersion);
                if (ConfigHandler.ignoreVersion()) {
                    ConfigHandler.setIgnoreVersion(false);
                }
            }

            changes.add("A new update has been found" +
                    "\n \n" +
                    "Changelog:");

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("###Version")) {
                    break;
                }

                if (!line.isEmpty()) {
                    line = line.substring(1).trim();
                    changes.add(line);
                }
            }

            changes.add("\n \n " +
                    "To download, visit " + curseURL);

            if (!SAOCore.VERSION.equals(latestVersion)) {
                hasUpdate = true;
            }

        } catch (Exception e) {
            SAOCore.LOGGER.fatal("Caught exception in Update Checker thread!");
            e.printStackTrace();

        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    SAOCore.LOGGER.fatal("Caught exception in Update Checker");
                    e.printStackTrace();
                }
            }

            if (connection != null)
                connection.disconnect();

            hasChecked = true;
        }
    }

}
