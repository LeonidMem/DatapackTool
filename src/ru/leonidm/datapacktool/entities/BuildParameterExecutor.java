package ru.leonidm.datapacktool.entities;

import java.io.File;

public interface BuildParameterExecutor {

    /**
     * Calls when this parameter was parsed in ".mcfunction"
     * @param args Arguments
     * @param inFile File object of the input file
     * @param outFile File object of the output file
     * @throws Exception
     */
    void execute(String[] args, File inFile, File outFile) throws Exception;

}
