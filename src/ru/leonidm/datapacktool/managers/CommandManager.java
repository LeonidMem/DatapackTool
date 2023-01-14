package ru.leonidm.datapacktool.managers;

import ru.leonidm.datapacktool.entities.BuildCommand;

import java.util.HashMap;
import java.util.Map;

public class CommandManager {

    private final static Map<String, BuildCommand> commands = new HashMap<>();

    /**
     * Register command
     *
     * @param command
     */
    public static void registerCommand(BuildCommand command) {
        commands.put(command.getLabel(), command);
    }

    public static BuildCommand getCommand(String label) {
        return commands.get(label);
    }

}
