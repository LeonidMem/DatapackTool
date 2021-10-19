package ru.leonidm.datapacktool.build.commands;

import ru.leonidm.datapacktool.Utils;
import ru.leonidm.datapacktool.entities.BuildCommandExecutor;

import java.io.File;

public class BuildExecuteExecutor implements BuildCommandExecutor {

    @Override
    public void execute(StringBuilder outFileBuilder, String[] args, String anonymousFunctionContent, File inFile, File outFile) throws Exception {
        String functionName = Utils.createAnonymousFunction(outFile, anonymousFunctionContent);

        String line = String.join(" ", args);
        outFileBuilder.append("execute ")
                .append(line.replace("{", "").strip())
                .append(" run function ")
                .append(functionName)
                .append('\n');
    }
}
