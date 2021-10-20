package ru.leonidm.datapacktool.utils;

import ru.leonidm.datapacktool.DatapackTool;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {

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

}
