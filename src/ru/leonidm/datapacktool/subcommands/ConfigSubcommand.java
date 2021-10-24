package ru.leonidm.datapacktool.subcommands;

import ru.leonidm.datapacktool.configs.MainConfig;
import ru.leonidm.datapacktool.entities.SubcommandExecutor;

import java.util.List;
import java.util.Map;

public class ConfigSubcommand implements SubcommandExecutor {

    @Override
    public void run(List<String> args, List<String> keys) {
        if(args.size() == 0) {
            exit();
            return;
        }

        switch(args.get(0).toLowerCase()) {
            case "set":
                if(args.size() != 4) {
                    exit();
                    return;
                }

                String id = args.get(1);
                String inPath = args.get(2);
                String outPath = args.get(3);

                MainConfig.add(id + "_in", inPath);
                MainConfig.add(id + "_out", outPath);
                
                System.out.println("Set successfully!");
                break;

            case "remove":
                if(args.size() != 2) {
                    exit();
                    return;
                }

                id = args.get(1);
                MainConfig.remove(id + "_in");
                MainConfig.remove(id + "_out");

                System.out.println("Removed successfully!");
                break;

            case "display":
                if(args.size() != 1) {
                    exit();
                    return;
                }

                for(Map.Entry<String, String> entry : MainConfig.getEntries()) {
                    System.out.println(entry.getKey() + "=" + entry.getValue());
                }

                break;

            case "clear":
                if(args.size() != 1) {
                    exit();
                    return;
                }

                MainConfig.clear();

                System.out.println("Cleared successfully!");
                break;

            default:
                exit();
                break;
        }

        MainConfig.save();
    }

    @Override
    public String info() {
        return "  config:\n" +
               "    set <id> <in_path> <out_path>  - set value to the config\n" +
               "    remove <id> - remove id from the config\n" +
               "    clear - clear config\n" +
               "    display - show config";
    }
}

