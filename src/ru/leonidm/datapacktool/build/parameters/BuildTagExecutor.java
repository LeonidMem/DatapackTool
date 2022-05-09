package ru.leonidm.datapacktool.build.parameters;

import ru.leonidm.datapacktool.utils.DatapackUtils;
import ru.leonidm.datapacktool.entities.BuildParameterExecutor;

import java.io.File;
import java.util.List;

public class BuildTagExecutor implements BuildParameterExecutor {

    @Override
    public void execute(List<String> args, File inFile, File outFile) throws Exception {
        DatapackUtils.addValueToTag(outFile, args.get(0), "functions",
                DatapackUtils.getFunctionName(outFile));
    }
}
