package ru.leonidm.datapacktool.build.commands;

import ru.leonidm.datapacktool.Messages;
import ru.leonidm.datapacktool.entities.BuildCommandExecutor;
import ru.leonidm.datapacktool.entities.McFunction;
import ru.leonidm.datapacktool.exceptions.BuildException;
import ru.leonidm.datapacktool.entities.Pair;
import ru.leonidm.datapacktool.events.*;
import ru.leonidm.datapacktool.subcommands.BuildSubcommand;

import java.io.File;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class BuildSetExecutor implements BuildCommandExecutor, BuildListener {

    private final static Map<String, String> changeFromTo = new HashMap<>();
    private final Pattern varPattern = Pattern.compile("^%[^ ]{1,1000}%$");

    protected final static Set<Pair<String, Integer>> warns = new HashSet<>();

    @Override
    public void execute(StringBuilder outFileBuilder, List<String> args, String anonymousFunctionContent, File inFile, File outFile) throws Exception {
        execute(args, changeFromTo);
    }

    protected void execute(List<String> args, Map<String, String> changeFromTo) throws Exception {
        if (args.size() == 0) {
            throw new BuildException(Messages.ILLEGAL_AMOUNT_OF_ARGS);
        }

        if (args.size() == 1) {
            String key = "%" + args.get(0) + "%";
            changeFromTo.remove(key);
            return;
        }

        String key = args.get(0);
        args.remove(0);
        String value = String.join(" ", args);

        changeFromTo.put(key.replace("%", ""), value);
    }

    @EventHandler
    public void onLineParsed(LineParsedEvent event) {
        onLineParsed(event, changeFromTo);
    }

    protected void onLineParsed(LineParsedEvent event, Map<String, String> changeFromTo) {
        String content = event.getContent();

        if (!content.contains("%")) return;

        StringBuilder editedContent = new StringBuilder();

        int lineNumber = event.getLineNumber();

        boolean keyFound = false;
        StringBuilder keyBuilder = new StringBuilder();
        for (char c : content.toCharArray()) {
            if (!keyFound) {
                if (c != '%') {
                    editedContent.append(c);
                } else {
                    keyFound = true;
                }

                continue;
            }

            if (c == ' ') {
                keyFound = false;
                keyBuilder.setLength(0);
            } else if (c != '%') {
                keyBuilder.append(c);
            } else {
                keyFound = false;

                String key = keyBuilder.toString();
                keyBuilder.setLength(0);

                String value = changeFromTo.get(key.toLowerCase());
                if (value == null) {
                    warns.add(new Pair<>(key, lineNumber));
                    editedContent.append('%').append(key).append('%');
                    continue;
                }

                warns.remove(new Pair<>(key, lineNumber));
                editedContent.append(value);
            }
        }

        if (keyBuilder.length() > 0) {
            editedContent.append('%').append(keyBuilder);
        }

        event.setContent(editedContent.toString());
    }

    @EventHandler
    private void onFileParsed(FileParsedEvent event) {
        if (event.getFileType() == FileParsedEvent.FileType.SOURCE) changeFromTo.clear();

        for (Pair<String, Integer> warn : warns.parallelStream().sorted(Comparator.comparingInt(Pair::getRight))
                .collect(Collectors.toList())) {
            BuildSubcommand.addWarn("[file: " + event.getOutFile().getAbsolutePath() + " | line:" + warn.getRight() + "] " +
                    "Variable \"" + warn.getLeft() + "\" is unset!");
        }

        warns.clear();
    }
}
