package ru.leonidm.datapacktool.managers;

import ru.leonidm.datapacktool.entities.BuildParameter;

import java.util.HashMap;
import java.util.Map;

public class ParameterManager {

    private final static Map<String, BuildParameter> commands = new HashMap<>();

    public static void registerParameter(BuildParameter command) {
        commands.put(command.getLabel(), command);
    }

    public static BuildParameter getParameter(String label) {
        return commands.get(label);
    }

}
