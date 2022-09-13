package ru.leonidm.datapacktool.build.parameters;

import ru.leonidm.datapacktool.entities.BuildParameterExecutor;
import ru.leonidm.datapacktool.exceptions.FileIgnoreException;

import java.io.File;
import java.util.List;

public class BuildIgnoreExecutor implements BuildParameterExecutor {

    @Override
    public void execute(List<String> args, File inFile, File outFile) throws Exception {
        throw new FileIgnoreException();
    }
}
