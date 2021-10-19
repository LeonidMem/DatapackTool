package ru.leonidm.datapacktool.build_commands;

import ru.leonidm.datapacktool.entities.BuildCommandExecutor;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class BuildVariableExecutor extends BuildCommandExecutor {

    private final List<String> mathSigns = Arrays.asList("=", ">", "<", "><", "/=", "-=", "+=", "*=", "%=");

    @Override
    public void execute(StringBuilder outFileBuilder, String[] args, String anonymousFunctionContent, File inFile, File outFile, List<File> anonymousFiles) throws Exception {
        if(args.length == 3) {
            try {
                Integer.parseInt(args[2]);
            } catch(Exception e) {
                throw new Exception("Last value must be integer!");
            }

            outFileBuilder.append("scoreboard players set ")
                          .append(args[0])
                          .append(" ")
                          .append(args[1])
                          .append(" ")
                          .append(args[2])
                          .append("\n");
            return;
        }

        if(args.length == 5) {

            if(!mathSigns.contains(args[2])) {
                throw new Exception("Unknown math sign!");
            }

            outFileBuilder.append("scoreboard players operation ")
                          .append(args[0])
                          .append(" ")
                          .append(args[1])
                          .append(" ")
                          .append(args[2])
                          .append(" ")
                          .append(args[3])
                          .append(" ")
                          .append(args[4])
                          .append("\n");

        }
    }
}
