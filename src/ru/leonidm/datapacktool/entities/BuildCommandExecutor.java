package ru.leonidm.datapacktool.entities;

import ru.leonidm.datapacktool.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public interface BuildCommandExecutor {

    void execute(StringBuilder outFileBuilder, String[] args, String anonymousFunctionContent, File inFile, File outFile, List<File> anonymousFiles) throws Exception;

    static String createAnonymousFunction(File inFile, File outFile, String anonymousFunctionContent, List<File> anonymousFiles) throws IOException {
        String sep = String.valueOf(Utils.getFileSeparator()).replace("\\", "\\\\");

        File anonymousFile = new File(outFile.getParentFile(), outFile.getName().substring(0, outFile.getName().length() - 11) + anonymousFunctionContent.hashCode() + ".mcfunction");
        OutputStream outputStream = new FileOutputStream(anonymousFile);
        outputStream.write(anonymousFunctionContent.getBytes(StandardCharsets.UTF_8));
        outputStream.close();

        anonymousFiles.add(anonymousFile);

        String regex1 = sep + "data" + sep;

        String namespace = inFile.getParent().split(regex1, 2)[1].split(sep, 2)[0];

        String regex2 = regex1 + namespace + sep + "functions";

        String directory = outFile.getParent().split(regex2, 2)[1].replace("\\", "/");
        if(directory.startsWith("/")) directory = directory.substring(1);
        if(!directory.equals("")) directory += "/";

        return namespace + ":" + directory + anonymousFile.getName().substring(0, anonymousFile.getName().length() - 11);
    }
}
