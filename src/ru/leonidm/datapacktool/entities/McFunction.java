package ru.leonidm.datapacktool.entities;

import ru.leonidm.datapacktool.events.FileParsedEvent;
import ru.leonidm.datapacktool.events.LinePreParseEvent;
import ru.leonidm.datapacktool.managers.CommandManager;
import ru.leonidm.datapacktool.managers.EventManager;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class McFunction {

    private final List<File> anonymousFiles;
    private final File inFile, outFile;
    private final StringBuilder out;
    private int lineNumber = 0;

    public McFunction(File inFile, File outFile) {
        this.inFile = inFile;
        outFile.getParentFile().mkdirs();
        this.outFile = outFile;
        this.out = new StringBuilder();
        this.anonymousFiles = new ArrayList<>();
    }

    public void parse() throws Exception {
        InputStream inputStream = new FileInputStream(inFile);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        System.out.println("Parsing " + inFile.getAbsolutePath() + "...");
        while(reader.ready()) {
            parseLine(reader);
        }

        FileParsedEvent fileParsedEvent = new FileParsedEvent(this.out.toString());
        EventManager.callEvent(fileParsedEvent);

        OutputStream outputStream = new FileOutputStream(outFile);
        outputStream.write(fileParsedEvent.getContent().getBytes(StandardCharsets.UTF_8));
        outputStream.close();
    }

    private void parseLine(BufferedReader reader) throws Exception {
        lineNumber += 1;
        String line = reader.readLine().strip();

        boolean isCommented;

        if((isCommented = !line.startsWith("! ")) && !line.startsWith("#! ")) {
            LinePreParseEvent linePreParseEvent = new LinePreParseEvent(line);
            EventManager.callEvent(linePreParseEvent);
            line = linePreParseEvent.getContent();

            out.append(line).append('\n');
            return;
        }

        line = line.substring(isCommented ? 3 : 2).strip();
        LinePreParseEvent linePreParseEvent = new LinePreParseEvent(line);
        EventManager.callEvent(linePreParseEvent);
        line = linePreParseEvent.getContent();

        String[] split = line.split(" ");
        BuildCommand command = CommandManager.getCommand(split[0]);
        if(command == null) {
            if(split[0].startsWith("%")) return;
            throw new Exception("[line:" + lineNumber + "] Unknown command!\n> " + line);
        }

        StringBuilder anonymousContent = null;

        if(line.endsWith("{")) {

            int anonymousStartLineNumber = lineNumber;

            anonymousContent = new StringBuilder();
            String anonymousLine;

            int figureBracketStack = 0;
            while((anonymousLine = reader.readLine()) != null) {

                lineNumber += 1;

                if((anonymousLine = anonymousLine.strip()).endsWith("{")) {
                    figureBracketStack += 1;
                }

                if((anonymousLine = anonymousLine.strip()).equals("#! }") || anonymousLine.equals("! }")) {
                    if(figureBracketStack-- == 0) break;
                }

                anonymousContent.append(anonymousLine).append('\n');
            }

            if(anonymousLine == null) {
                throw new Exception("[line:" + anonymousStartLineNumber + "] Anonymous function wasn't closed!\n> " + line);
            }
        }

        while(line.contains("  ")) {
            line = line.replace("  ", " ");
        }

        try {
            command.execute(out, Arrays.copyOfRange(split, 1, split.length),
                    anonymousContent == null ? null : anonymousContent.toString(),
                    inFile, outFile, anonymousFiles);
        } catch(Exception e) {
            throw new Exception("[line:" + lineNumber + "] " + e.getMessage() + "\n> " + line);
        }
    }

    public List<File> getAnonymousFiles() {
        return anonymousFiles;
    }
}
