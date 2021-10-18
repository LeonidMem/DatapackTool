package ru.leonidm.datapacktool;

import ru.leonidm.datapacktool.entities.NativeCommand;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
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

    private static final char fileSeparator = isWindows() ? '\\': '/';

    public static char getFileSeparator() {
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
                if(file.isDirectory()) deleteDirectoryRecursively(file);
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

}
