package ru.leonidm.datapacktool.entities;

import java.io.File;

public interface BuildParameterExecutor {

    void execute(String[] args, File inFile, File outFile) throws Exception;

}
