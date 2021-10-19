package ru.leonidm.datapacktool.build.parameters;

import ru.leonidm.datapacktool.Utils;
import ru.leonidm.datapacktool.entities.BuildParameterExecutor;

import java.io.File;

public class BuildTagExecutor implements BuildParameterExecutor {

    @Override
    public void execute(String[] args, File inFile, File outFile) throws Exception {
        Utils.addValueToTag(outFile, args[0], "functions", Utils.getFunctionMinecraftFormattedName(outFile));
    }
}
