package ru.leonidm.datapacktool.subcommands;

import ru.leonidm.datapacktool.entities.SubcommandExecutor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class EnvironmentSubcommand implements SubcommandExecutor {

    @Override
    public void run(List<String> args, List<String> keys) {
        if (args.size() == 0) {
            exit();
            return;
        }

        switch (args.get(0).toLowerCase()) {
            case "init":
                File buildFile = new File("dtool/build.json");
                if (buildFile.exists()) {
                    System.err.println("Environment is already initialized!");
                    System.err.println();
                    return;
                }

                buildFile.getParentFile().mkdirs();

                try {
                    buildFile.createNewFile();

                    FileWriter fileWriter = new FileWriter(buildFile);
                    fileWriter.write("{\n\t\"out\": \"../out\",\n\t\"args\": [\"-pr\", \"-minify\"],\n\t\"modules\": []\n}");
                    fileWriter.close();

                    File initFunction = new File("dtool/init.mcfunction");
                    initFunction.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();

                    System.err.println("=====");
                    System.err.println("Environment isn't initialized!");
                    System.err.println();
                }

                System.out.println("Environment was initialized successfully!");
                System.out.println();
                break;

            default:
                exit();
                break;
        }
    }

    @Override
    public String info() {
        return "  environment env:\n" +
                "    init - initialize DTool's environment";
    }
}
