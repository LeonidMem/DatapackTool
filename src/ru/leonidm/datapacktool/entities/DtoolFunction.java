package ru.leonidm.datapacktool.entities;

import ru.leonidm.datapacktool.exceptions.BuildException;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DtoolFunction extends McFunction {

    private final Set<String> allowedCommands;

    public DtoolFunction(File inFile, String... allowedCommands) {
        super(inFile, null);

        this.allowedCommands = new HashSet<>(Arrays.asList(allowedCommands));
    }

    @Override
    public void save() throws Exception {
        throw new BuildException("DtoolFunction can't be saved!");
    }

    @Override
    protected void executeParameter(BuildParameter parameter, List<String> args) throws Exception {
        throw new BuildException("Parameters can't be used in DtoolFunctions!");
    }

    @Override
    protected void executeCommand(BuildCommand command, List<String> args, String anonymousFunctionContent) throws Exception {
        String label = command.getLabel();
        if (!allowedCommands.contains(label))
            throw new BuildException("Command \"" + label + "\" can't be used in this DtoolFunction!");

        super.executeCommand(command, args, anonymousFunctionContent);
    }
}
