package ru.leonidm.datapacktool.entities;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum NativeCommand {

    HELP("h", "?"), INSTALL, CONFIG, BUILD, MODULE;

    private static final Map<String, NativeCommand> stringToCommand = new HashMap<>();

    public static NativeCommand get(String label) {
        return stringToCommand.get(label);
    }

    static {
        for(NativeCommand command : values()) {
            for (String alias : command.aliases) {
                stringToCommand.put(alias, command);
            }
            stringToCommand.put(command.name().toLowerCase(), command);
        }
    }

    private final NativeCommandExecutor commandExecutor;
    private final String[] aliases;

    NativeCommand(String... aliases) {
        this.aliases = aliases;
        this.commandExecutor = getCommandExecutor();
    }

    private NativeCommandExecutor getCommandExecutor() {
        try {
            StringBuilder sb = new StringBuilder("ru.leonidm.datapacktool.commands.");
            sb.append(this.toString().toLowerCase());
            sb.replace(33, 34, String.valueOf(Character.toUpperCase(sb.charAt(33))));

            Class<? extends NativeCommandExecutor> commandExecutorClass =
                    (Class<? extends NativeCommandExecutor>) Class.forName(sb.toString());

            return commandExecutorClass.getConstructor().newInstance();
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void run(List<String> args, List<String> keys) {
        commandExecutor.run(args, keys);
    }

    public String getInfo() {
        return commandExecutor.info();
    }
}
