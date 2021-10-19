package ru.leonidm.datapacktool;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import ru.leonidm.datapacktool.commands.Build;
import ru.leonidm.datapacktool.entities.NativeCommand;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class Utils {

    private static final boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");

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

    private static final String fileSeparator = isWindows() ? "\\": "/";
    private static final String regexFileSeparator = fileSeparator.replace("\\", "\\\\");

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

    public static void copy(File from, File to) throws IOException {
        InputStream fromInputStream = new FileInputStream(from);

        to.delete();
        to.createNewFile();

        OutputStream toOutputStream = new FileOutputStream(to);

        toOutputStream.write(fromInputStream.readAllBytes());

        fromInputStream.close();
        toOutputStream.close();
    }

    public static void copy(File directory, String name) throws IOException {
        File resourceFile = new File(name);
        InputStream resourceInputStream = new FileInputStream(resourceFile);

        File outFile = new File(directory, name);
        if(outFile.exists()) {
            outFile.delete();
        }
        outFile = new File(directory, name);
        outFile.createNewFile();

        OutputStream resourceOutputStream = new FileOutputStream(outFile);
        resourceOutputStream.write(resourceInputStream.readAllBytes());

        resourceInputStream.close();
        resourceOutputStream.close();
    }

    public static void copyFromResource(File directory, String name) throws IOException {
        InputStream resourceInputStream = DatapackTool.class.getResourceAsStream(name);

        File outFile = new File(directory, name);
        outFile.createNewFile();

        OutputStream resourceOutputStream = new FileOutputStream(outFile);
        resourceOutputStream.write(resourceInputStream.readAllBytes());

        resourceInputStream.close();
        resourceOutputStream.close();
    }

    public static List<File> listFilesRecursively(File directory) {
        List<File> output = new ArrayList<>();

        File[] files = directory.listFiles();
        if(files != null) {
            for(File f : files) {
                if (f.isDirectory()) output.addAll(listFilesRecursively(new File(directory, f.getName())));
                else output.add(f);
            }
        }
        return output;
    }

    public static void deleteDirectoryRecursively(File directory) {
        File[] files = directory.listFiles();
        if(files != null) {
            for(File file : files) {
                if(file.isDirectory()) {
                    deleteDirectoryRecursively(file);
                    directory.delete();
                }
                else file.delete();
            }
        }
    }

    public static File getFileFromGitHub(String repository, String fileName) throws IOException {
        URL url = new URL("https://github.com/" + repository + "/raw/main/" + fileName);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setDoOutput(true);

        connection.setRequestProperty("keepAlive", "false");

        if(connection.getResponseCode() != 200) return null;

        InputStream inputStream = connection.getInputStream();

        File moduleFile = new File(System.getenv("DToolPath") + Utils.getFileSeparator()
                + "modules" + Utils.getFileSeparator() + fileName);

        if(moduleFile.exists()) moduleFile.delete();
        moduleFile.createNewFile();

        OutputStream outputStream = new FileOutputStream(moduleFile);
        outputStream.write(inputStream.readAllBytes());

        inputStream.close();
        outputStream.close();

        return moduleFile;
    }

    public static String getFunctionMinecraftFormattedName(File file) {
        String namespace;
        File outDirectory = file;
        while(true) {
            if(outDirectory.getParentFile().getName().equalsIgnoreCase("data")) {
                namespace = outDirectory.getName();
                break;
            }
            outDirectory = outDirectory.getParentFile();
        }

        String regex2 = fileSeparator + "data" + fileSeparator + namespace + fileSeparator + "functions";

        String[] split = file.getParent().split(regex2, 2);
        String directory;
        if(split.length != 2) directory = "";
        else {
            directory = split[1].replace("\\", "/");
            if(directory.startsWith("/")) directory = directory.substring(1);
            if(!directory.equals("")) directory += "/";
        }

        return namespace + ":" + directory + file.getName().substring(0, file.getName().length() - 11);
    }

    public static void addValueToTag(File outFile, String minecraftFormattedTag, String category, String value) throws Exception {
        String[] splitTag = minecraftFormattedTag.split(":");
        if(splitTag.length != 2) throw new Exception("Illegal tag!");

        File directory = outFile;
        while(!(directory = directory.getParentFile()).getName().equalsIgnoreCase("data"));

        File tagFile = new File(directory, splitTag[0] + fileSeparator + "tags" + fileSeparator + category +
                fileSeparator + splitTag[1].replace("/", fileSeparator) + ".json");

        tagFile.getParentFile().mkdirs();
        if(tagFile.createNewFile()) {
            OutputStream outputStream = new FileOutputStream(tagFile);
            outputStream.write(("{\"values\":[\"" + value + "\"]}").getBytes(StandardCharsets.UTF_8));
            outputStream.close();
            return;
        }

        String content = Files.readString(tagFile.toPath());

        JSONObject jsonObject = (JSONObject) JSONValue.parse(content);
        JSONArray values = (JSONArray) jsonObject.get("values");

        String outValue = Utils.getFunctionMinecraftFormattedName(outFile);
        if(values.contains(outValue)) return;

        tagFile.delete();
        tagFile.createNewFile();

        OutputStream outputStream = new FileOutputStream(tagFile);
        outputStream.write(jsonObject.toJSONString().getBytes(StandardCharsets.UTF_8));
        outputStream.close();
    }

    public static String createAnonymousFunction(File outFile, String anonymousFunctionContent) throws IOException {

        File anonymousFile = new File(outFile.getParentFile(), outFile.getName().substring(0, outFile.getName().length() - 11) + anonymousFunctionContent.hashCode() + ".mcfunction");
        OutputStream outputStream = new FileOutputStream(anonymousFile);
        outputStream.write(anonymousFunctionContent.getBytes(StandardCharsets.UTF_8));
        outputStream.close();

        Build.addAnonymousFunction(anonymousFile);

        return Utils.getFunctionMinecraftFormattedName(anonymousFile);
    }
}
