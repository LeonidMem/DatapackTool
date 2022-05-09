package ru.leonidm.datapacktool.build.commands;

import ru.leonidm.datapacktool.Messages;
import ru.leonidm.datapacktool.entities.BuildCommandExecutor;
import ru.leonidm.datapacktool.entities.BuildException;
import ru.leonidm.datapacktool.entities.Pair;
import ru.leonidm.datapacktool.events.*;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BuildSetExecutor implements BuildCommandExecutor, BuildListener {

    private final static Map<String, String> changeFromTo = new HashMap<>();
    private final Pattern varPattern = Pattern.compile("^%[^ ]{1,1000}%$");

    protected final static Set<Pair<String, Integer>> warns = new HashSet<>();

    @Override
    public void execute(StringBuilder outFileBuilder, List<String> args, String anonymousFunctionContent, File inFile, File outFile) throws Exception {
        execute(args, changeFromTo);
    }

    protected void execute(List<String> args, Map<String, String> changeFromTo) throws Exception {
        if(args.size() == 0) {
            throw new BuildException(Messages.ILLEGAL_AMOUNT_OF_ARGS);
        }

        if(args.size() == 1) {
            String key = "%" + args.get(0) + "%";
            changeFromTo.remove(key);
            return;
        }

        String key;
        if(args.get(0).startsWith("%") && args.get(0).endsWith("%")) key = args.get(0);
        else key = "%" + args.get(0) + "%";
        args.remove(0);
        String value = String.join(" ", args);

        changeFromTo.put(key.toLowerCase(), value);
    }

    @EventHandler
    public void onLineParsed(LineParsedEvent event) {
        onLineParsed(event, changeFromTo);
    }

    protected void onLineParsed(LineParsedEvent event, Map<String, String> changeFromTo) {
        String content = event.getContent();

        String[] entries = content.split("((?<=(?:%[^ ]{1,1000}%))|(?=(?:%[^ ]{1,1000}%)))");
        StringBuilder editedContent = new StringBuilder();

        int lineNumber = event.getLineNumber();

        for(String entry : entries) {
            Matcher matcher = varPattern.matcher(entry);
            if(!matcher.matches()) {
                editedContent.append(entry);
                continue;
            }

            String value = changeFromTo.get(entry.toLowerCase());
            if(value == null) {
                warns.add(new Pair<>(entry, lineNumber));
                editedContent.append(entry);
                continue;
            }

            editedContent.append(value);
            warns.remove(new Pair<>(entry, lineNumber));
        }

        event.setContent(editedContent.toString());
    }

    @EventHandler
    private void onFileParsed(FileParsedEvent event) {
        if(event.getFileType() == FileParsedEvent.FileType.SOURCE) changeFromTo.clear();

        for(Pair<String, Integer> warn : warns) {
            System.out.println("[WARN] [line:" + warn.getRight() + "] Variable \"" + warn.getLeft() + "\" is unset!");
        }

        warns.clear();
    }
}
