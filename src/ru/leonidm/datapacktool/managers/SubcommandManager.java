package ru.leonidm.datapacktool.managers;

import ru.leonidm.datapacktool.entities.Subcommand;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubcommandManager {

    private static final Map<String, Subcommand> stringToCommand = new HashMap<>();
    private static final List<Subcommand> commands = new ArrayList<>();

    /**
     * Register subcommand
     *
     * @param subcommand
     */
    public static void registerCommand(Subcommand subcommand) {
        for (String label : subcommand.getLabels()) {
            stringToCommand.put(label, subcommand);
        }
        commands.add(subcommand);
    }

    public static Subcommand getSubcommand(String label) {
        return stringToCommand.get(label);
    }

    public static List<Subcommand> getSubcommands() {
        return new ArrayList<>(commands);
    }

}
