package ru.leonidm.datapacktool.commands;

import ru.leonidm.datapacktool.ModuleLoader;
import ru.leonidm.datapacktool.configs.ModulesConfig;
import ru.leonidm.datapacktool.entities.NativeCommandExecutor;
import ru.leonidm.datapacktool.utils.GitHubUtils;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

                    File moduleFile = GitHubUtils.getFile(repository, moduleName);
                    if(moduleFile == null) {
                        System.out.println("Wrong name of the module or repository! Visit https://github.com/LeonidMem/DatapackTool-Modules to get full list!");
                        break;
                    }

                    moduleInfo = ModuleLoader.loadModule(moduleFile, keys.contains("-debug"));

                    String commitID = GitHubUtils.getLastFileCommitID(repository, moduleName);
                    if(commitID != null) {
                        moduleName = moduleName.substring(0, moduleName.length() - 4);
                        ModulesConfig.add(moduleName + "_last_commit", commitID);
                        ModulesConfig.add(moduleName + "_repository", repository);
                        ModulesConfig.save();
                    }

                    if(moduleInfo == null) {
                        System.out.println("Module was installed, but it works incorrectly...");
                    }
                    else {
                        System.out.println("Installed " + moduleInfo.getName() + " v" + moduleInfo.getVersion() + "!");
                    }
                    break;

                case "update":
                    ModuleLoader.loadModules(keys.contains("-debug"));

                    List<ModuleLoader.ModuleInfo> modules = ModuleLoader.getModules();
                    ModuleLoader.unloadModules();

                    for(ModuleLoader.ModuleInfo moduleInfo1 : modules) {
                        System.out.println("Checking for " + moduleInfo1.getName() + "'s updates...");

                        moduleName = moduleInfo1.getName();

                        String lastCommit = ModulesConfig.get(moduleName + "_last_commit");
                        repository = ModulesConfig.get(moduleName + "_repository");

                        commitID = GitHubUtils.getLastFileCommitID(repository, moduleName + ".jar");
                        if(commitID == null) {
                            System.out.println("Something went wrong! Skipping it...");
                            continue;
                        }

                        if(commitID.equals(lastCommit)) {
                            System.out.println("This module doesn't have any updates! Skipping it...");
                            continue;
                        }

                        moduleFile = GitHubUtils.getFile(repository, moduleName + ".jar");
                        if(moduleFile == null) {
                            System.out.println("Can't get this module from GitHub! Skipping it...");
                            break;
                        }

                        ModulesConfig.add(moduleName + "_last_commit", commitID);
                        ModulesConfig.save();

                        moduleInfo = ModuleLoader.loadModule(moduleFile, keys.contains("-debug"));

                        if(moduleInfo == null) {
                            System.out.println("Module was installed, but it works incorrectly...");
                        }
                        else {
                            System.out.println("Updated " + moduleInfo.getName() + " v" + moduleInfo.getVersion() + "!");
                        }

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
               "      -debug - show debug information" +
               "    update - update all modules\n" +
               "      -debug - show debug information";
    }
}

