package ru.leonidm.datapacktool;

import ru.leonidm.datapacktool.build_commands.BuildExecuteExecutor;
import ru.leonidm.datapacktool.build_commands.BuildGlobalSetExecutor;
import ru.leonidm.datapacktool.build_commands.BuildSetExecutor;
import ru.leonidm.datapacktool.build_commands.BuildVariableExecutor;
import ru.leonidm.datapacktool.entities.*;
import ru.leonidm.datapacktool.managers.CommandManager;
import ru.leonidm.datapacktool.managers.EventManager;

import java.util.*;

public class DatapackTool {

    public static void main(String[] args) {
        NativeCommand command;

        if(args.length == 0 || (command = NativeCommand.get(args[0])) == null) {
            Utils.printHelp();
            return;
        }

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
}
