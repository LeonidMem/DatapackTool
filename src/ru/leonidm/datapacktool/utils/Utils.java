package ru.leonidm.datapacktool.utils;

import ru.leonidm.datapacktool.entities.Subcommand;
import ru.leonidm.datapacktool.managers.SubcommandManager;

import java.util.regex.Pattern;

public class Utils {
    private static final boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
    public static final String fileSeparator = isWindows() ? "\\" : "/";
    public static final String regexFileSeparator = Pattern.quote(fileSeparator);

    /**
     * @return True if current OS is Windows
     */
    public static boolean isWindows() {
        return isWindows;
    }

    /**
     * @return File separator in player's OS
     */
    public static String getFileSeparator() {
        return fileSeparator;
    }

    public static void printHelp() {
        System.out.println("DatapackTool's subcommands:");
        for (Subcommand command : SubcommandManager.getSubcommands()) {
            if (command.getInfo() == null) continue;
            System.out.println();
            System.out.println(command.getInfo());
        }
        System.out.println();
    }
}
