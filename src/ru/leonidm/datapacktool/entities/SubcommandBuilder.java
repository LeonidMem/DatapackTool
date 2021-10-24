package ru.leonidm.datapacktool.entities;

public class SubcommandBuilder {

    private String[] labels;
    private SubcommandExecutor executor;

    public SubcommandBuilder setLabels(String... labels) {
        this.labels = labels;
        return this;
    }

    public SubcommandBuilder setExecutor(SubcommandExecutor executor) {
        this.executor = executor;
        return this;
    }

    public Subcommand build() {
        if(labels == null) throw new IllegalArgumentException("Labels is null!");
        if(executor == null) throw new IllegalArgumentException("Executor is null!");
        return new Subcommand(labels, executor);
    }
}
