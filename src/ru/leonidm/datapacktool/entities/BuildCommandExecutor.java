package ru.leonidm.datapacktool.entities;

import ru.leonidm.datapacktool.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public abstract class BuildCommandExecutor {

    public abstract void execute(StringBuilder outFileBuilder, String[] args, String anonymousFunctionContent, File inFile, File outFile, List<File> anonymousFiles) throws Exception;

    protected static final String fileSeparator = String.valueOf(Utils.getFileSeparator()).replace("\\", "\\\\");

    protected static String createAnonymousFunction(File inFile, File outFile, String anonymousFunctionContent, List<File> anonymousFiles) throws IOException {

        File anonymousFile = new File(outFile.getParentFile(), outFile.getName().substring(0, outFile.getName().length() - 11) + anonymousFunctionContent.hashCode() + ".mcfunction");
        OutputStream outputStream = new FileOutputStream(anonymousFile);
        outputStream.write(anonymousFunctionContent.getBytes(StandardCharsets.UTF_8));
        outputStream.close();

        anonymousFiles.add(anonymousFile);

        String regex1 = fileSeparator + "data" + fileSeparator;

        String namespace = inFile.getParent().split(regex1, 2)[1].split(fileSeparator, 2)[0];

        String regex2 = regex1 + namespace + fileSeparator + "functions";

        String directory = outFile.getParent().split(regex2, 2)[1].replace("\\", "/");
        if(directory.startsWith("/")) directory = directory.substring(1);
        if(!directory.equals("")) directory += "/";

        return namespace + ":" + directory + anonymousFile.getName().substring(0, anonymousFile.getName().length() - 11);
    }
}
