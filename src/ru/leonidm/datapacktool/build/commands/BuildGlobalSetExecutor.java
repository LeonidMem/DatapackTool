package ru.leonidm.datapacktool.build.commands;

import ru.leonidm.datapacktool.events.EventHandler;
import ru.leonidm.datapacktool.events.FileParsedEvent;
import ru.leonidm.datapacktool.events.LineParsedEvent;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
    public void onLineParsed(LineParsedEvent event) {
        onLineParsed(event, changeFromTo);
    }

    @EventHandler
    private void onFileParsed(FileParsedEvent event) throws IOException {
        if (event.getFileType() != FileParsedEvent.FileType.RESOURCE) return;

        File inFile = event.getInFile();
        Path inPath = Path.of(inFile.getAbsolutePath());

        String content = Files.readString(inPath);

        LineParsedEvent lineParsedEvent = new LineParsedEvent(inFile, null, content, -1);
        onLineParsed(lineParsedEvent, changeFromTo);

        event.setContent(lineParsedEvent.getContent());
    }
}
