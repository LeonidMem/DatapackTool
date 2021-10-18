package ru.leonidm.datapacktool.build_commands;

import ru.leonidm.datapacktool.Utils;
import ru.leonidm.datapacktool.entities.BuildCommandExecutor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class BuildExecuteExecutor implements BuildCommandExecutor {

    private final static String sep = String.valueOf(Utils.getFileSeparator()).replace("\\", "\\\\");

    @Override
    public void execute(StringBuilder outFileBuilder, String[] args, String anonymousFunctionContent, File inFile, File outFile, List<File> anonymousFiles) throws Exception {

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

        String line = String.join(" ", args);
        outFileBuilder.append("execute ")
                .append(line.replace("{", "").strip())
                .append(" run function ")
                .append(namespace)
                .append(':')
                .append(directory)
                .append(anonymousFile.getName(), 0, anonymousFile.getName().length() - 11)
                .append('\n');
    }
}
