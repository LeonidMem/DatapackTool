package ru.leonidm.datapacktool.build.commands;

import ru.leonidm.datapacktool.events.EventHandler;
import ru.leonidm.datapacktool.events.FileParsedEvent;
import ru.leonidm.datapacktool.events.LineParsedEvent;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BuildGlobalSetExecutor extends BuildSetExecutor {

    private final static Map<String, String> changeFromTo = new HashMap<>();

    @Override
    public void execute(StringBuilder outFileBuilder, List<String> args, String anonymousFunctionContent, File inFile, File outFile) throws Exception {
        execute(args, changeFromTo);
    }

    @Override
    @EventHandler
    public void onLinePreParse(LineParsedEvent event) {
        onLinePreParse(event, changeFromTo);
    }

    @Override
    @EventHandler
    public void onFileParsed(FileParsedEvent event) {}
}
