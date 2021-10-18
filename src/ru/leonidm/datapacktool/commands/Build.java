package ru.leonidm.datapacktool.commands;

import ru.leonidm.datapacktool.configs.MainConfig;
import ru.leonidm.datapacktool.ModuleLoader;
import ru.leonidm.datapacktool.Utils;
import ru.leonidm.datapacktool.entities.NativeCommandExecutor;
import ru.leonidm.datapacktool.entities.McFunction;

import java.io.File;
import java.util.Date;
import java.util.List;

public class Build implements NativeCommandExecutor {

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

        Utils.deleteDirectoryRecursively(new File(outPath));

        if(inPath == null || outPath == null) {
            System.out.println("Incorrect ID \"" + id + "\"!");
            return;
        }

        System.out.println("{Phase [3/3]} Parsing...}\n");
        List<File> files = Utils.listFilesRecursively(new File(inPath));

        try{
            parseFiles(inPath, outPath, files);
        } catch(Exception e) {
            System.out.println("\n" + e.getMessage());
            System.out.println("\nError occurred! Building was cancelled!");
            return;
        }

        long endMillis = new Date().getTime();

        System.out.println("\n\n=====\nSuccessfully built in " + (double) (endMillis - startMillis) / 1000 + " sec.!");
    }

    public void parseFiles(String inPath, String outPath, List<File> files) throws Exception {
        for(File file : files) {
            File outFile = new File(file.getAbsolutePath().replace(inPath, outPath));

            McFunction mcFunction = new McFunction(file, outFile);
            mcFunction.parse();

            parseFiles(inPath, outPath, mcFunction.getAnonymousFiles());
        }
    }

    @Override
    public String info() {
        return "  build [id] - build project";
    }
}

