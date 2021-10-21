package ru.leonidm.datapacktool.commands;

import ru.leonidm.datapacktool.configs.MainConfig;
import ru.leonidm.datapacktool.ModuleLoader;
import ru.leonidm.datapacktool.utils.FileUtils;
import ru.leonidm.datapacktool.entities.NativeCommandExecutor;
import ru.leonidm.datapacktool.entities.McFunction;
import ru.leonidm.datapacktool.events.FilesParsedEvent;
import ru.leonidm.datapacktool.managers.EventManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Build implements NativeCommandExecutor {

    private static final List<File> anonymousFunctions = new ArrayList<>();

    public static void addAnonymousFunction(File anonymousFunction) {
        anonymousFunctions.add(anonymousFunction);
    }

    @Override
    public void run(List<String> args, List<String> keys) {
        if(args.size() > 1) {
            exit();
            return;
        }

        String id;
        if(args.size() == 0) {
            id = MainConfig.get("last_build_id");
            if(id == null) {
                System.out.println("You can't build without specifying an ID at the first time!");
                return;
            }
        }
        else {
            id = args.get(0);
            MainConfig.add("last_build_id", id);
            MainConfig.save();
        }

        long startMillis = new Date().getTime();

        System.out.println("{Phase [1/3]} Loading the modules...");
        try {
            ModuleLoader.loadModules(true);
        } catch(Exception e) {
            System.out.println("\nError occurred! Building was cancelled!");
            e.printStackTrace();
        }

        System.out.println("\n{Phase [2/3]} Getting information from the config...}\n");

        String inPath = MainConfig.get(id + "_in");
        String outPath = MainConfig.get(id + "_out");

        FileUtils.deleteDirectoryRecursively(new File(outPath));

        if(inPath == null || outPath == null) {
            System.out.println("Incorrect ID \"" + id + "\"!");
            return;
        }

        System.out.println("{Phase [3/3]} Parsing...}\n");
        List<File> files = FileUtils.listFilesRecursively(new File(inPath));

        List<File> jsonFiles = new ArrayList<>();
        List<File> functionsFiles = new ArrayList<>();

        for(File file : new ArrayList<>(files)) {
            if(file.getName().endsWith(".json")) {
                jsonFiles.add(file);
                files.remove(file);
                continue;
            }

            if(file.getName().endsWith(".mcfunction")) {
                functionsFiles.add(file);
                files.remove(file);
                continue;
            }
        }

        try {
            for(File jsonFile : jsonFiles) {
                FileUtils.copy(jsonFile, new File(jsonFile.getAbsolutePath().replace(inPath, outPath)));
            }

            parseFiles(inPath, outPath, functionsFiles);

            for(File file : files) {
                FileUtils.copy(file, new File(file.getAbsolutePath().replace(inPath, outPath)));
            }

            FilesParsedEvent filesParsedEvent = new FilesParsedEvent();
            EventManager.callEvent(filesParsedEvent);

            if(filesParsedEvent.isParseNewAnonymous()) parseFiles(inPath, outPath, anonymousFunctions);

        } catch(Exception e) {
            System.out.println("\n" + e.getMessage());
            if(keys.contains("-debug")) e.printStackTrace();
            System.out.println("\nError occurred! Building was cancelled!");
            return;
        }

        long endMillis = new Date().getTime();

        System.out.println("\n\n=====\nSuccessfully built in " + (double) (endMillis - startMillis) / 1000 + " sec.!");
    }

    public void parseFiles(String inPath, String outPath, List<File> files) throws Exception {
        for(File file : new ArrayList<>(files)) {
            File outFile = new File(file.getAbsolutePath().replace(inPath, outPath));

            McFunction mcFunction = new McFunction(file, outFile);
            mcFunction.parse();

            files.remove(file);
            parseFiles(inPath, outPath, anonymousFunctions);
        }
    }

    @Override
    public String info() {
        return "  build [id] - build project";
    }
}

