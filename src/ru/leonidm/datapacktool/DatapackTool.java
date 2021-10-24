package ru.leonidm.datapacktool;

import ru.leonidm.datapacktool.build.commands.BuildExecuteExecutor;
import ru.leonidm.datapacktool.build.commands.BuildGlobalSetExecutor;
import ru.leonidm.datapacktool.build.commands.BuildSetExecutor;
import ru.leonidm.datapacktool.build.commands.BuildVariableExecutor;
import ru.leonidm.datapacktool.build.parameters.BuildTagExecutor;
import ru.leonidm.datapacktool.subcommands.*;
import ru.leonidm.datapacktool.entities.*;
import ru.leonidm.datapacktool.managers.CommandManager;
import ru.leonidm.datapacktool.managers.EventManager;
import ru.leonidm.datapacktool.managers.ParameterManager;
import ru.leonidm.datapacktool.managers.SubcommandManager;
import ru.leonidm.datapacktool.utils.Utils;

import java.util.*;

public class DatapackTool {

    public static void main(String[] args) throws Exception {
        Subcommand command;

        registerSubcommands();

        if(args.length == 0) {
            ModuleLoader.loadModules(false);

            System.out.println();
            Utils.printHelp();
            return;
        }

        if((command = SubcommandManager.getSubcommand(args[0])) == null) {
            ModuleLoader.loadModules(false);

            if((command = SubcommandManager.getSubcommand(args[0])) == null) {
                System.out.println();
                Utils.printHelp();
                return;
            }
        }

        registerBuildParameters();
        registerBuildCommands();

        List<String> outArgs = new ArrayList<>();
        List<String> outKeys = new ArrayList<>();

        for(String string : Arrays.copyOfRange(args, 1, args.length)) {
            if(string.startsWith("-")) outKeys.add(string);
            else outArgs.add(string);
        }

        System.out.println();
        command.run(outArgs, outKeys);
    }

    private static void registerSubcommands() {
        SubcommandManager.registerCommand(new SubcommandBuilder()
                .setLabels("help", "h", "?")
                .setExecutor(new HelpSubcommand())
                .build());

        SubcommandManager.registerCommand(new SubcommandBuilder()
                .setLabels("install")
                .setExecutor(new InstallSubcommand())
                .build());

        SubcommandManager.registerCommand(new SubcommandBuilder()
                .setLabels("config")
                .setExecutor(new ConfigSubcommand())
                .build());

        SubcommandManager.registerCommand(new SubcommandBuilder()
                .setLabels("build")
                .setExecutor(new BuildSubcommand())
                .build());

        SubcommandManager.registerCommand(new SubcommandBuilder()
                .setLabels("module")
                .setExecutor(new ModuleSubcommand())
                .build());
    }

    private static void registerBuildCommands() {
        CommandManager.registerCommand(new BuildCommandBuilder()
                .setLabel("execute")
                .setExecutor(new BuildExecuteExecutor())
                .set(BuildCommand.Setting.TAKE_ANONYMOUS_FUNCTION_AS_ARG, true)
                .set(BuildCommand.Setting.THERE_MUST_BE_ANONYMOUS_FUNCTION, true)
                .build());

        CommandManager.registerCommand(new BuildCommandBuilder()
                .setLabel("var")
                .setExecutor(new BuildVariableExecutor())
                .set(BuildCommand.Setting.ARGS_AMOUNT, Arrays.asList(3, 5))
                .build());

        CommandManager.registerCommand(new BuildCommandBuilder()
                .setLabel("set")
                .setExecutor(new BuildSetExecutor())
                .build());
        EventManager.registerListener(new BuildSetExecutor());

        CommandManager.registerCommand(new BuildCommandBuilder()
                .setLabel("globalset")
                .setExecutor(new BuildGlobalSetExecutor())
                .build());
        EventManager.registerListener(new BuildGlobalSetExecutor());
    }

    private static void registerBuildParameters() {
        ParameterManager.registerParameter(new BuildParameterBuilder()
                .setLabel("tag")
                .setExecutor(new BuildTagExecutor())
                .set(BuildParameter.Setting.ARGS_AMOUNT, Collections.singletonList(1))
                .build());
    }
}
