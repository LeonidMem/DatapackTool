package ru.leonidm.datapacktool.build_commands;

import ru.leonidm.datapacktool.entities.BuildCommandExecutor;

import java.io.File;
import java.util.List;

public class BuildExecuteExecutor extends BuildCommandExecutor {

    @Override
    public void execute(StringBuilder outFileBuilder, String[] args, String anonymousFunctionContent, File inFile, File outFile, List<File> anonymousFiles) throws Exception {
        String functionName = BuildCommandExecutor.createAnonymousFunction(inFile, outFile, anonymousFunctionContent, anonymousFiles);

        String line = String.join(" ", args);
        outFileBuilder.append("execute ")
                .append(line.replace("{", "").strip())
                .append(" run function ")
                .append(functionName)
                .append('\n');
    }
}
