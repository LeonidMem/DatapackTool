package ru.leonidm.datapacktool.entities;

import java.util.HashMap;
import java.util.Map;

public class BuildParameterBuilder {

    private String label;
    private BuildParameterExecutor executor;
    private final Map<BuildParameter.Setting.Type, BuildParameter.Setting<?>> settings = new HashMap<>();

    public BuildParameterBuilder setLabel(String label) {
        this.label = label;
        return this;
    }

    public BuildParameterBuilder setExecutor(BuildParameterExecutor executor) {
        this.executor = executor;
        return this;
    }

    public <T> BuildParameterBuilder set(BuildParameter.Setting<T> setting, T value) {
        settings.put(setting.getType(), setting.clone(value));
        return this;
    }

    public BuildParameter build() {
        if (label == null) throw new IllegalArgumentException("Label is null!");
        if (executor == null) throw new IllegalArgumentException("Executor is null!");
        return new BuildParameter(label, executor, settings);
    }

}
