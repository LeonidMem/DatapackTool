package ru.leonidm.datapacktool.build.commands;

import ru.leonidm.datapacktool.Messages;
import ru.leonidm.datapacktool.entities.BuildCommandExecutor;
import ru.leonidm.datapacktool.events.BuildListener;
import ru.leonidm.datapacktool.events.EventHandler;
import ru.leonidm.datapacktool.events.FileParsedEvent;
import ru.leonidm.datapacktool.events.LineParsedEvent;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BuildSetExecutor implements BuildCommandExecutor, BuildListener {

    private final static Map<String, String> changeFromTo = new HashMap<>();

    @Override
    public void execute(StringBuilder outFileBuilder, List<String> args, String anonymousFunctionContent, File inFile, File outFile) throws Exception {
        execute(args, changeFromTo);
    }

    protected void execute(List<String> args, Map<String, String> changeFromTo) throws Exception {
        if(args.size() == 0) {
            throw new Exception(Messages.ILLEGAL_AMOUNT_OF_ARGS);
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
