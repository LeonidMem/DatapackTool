package ru.leonidm.datapacktool.utils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class GitHubUtils {

    /**
     * Gets last file's commit ID from GitHub repository
     *
     * @param repository Name of the repository
     * @param fileName   Name of the file (with extension)
     * @return Commit ID or null if this file doesn't exist
     * @throws IOException
     */
    public static String getLastFileCommitID(String repository, String fileName) throws IOException {
        URL url = new URL("https://github.com/" + repository + "/contributors/main/" + fileName);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setDoOutput(true);

        connection.setRequestProperty("keepAlive", "false");

        int responseCode = connection.getResponseCode();
        if (responseCode != 200) return null;

        InputStream inputStream = connection.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        String commitID = null;

        String line;
        while ((line = reader.readLine()) != null) {
            if (line.strip().equals("<span>Latest commit</span>")) {
                commitID = reader.readLine().split(">", 2)[1].split("</a>")[0];
                break;
            }
        }

        return commitID;
    }

    /**
     * Download module from the GitHub
     *
     * @param repository Name of the repository
     * @param fileName   Name of the file (with extension)
     * @return File of the module or null if this file doesn't exist
     * @throws IOException
     */
    public static File getFile(String repository, String fileName) throws IOException {
        URL url = new URL("https://github.com/" + repository + "/raw/main/" + fileName);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setDoOutput(true);

        connection.setRequestProperty("keepAlive", "false");

        if (connection.getResponseCode() != 200) return null;

        InputStream inputStream = connection.getInputStream();

        File moduleFile = new File(System.getenv("DToolPath") + Utils.getFileSeparator()
                + "modules" + Utils.getFileSeparator() + fileName);

        if (moduleFile.exists()) moduleFile.delete();
        moduleFile.createNewFile();

        OutputStream outputStream = new FileOutputStream(moduleFile);
        outputStream.write(inputStream.readAllBytes());

        inputStream.close();
        outputStream.close();

        return moduleFile;
    }
}
