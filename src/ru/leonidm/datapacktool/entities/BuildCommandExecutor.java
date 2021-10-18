package ru.leonidm.datapacktool.entities;

import java.io.File;
import java.util.List;

public interface BuildCommandExecutor {

    void execute(StringBuilder outFileBuilder, String[] args, String anonymousFunctionContent, File inFile, File outFile, List<File> anonymousFiles) throws Exception;
}
