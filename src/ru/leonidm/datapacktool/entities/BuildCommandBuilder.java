package ru.leonidm.datapacktool.entities;

import java.util.HashMap;
import java.util.Map;

public class BuildCommandBuilder {

    private String label;
    private BuildCommandExecutor executor;
    private final Map<BuildCommand.Setting.Type, BuildCommand.Setting<?>> settings = new HashMap<>();

    public BuildCommandBuilder() {
        this.set(BuildCommand.Setting.TAKE_ANONYMOUS_FUNCTION_AS_ARG, false);
    }

    public BuildCommandBuilder setLabel(String label) {
        this.label = label;
        return this;
    }

    public BuildCommandBuilder setExecutor(BuildCommandExecutor executor) {
        this.executor = executor;
        return this;
    }

    public <T> BuildCommandBuilder set(BuildCommand.Setting<T> setting, T value) {
        settings.put(setting.getType(), setting.clone(value));
        return this;
    }

    public BuildCommand build() {
        if (label == null) throw new IllegalArgumentException("Label is null!");
        if (executor == null) throw new IllegalArgumentException("Executor is null!");
        return new BuildCommand(label, executor, settings);
    }
}
