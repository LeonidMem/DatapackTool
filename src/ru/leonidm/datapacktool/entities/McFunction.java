package ru.leonidm.datapacktool.entities;

import ru.leonidm.datapacktool.events.FileParsedEvent;
import ru.leonidm.datapacktool.events.LinePreParseEvent;
import ru.leonidm.datapacktool.managers.CommandManager;
import ru.leonidm.datapacktool.managers.EventManager;
import ru.leonidm.datapacktool.managers.ParameterManager;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class McFunction {

    private final File inFile, outFile;
    private final StringBuilder out;
    private int lineNumber = 0;

    public McFunction(File inFile, File outFile) {
        this.inFile = inFile;
        outFile.getParentFile().mkdirs();
        this.outFile = outFile;
        this.out = new StringBuilder();
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

        boolean isCommand;

        if(line.startsWith("! ") || line.startsWith("#! ")) {
            isCommand = true;
        }
        else if(line.startsWith("$ ") || line.startsWith("#$ ")) {
            isCommand = false;
        }
        else {
            LinePreParseEvent linePreParseEvent = new LinePreParseEvent(line);
            EventManager.callEvent(linePreParseEvent);
            line = linePreParseEvent.getContent();

            out.append(line).append('\n');
            return;
        }

        line = line.substring(line.startsWith("#") ? 3 : 2).strip();

        while(line.contains("  ")) {
            line = line.replace("  ", " ");
        }

        LinePreParseEvent linePreParseEvent = new LinePreParseEvent(line);
        EventManager.callEvent(linePreParseEvent);
        line = linePreParseEvent.getContent();

        String[] split = line.split(" ");

        if(!isCommand) {
            BuildParameter parameter = ParameterManager.getParameter(split[0]);

            if(parameter == null) {
                throw new Exception("[line:" + lineNumber + "] Unknown parameter!\n> " + line);
            }

            try {
                parameter.execute(Arrays.copyOfRange(split, 1, split.length), inFile, outFile);
            } catch(Exception e) {
                throw new Exception("[line:" + lineNumber + "] " + e.getMessage() + "\n> " + line);
            }

            return;
        }

        BuildCommand command = CommandManager.getCommand(split[0]);
        if(command == null) {
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

        try {
            command.execute(out, Arrays.copyOfRange(split, 1, split.length),
                    anonymousContent == null ? null : anonymousContent.toString(),
                    inFile, outFile);
        } catch(Exception e) {
            throw new Exception("[line:" + lineNumber + "] " + e.getMessage() + "\n> " + line);
        }
    }
}
