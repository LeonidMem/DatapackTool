package ru.leonidm.datapacktool.entities;

import java.util.List;

public class Subcommand {

    private final SubcommandExecutor executor;
    private final String[] labels;

    public Subcommand(String[] labels, SubcommandExecutor executor) {
        this.executor = executor;
        this.labels = labels;
    }

    public String[] getLabels() {
        return labels;
    }

    public void run(List<String> args, List<String> keys) {
        executor.run(args, keys);
    }

    public String getInfo() {
        return executor.info();
    }
}
