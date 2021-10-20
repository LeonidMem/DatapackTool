package ru.leonidm.datapacktool.utils;

import ru.leonidm.datapacktool.entities.NativeCommand;

public class Utils {
    private static final boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
    static final String fileSeparator = isWindows() ? "\\": "/";
    private static final String regexFileSeparator = fileSeparator.replace("\\", "\\\\");

    public static boolean isWindows() {
        return isWindows;
    }

    public static boolean isWindowsAdmin() {
        String[] groups = (new com.sun.security.auth.module.NTSystem()).getGroupIDs();
        for(String group : groups) {
            if(group.equals("S-1-5-32-544")) {
                return true;
            }
        }
        return false;
    }

    public static String getFileSeparator() {
        return fileSeparator;
    }

    public static void printHelp() {
        System.out.println("DatapackTool's subcommands:");
        for(NativeCommand command : NativeCommand.values()) {
            if(command.getInfo() == null) continue;
            System.out.println();
            System.out.println(command.getInfo());
        }
    }
}
