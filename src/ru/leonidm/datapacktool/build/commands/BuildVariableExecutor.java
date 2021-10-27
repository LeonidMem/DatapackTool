package ru.leonidm.datapacktool.build.commands;

import ru.leonidm.datapacktool.entities.BuildCommandExecutor;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class BuildVariableExecutor implements BuildCommandExecutor {

    private final List<String> mathSigns = Arrays.asList("=", ">", "<", "><", "/=", "-=", "+=", "*=", "%=");

    @Override
    public void execute(StringBuilder outFileBuilder, List<String> args, String anonymousFunctionContent, File inFile, File outFile) throws Exception {
        if(args.size() == 3) {
            try {
                Integer.parseInt(args.get(2));
            } catch(Exception e) {
                throw new Exception("Last value must be integer!");
            }

            outFileBuilder.append("scoreboard players set ")
                          .append(args.get(0))
                          .append(" ")
                          .append(args.get(1))
                          .append(" ")
                          .append(args.get(2))
                          .append("\n");
            return;
        }

        if(args.size() == 5) {

            if(!mathSigns.contains(args.get(2))) {
                throw new Exception("Unknown math sign!");
            }

            outFileBuilder.append("scoreboard players operation ")
                          .append(args.get(0))
                          .append(" ")
                          .append(args.get(1))
                          .append(" ")
                          .append(args.get(2))
                          .append(" ")
                          .append(args.get(3))
                          .append(" ")
                          .append(args.get(4))
                          .append("\n");

        }
    }
}
