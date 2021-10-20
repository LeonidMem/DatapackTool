package ru.leonidm.datapacktool.build.parameters;

import ru.leonidm.datapacktool.utils.DatapackUtils;
import ru.leonidm.datapacktool.entities.BuildParameterExecutor;

import java.io.File;

public class BuildTagExecutor implements BuildParameterExecutor {

    @Override
    public void execute(String[] args, File inFile, File outFile) throws Exception {
        DatapackUtils.addValueToTag(outFile, args[0], "functions", DatapackUtils.getFunctionMinecraftFormattedName(outFile));
    }
}
