package ru.leonidm.datapacktool.entities;

import java.io.File;
import java.util.List;

public interface BuildCommandExecutor {

    /**
     * Calls when this command was parsed in ".mcfunction"
     * @param outFileBuilder StringBuilder of the output file
     * @param args Arguments
     * @param anonymousFunctionContent Content of the anonymous function (can be null)
     * @param inFile File object of the input file
     * @param outFile File object of the output file
     * @throws Exception
     */
    void execute(StringBuilder outFileBuilder, List<String> args, String anonymousFunctionContent, File inFile, File outFile) throws Exception;

}
