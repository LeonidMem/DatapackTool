package ru.leonidm.datapacktool.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FileUtils {

    /**
     * Copies files
     * @param from File which will be copied
     * @param to Where to
     * @throws IOException
     */
    public static void copy(File from, File to) throws IOException {
        to.getParentFile().mkdirs();
        InputStream fromInputStream = new FileInputStream(from);

        to.delete();
        to.createNewFile();

        OutputStream toOutputStream = new FileOutputStream(to);

        toOutputStream.write(fromInputStream.readAllBytes());

        fromInputStream.close();
        toOutputStream.close();
    }

    /**
     * Copies files
     * @param directory Directory where located new file will be
     * @param name Name of the file to copy in the same directory
     * @throws IOException
     */
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

    /**
     * List all files recursively
     * @param directory Directory
     * @return List of the files
     */
    // TODO: optimize
    public static Set<File> listFilesRecursively(File directory) {
        Set<File> output = new HashSet<>();

        File[] files = directory.listFiles();
        if(files != null) {
            for(File f : files) {
                if (f.isDirectory()) output.addAll(listFilesRecursively(new File(directory, f.getName())));
                else output.add(f);
            }
        }
        return output;
    }

    /**
     * Deletes all files recursively
     * @param directory Directory
     */
    public static void deleteFilesRecursively(File directory) {
        File[] files = directory.listFiles();
        if(files != null) {
            for(File file : files) {
                if(file.isDirectory()) {
                    deleteFilesRecursively(file);
                    directory.delete();
                }
                else file.delete();
            }
        }
    }

}
