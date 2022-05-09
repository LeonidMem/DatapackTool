package ru.leonidm.datapacktool.entities;

import ru.leonidm.datapacktool.Messages;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BuildCommand {

    private final String label;
    private final BuildCommandExecutor executor;
    private final Map<Setting.Type, Setting<?>> settings;

    public BuildCommand(String label, BuildCommandExecutor executor, Map<Setting.Type, Setting<?>> settings) {
        this.label = label;
        this.executor = executor;
        this.settings = settings;
    }

    public void execute(StringBuilder outFileBuilder, List<String> args, String anonymousFunctionContent,
                        File inFile, File outFile) throws Exception {
        Setting<?> setting = settings.get(Setting.Type.ARGS_AMOUNT);
        if(setting != null) {
            if(!((Setting<Set<Integer>>) setting).getValue().contains(args.size())) {
                throw new BuildException(Messages.ILLEGAL_AMOUNT_OF_ARGS);
            }
        }

        if(anonymousFunctionContent != null) {
            setting = settings.get(Setting.Type.TAKE_ANONYMOUS_FUNCTION_AS_ARG);

            if(setting != null && !((Setting<Boolean>) setting).getValue()) {
                throw new BuildException("This command can't be used with anonymous function!");
            }

            boolean hasAnonymousFunction = ((Setting<Boolean>) setting).getValue();
        }
        else {
            setting = settings.get(Setting.Type.THERE_MUST_BE_ANONYMOUS_FUNCTION);

            if(setting != null && ((Setting<Boolean>) setting).getValue()) {
                throw new BuildException("You must specify anonymous function as argument here!");
            }
        }

        executor.execute(outFileBuilder, args, anonymousFunctionContent, inFile, outFile);
    }

    public String getLabel() {
        return label;
    }

    public static class Setting<T> {

        public static class Type {

            public static final Type ARGS_AMOUNT = new Type();
            public static final Type TAKE_ANONYMOUS_FUNCTION_AS_ARG = new Type();
            public static final Type THERE_MUST_BE_ANONYMOUS_FUNCTION = new Type();

        }

        public static final Setting<Set<Integer>> ARGS_AMOUNT = new Setting<>(Type.ARGS_AMOUNT, null);
        public static final Setting<Boolean> TAKE_ANONYMOUS_FUNCTION_AS_ARG = new Setting<>(Type.TAKE_ANONYMOUS_FUNCTION_AS_ARG, null);
        public static final Setting<Boolean> THERE_MUST_BE_ANONYMOUS_FUNCTION = new Setting<>(Type.THERE_MUST_BE_ANONYMOUS_FUNCTION, null);

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
}
