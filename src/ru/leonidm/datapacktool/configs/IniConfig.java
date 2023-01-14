package ru.leonidm.datapacktool.configs;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class IniConfig {

    private final Map<String, String> config = new HashMap<>();
    private final String path;

    public IniConfig(String path) throws Exception {
        this(new File(path));
    }

    public IniConfig(File file) throws Exception {
        this.path = file.getAbsolutePath();
        if (!file.exists()) {
            file.createNewFile();
        } else {
            String content = Files.readString(Paths.get(path));

            for (String line : content.split("\n")) {
                String[] split = line.split("=", 2);
                if (split.length != 2) continue;

                config.put(split[0].strip().toLowerCase(), split[1].strip());
            }
        }
    }

    public IniConfig(InputStream inputStream) throws Exception {
        this.path = null;

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        String line;
        while ((line = reader.readLine()) != null) {
            String[] split = line.split("=", 2);
            if (split.length != 2) continue;

            config.put(split[0].strip().toLowerCase(), split[1].strip());
        }
    }

    public String get(String key) {
        return config.get(key.toLowerCase());
    }

    public void set(String key, String value) {
        if (value == null) config.remove(key.toLowerCase());
        else config.put(key.toLowerCase(), value);
    }

    public void remove(String key) {
        config.remove(key);
    }

    public void clear() {
        config.clear();
    }

    public void save() {
        try {
            StringBuilder sb = new StringBuilder();

            for (Map.Entry<String, String> entry : config.entrySet()) {
                sb.append(entry.getKey()).append('=').append(entry.getValue()).append('\n');
            }

            Files.writeString(Paths.get(path), sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Set<Map.Entry<String, String>> getEntries() {
        return config.entrySet();
    }
}
