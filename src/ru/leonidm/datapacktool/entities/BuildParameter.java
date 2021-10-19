package ru.leonidm.datapacktool.entities;

import ru.leonidm.datapacktool.Messages;

import java.io.File;
import java.util.List;
import java.util.Map;

public class BuildParameter {

    private final String label;
    private final BuildParameterExecutor executor;
    private final Map<Setting.Type, Setting<?>> settings;

    public BuildParameter(String label, BuildParameterExecutor executor, Map<Setting.Type, Setting<?>> settings) {
        this.label = label;
        this.executor = executor;
        this.settings = settings;
    }

    public void execute(String[] args, File inFile, File outFile) throws Exception {
        Setting<?> setting = settings.get(Setting.Type.ARGS_AMOUNT);

        if(setting != null) {
            if(!((Setting<List<Integer>>) setting).getValue().contains(args.length)) {
                throw new Exception(Messages.ILLEGAL_AMOUNT_OF_ARGS);
            }
        }

        executor.execute(args, inFile, outFile);
    }

    public static class Setting<T> {

        public static class Type {

            public static final Type ARGS_AMOUNT = new Type();

        }

        public static final Setting<List<Integer>> ARGS_AMOUNT = new Setting<>(Type.ARGS_AMOUNT, null);

        private final Type type;
        private final T value;

        public Setting(Type type, T value) {
            this.type = type;
            this.value = value;
        }

        public Type getType() {
            return type;
        }

        public T getValue() {
            return value;
        }

        public Setting<T> clone(T value) {
            return new Setting<>(type, value);
        }
    }

    public String getLabel() {
        return label;
    }
}
