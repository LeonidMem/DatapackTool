package ru.leonidm.datapacktool.build.commands;

import ru.leonidm.datapacktool.entities.BuildCommandExecutor;
import ru.leonidm.datapacktool.exceptions.BuildException;

import java.io.File;
import java.util.*;

public class BuildVariableExecutor implements BuildCommandExecutor {

    private final Set<String> mathSigns = new HashSet<>(Arrays.asList("=", ">", "<", "><", "/=", "-=", "+=", "*=", "%="));
    private final Map<Character, String> mathSigns2 = new HashMap<>() {{
        put('+', "add");
        put('-', "remove");
        put('=', "set");
    }};

    @Override
    public void execute(StringBuilder outFileBuilder, List<String> args, String anonymousFunctionContent, File inFile, File outFile) throws Exception {
        if(args.size() == 3) {
            String operation;

            char mathSign = args.get(2).charAt(0);

            operation = mathSigns2.get(mathSign);
            if(operation == null) {
                throw new BuildException("Unknown math sign \"" + mathSign + "\"!");
            }

            String number = args.get(2).substring(1);
            try {
                Integer.parseInt(number);
            } catch(Exception e) {
                throw new BuildException("Last value must be integer!");
            }

            outFileBuilder.append("scoreboard players ")
                          .append(operation)
                          .append(" ")
                          .append(args.get(0))
                          .append(" ")
                          .append(args.get(1))
                          .append(" ")
                          .append(number)
                          .append("\n");
            return;
        }

        if(args.size() == 5) {

            if(!mathSigns.contains(args.get(2))) {
                throw new BuildException("Unknown math sign \"" + args.get(2) + "\"!");
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
