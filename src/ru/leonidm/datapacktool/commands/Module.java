package ru.leonidm.datapacktool.commands;

import ru.leonidm.datapacktool.ModuleLoader;
import ru.leonidm.datapacktool.Utils;
import ru.leonidm.datapacktool.entities.NativeCommandExecutor;

import java.io.File;
import java.util.List;

public class Module implements NativeCommandExecutor {

    @Override
    public void run(List<String> args, List<String> keys) {
        if(args.size() == 0) {
            exit();
            return;
        }

        try {
            switch(args.get(0)) {
                case "load":
                    ModuleLoader.loadModules(true);
                    break;

                case "list":
                    ModuleLoader.loadModules(keys.contains("-debug"));

                    System.out.println("Installed modules:");
                    for (ModuleLoader.ModuleInfo moduleInfo : ModuleLoader.getModules()) {
                        System.out.println("- " + moduleInfo.getName() + " v" + moduleInfo.getVersion());
                    }
                    break;

                case "info":
                    if(args.size() != 2) {
                        exit();
                        return;
                    }

                    ModuleLoader.loadModules(keys.contains("-debug"));
                    ModuleLoader.ModuleInfo moduleInfo = ModuleLoader.getModule(args.get(1));
                    if(moduleInfo == null) {
                        System.out.println("Unknown module!");
                        return;
                    }

                    System.out.println("Name: " + moduleInfo.getName());
                    System.out.println("Version: " + moduleInfo.getVersion());
                    System.out.println("Info: " + moduleInfo.getInfo());
                    break;

                case "download":
                    if(args.size() == 1 || args.size() > 3) {
                        exit();
                        return;
                    }

                    String repository;
                    String moduleName;

                    if(args.size() == 2) {
                        repository = "LeonidMem/DatapackTool-Modules";
                        moduleName = args.get(1);
                    }
                    else {
                        repository = args.get(1);
                        moduleName = args.get(2);
                    }
                    if(!moduleName.endsWith(".jar")) moduleName += ".jar";

                    File moduleFile = Utils.getFileFromGitHub(repository, moduleName);
                    if(moduleFile == null) {
                        System.out.println("Wrong name of the module! Visit https://github.com/LeonidMem/DatapackTool-Modules to get full list!");
                        break;
                    }

                    moduleInfo = ModuleLoader.loadModule(moduleFile, keys.contains("-debug"));

                    if(moduleInfo == null) {
                        System.out.println("Module was installed, but it works incorrectly...");
                    }
                    else {
                        System.out.println("Installed " + moduleInfo.getName() + " v" + moduleInfo.getVersion() + "!");
                    }
                    break;

                default:
                    exit();
                    break;
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String info() {
        return "  module:\n" +
               "    load - load all modules (useful for debug)\n" +
               "    list - show list of installed modules\n" +
               "      -debug - show debug information\n" +
               "    info <name> - module info\n" +
               "      -debug - show debug information\n" +
               "    download [repository] <name> - download module from official repository\n" +
               "      -debug - show debug information";
    }
}

