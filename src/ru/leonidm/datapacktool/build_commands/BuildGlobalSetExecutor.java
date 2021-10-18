package ru.leonidm.datapacktool.build_commands;

import ru.leonidm.datapacktool.events.EventHandler;
import ru.leonidm.datapacktool.events.FileParsedEvent;
import ru.leonidm.datapacktool.events.LinePreParseEvent;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BuildGlobalSetExecutor extends BuildSetExecutor {

    private final static Map<String, String> changeFromTo = new HashMap<>();

    @Override
    public void execute(StringBuilder outFileBuilder, String[] args, String anonymousFunctionContent, File inFile, File outFile, List<File> anonymousFiles) throws Exception {
        execute(args, changeFromTo);
    }

    @Override
    @EventHandler
    public void onLinePreParse(LinePreParseEvent event) {
        onLinePreParse(event, changeFromTo);
    }

    @Override
    @EventHandler
    public void onFileParsed(FileParsedEvent event) {}
}
