package ru.leonidm.datapacktool;

import org.jetbrains.annotations.NotNull;
import ru.leonidm.datapacktool.configs.IniConfig;
import ru.leonidm.datapacktool.utils.Utils;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.function.Predicate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ModuleLoader {

    private static final Map<String, ModuleInfo> modules = new HashMap<>();

    public static void loadModules(boolean log) throws Exception {
        loadModules(log, file -> true);
    }

    public static void loadModules(boolean log, Predicate<File> loadPredicate) throws Exception {
        File modulesDirectory = new File(System.getenv("DToolPath") + Utils.getFileSeparator() + "modules");
        File[] modulesFiles = modulesDirectory.listFiles();

        if (modulesFiles != null) {
            for (File moduleFile : modulesFiles) {
                if (!moduleFile.getName().endsWith(".jar") || !loadPredicate.test(moduleFile)) continue;
                loadModule(moduleFile, log);
            }
        }
    }

    public static void unloadModules() {
        modules.clear();
    }

    public static ModuleInfo loadModule(File moduleFile, boolean log) throws Exception {
        if (log) System.out.println("\nTrying to load " + moduleFile.getName() + "...");

        ZipFile zipFile = new ZipFile(moduleFile);
        ZipEntry zipEntry = zipFile.getEntry("module-info.ini");
        if (zipEntry == null) {
            if (log)
                System.err.println("Module " + moduleFile.getName() + " doesn't have \"module-info.ini\"! Skipping it...");
            return null;
        }

        IniConfig iniConfig = new IniConfig(zipFile.getInputStream(zipEntry));

        String name = iniConfig.get("name");
        String version = iniConfig.get("version");
        if (name == null || version == null) {
            if (log)
                System.err.println("Module " + moduleFile.getName() + " doesn't have defined \"name\" or \"version\" in \"module-info.ini\"! Skipping it...");
            return null;
        }

        if (modules.containsKey(name)) {
            if (log)
                System.err.println("Module \"" + name + "\" can't be loaded, because there is module with the same name! Skipping it...");
            return null;
        }

        URLClassLoader classLoader = new URLClassLoader(
                new URL[]{moduleFile.toURI().toURL()},
                DatapackTool.class.getClassLoader()
        );

        try {
            Class<?> classToLoad = Class.forName(iniConfig.get("main"), true, classLoader);
            Object instance = classToLoad.getConstructor().newInstance();

            Method onEnable = classToLoad.getDeclaredMethod("onEnable");
            onEnable.invoke(instance);

            Method info = classToLoad.getDeclaredMethod("info");
            Object result = info.invoke(instance);

            ModuleInfo moduleInfo = new ModuleInfo(name, version, (String) result);
            modules.put(name.toLowerCase(), moduleInfo);

            if (log) System.out.println("Loaded " + name + "!");

            return moduleInfo;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<ModuleInfo> getModules() {
        return new ArrayList<>(modules.values());
    }

    public static ModuleInfo getModule(String name) {
        return modules.get(name.toLowerCase());
    }

    public static class ModuleInfo {

        private final String name;
        private final String version;
        private final String info;

        private ModuleInfo(@NotNull String name, @NotNull String version, @NotNull String info) {
            this.name = name;
            this.version = version;
            this.info = info;
        }

        @NotNull
        public String getName() {
            return name;
        }

        @NotNull
        public String getVersion() {
            return version;
        }

        @NotNull
        public String getInfo() {
            return info;
        }

        @Override
        public String toString() {
            return "ModuleInfo{" +
                    "name='" + name + '\'' +
                    ", version='" + version + '\'' +
                    '}';
        }
    }
}
