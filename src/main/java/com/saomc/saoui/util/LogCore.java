package com.saomc.saoui.util;

import com.saomc.saoui.SAOCore;
import com.saomc.saoui.config.OptionCore;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

public class LogCore {

    private static Logger logger = LogManager.getLogger(SAOCore.MODID);

    public static void log(Level level, String msg) {
        logger.log(level, msg);
    }

    public static void logInfo(String msg) {
        logger.info(msg);
    }

    public static void logWarn(String msg) {
        logger.warn(msg);
    }

    public static void logFatal(String msg) {
        logger.fatal(msg);
    }

    public static void logDebug(String msg) {
        if (OptionCore.DEBUG_MODE.isEnabled()) // visible in main console
            logger.info(msg);
        else logger.debug(msg); // fml log
    }

    public static void log(Throwable e) {
        Writer w = new StringWriter();
        e.printStackTrace(new PrintWriter(w));
        logFatal(w.toString());
    }
}
