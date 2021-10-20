package ru.leonidm.datapacktool.commands;

import ru.leonidm.datapacktool.utils.FileUtils;
import ru.leonidm.datapacktool.entities.NativeCommandExecutor;
import ru.leonidm.datapacktool.utils.Utils;

import java.io.*;
import java.util.List;
import java.util.Scanner;

public class Install implements NativeCommandExecutor {

    @Override
    public void run(List<String> args, List<String> keys) {
        if(args.size() != 1) {
            exit();
            return;
        }

        String path;
        File directory;
        try {
            path = args.get(0);
            if(!path.endsWith("/") && !path.endsWith("\\")) {
                path += Utils.getFileSeparator();
            }
            directory = new File(path);
            directory.mkdirs();
        } catch(Exception e) {
            e.printStackTrace();
            return;
        }

        boolean installed = false;

        try {
            if(Utils.isWindows()) {
                if(!Utils.isWindowsAdmin()) {
                    System.out.println("Run .bat as Administrator!");
                    return;
                }

                String outVariablePath = path.substring(0, path.length() - 1);
                String pathEnv = System.getenv("Path");
                if(!pathEnv.contains(outVariablePath)) {
                    pathEnv = pathEnv + ";" + outVariablePath;
                    pathEnv.replace(";;", ";");

                    System.out.println("Changing %Path%...");
                    Process p = Runtime.getRuntime().exec("cmd.exe /c REG ADD \"HKEY_LOCAL_MACHINE\\SYSTEM\\CurrentControlSet\\Control\\Session Manager\\Environment\" /v Path /t REG_SZ /d \"" + pathEnv + "\" /f");
                    p.waitFor();

                    printError(p);
                }

                String dtoolPath = System.getenv("DToolPath");
                if(dtoolPath == null || !dtoolPath.equals(outVariablePath)) {

                    if(dtoolPath != null) {
                        System.out.println("Removing old directory from %Path%...");
                        pathEnv = pathEnv.replaceFirst(dtoolPath.replace("\\", "\\\\"), "");
                        pathEnv.replace(";;", ";");

                        Process p = Runtime.getRuntime().exec("cmd.exe /c REG ADD \"HKEY_LOCAL_MACHINE\\SYSTEM\\CurrentControlSet\\Control\\Session Manager\\Environment\" /v Path /t REG_SZ /d \"" + pathEnv + "\" /f");
                        p.waitFor();

                        printError(p);
                    }

                    System.out.println("Changing %DToolPath%...");
                    Process p = Runtime.getRuntime().exec("cmd.exe /c REG ADD \"HKEY_LOCAL_MACHINE\\SYSTEM\\CurrentControlSet\\Control\\Session Manager\\Environment\" /v DToolPath /t REG_SZ /d \"" + directory.getAbsolutePath() + "\" /f");
                    p.waitFor();

                    printError(p);
                }
            }
            else {
                System.out.println("Changing $PATH...");
                Process p = Runtime.getRuntime().exec("export PATH=\"$HOME/bin:$PATH\"");
                p.waitFor();
            }

            System.out.println("Copying files...");
            FileUtils.copy(directory, "DatapackTool.jar");
            FileUtils.copy(directory, "dtool.bat");

            if(Utils.isWindows()) {
                System.out.println("Killing explorer.exe so variables will be refreshed...");
                Process p = Runtime.getRuntime().exec("taskkill /f /im explorer.exe");
                p.waitFor();

                System.out.println("Starting explorer.exe...");
                p = Runtime.getRuntime().exec("explorer.exe");
            }

            System.out.println("Installed successfully!");

            installed = true;

        } catch(Exception e) {
            e.printStackTrace();
        }

        if(!installed) {
            System.out.println("Something went wrong... Installation was cancelled");
        }
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nPress enter to continue...");
        scanner.nextLine();
    }

    @Override
    public String info() {
        return null;
    }

    private void printError(Process p) throws Exception {
        InputStream errorStream = p.getErrorStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(errorStream));

        String line;
        while((line = reader.readLine()) != null) {
            System.out.println(line);
        }
    }
}
