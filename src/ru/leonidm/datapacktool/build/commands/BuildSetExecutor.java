package ru.leonidm.datapacktool.build.commands;

import ru.leonidm.datapacktool.Messages;
import ru.leonidm.datapacktool.entities.BuildCommandExecutor;
import ru.leonidm.datapacktool.events.BuildListener;
import ru.leonidm.datapacktool.events.EventHandler;
import ru.leonidm.datapacktool.events.FileParsedEvent;
import ru.leonidm.datapacktool.events.LineParsedEvent;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class BuildSetExecutor implements BuildCommandExecutor, BuildListener {

    private final static Map<String, String> changeFromTo = new HashMap<>();

    @Override
    public void execute(StringBuilder outFileBuilder, String[] args, String anonymousFunctionContent, File inFile, File outFile) throws Exception {
        execute(args, changeFromTo);
    }

    protected void execute(String[] args, Map<String, String> changeFromTo) throws Exception {
        if(args.length == 0) {
            throw new Exception(Messages.ILLEGAL_AMOUNT_OF_ARGS);
        }

        if(args.length == 1) {
            String key = "%" + args[0] + "%";
            changeFromTo.remove(key);
            return;
        }

        String key;
        if(args[0].startsWith("%") && args[0].endsWith("%")) key = args[0];
        else key = "%" + args[0] + "%";
        String value = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

        changeFromTo.put(key, value);
    }

    @EventHandler
    public void onLinePreParse(LineParsedEvent event) {
        onLinePreParse(event, changeFromTo);
    }

    protected void onLinePreParse(LineParsedEvent event, Map<String, String> changeFromTo) {
        String content = event.getContent();

        for(Map.Entry<String, String> entry : changeFromTo.entrySet()) {
            content = content.replace(entry.getKey(), entry.getValue());
        }

        event.setContent(content);
    }

    @EventHandler
    public void onFileParsed(FileParsedEvent event) {
        changeFromTo.clear();
    }
}
