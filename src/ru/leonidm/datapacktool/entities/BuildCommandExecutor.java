package ru.leonidm.datapacktool.entities;

import java.io.File;

public interface BuildCommandExecutor {

    void execute(StringBuilder outFileBuilder, String[] args, String anonymousFunctionContent, File inFile, File outFile) throws Exception;

}
