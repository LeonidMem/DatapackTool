package ru.leonidm.datapacktool.configs;

import ru.leonidm.datapacktool.Utils;

import java.util.Map;
import java.util.Set;

public class MainConfig {

    private static final IniConfig instance;
    private static final String configPath = System.getenv("DToolPath") + Utils.getFileSeparator() + "dtool.ini";

    static {
        IniConfig outInstance;
        try {
            outInstance = new IniConfig(configPath);
        } catch (Exception e) {
            outInstance = null;
            e.printStackTrace();
        }
        instance = outInstance;
    }

    public static String get(String key) {
        return instance.get(key);
    }

    public static void add(String key, String value) {
        instance.add(key, value);
    }

    public static void remove(String key) {
        instance.remove(key);
    }

    public static void clear() {
        instance.clear();
    }

    public static void save() {
        instance.save();
    }

    public static Set<Map.Entry<String, String>> getEntries() {
        return instance.getEntries();
    }
}
