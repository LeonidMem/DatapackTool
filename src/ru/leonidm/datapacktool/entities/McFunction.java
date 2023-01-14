package ru.leonidm.datapacktool.entities;

import ru.leonidm.datapacktool.build.parameters.BuildIgnoreExecutor;
import ru.leonidm.datapacktool.events.FileParsedEvent;
import ru.leonidm.datapacktool.events.LineParsedEvent;
import ru.leonidm.datapacktool.exceptions.BuildException;
import ru.leonidm.datapacktool.exceptions.FileIgnoreException;
import ru.leonidm.datapacktool.managers.CommandManager;
import ru.leonidm.datapacktool.managers.ParameterManager;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class McFunction {

    private final File inFile, outFile;
    private final StringBuilder out;
    private int lineNumber = 0;
    private String finalContent = null;

    public McFunction(File inFile, File outFile) {
        this.inFile = inFile;
        this.outFile = outFile;
        this.out = new StringBuilder();
    }

    public void parse(boolean minifyOutput) throws Exception {
        InputStream inputStream = new FileInputStream(inFile);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        if (!minifyOutput) System.out.println("Parsing " + inFile.getAbsolutePath() + "...");

        while (reader.ready()) {
            parseLine(reader);
        }

        FileParsedEvent fileParsedEvent = new FileParsedEvent(inFile, outFile,
                FileParsedEvent.FileType.SOURCE, out.toString());
        fileParsedEvent.call();
        finalContent = fileParsedEvent.getContent();
    }

    public void save() throws Exception {
        if (finalContent == null) throw new BuildException("Function wasn't parsed!");

        outFile.getParentFile().mkdirs();
        OutputStream outputStream = new FileOutputStream(outFile);
        outputStream.write(finalContent.getBytes(StandardCharsets.UTF_8));
        outputStream.close();
    }

    private void parseLine(BufferedReader reader) throws Exception {
        lineNumber += 1;
        String line = reader.readLine().strip();

        try {
            boolean isCommand;

            if (line.startsWith("!") || line.startsWith("#!")) {
                isCommand = true;
            } else if (line.startsWith("$") || line.startsWith("#$")) {
                isCommand = false;
            } else {
                if (line.startsWith("#%")) {
                    line = line.substring(2).strip();
                }

                LineParsedEvent lineParsedEvent = new LineParsedEvent(inFile, outFile, line, lineNumber);
                lineParsedEvent.call();

                line = lineParsedEvent.getContent();

                if (!line.isBlank() && !line.startsWith("#")) {
                    out.append(line).append('\n');
                }

                return;
            }

            line = line.substring(line.startsWith("#") ? 2 : 1).strip();

            while (line.contains("  ")) {
                line = line.replace("  ", " ");
            }

            LineParsedEvent lineParsedEvent = new LineParsedEvent(inFile, outFile, line, lineNumber);
            lineParsedEvent.call();
            line = lineParsedEvent.getContent();

            String[] split = line.split(" ");

            if (!isCommand) {
                BuildParameter parameter = ParameterManager.getParameter(split[0]);

                if (parameter == null) {
                    throw new BuildException("[line:" + lineNumber + "] Unknown parameter!\n> " + line);
                }

                try {
                    List<String> arguments = new ArrayList<>(Arrays.asList(split).subList(1, split.length));

                    executeParameter(parameter, arguments);
                } catch (FileIgnoreException e) {
                    throw e;
                } catch (Exception e) {
                    Throwable cause = e.getCause();
                    if (cause != null)
                        throw new BuildException("[line:" + lineNumber + "] " + cause.getMessage() + "\n> " + line, cause);

                    throw new BuildException("[line:" + lineNumber + "] " + e.getMessage() + "\n> " + line, e);
                }

                return;
            }

            BuildCommand command = CommandManager.getCommand(split[0]);
            if (command == null) {
                throw new BuildException("[line:" + lineNumber + "] Unknown command!\n> " + line);
            }

            StringBuilder anonymousContent = null;


            List<String> arguments = new ArrayList<>(Arrays.asList(split).subList(1, split.length));

            if (line.endsWith(" {")) {

                arguments.remove("{");

                int anonymousStartLineNumber = lineNumber;

                anonymousContent = new StringBuilder();
                String anonymousLine;

                int figureBracketStack = 0;
                while ((anonymousLine = reader.readLine()) != null) {

                    lineNumber += 1;

                    if ((anonymousLine = anonymousLine.strip()).endsWith(" {")) {
                        figureBracketStack += 1;
                    }

                    if ((anonymousLine = anonymousLine.strip()).equals("#! }") || anonymousLine.equals("! }")) {
                        if (figureBracketStack-- == 0) break;
                    }

                    anonymousContent.append(anonymousLine).append('\n');
                }

                if (anonymousLine == null) {
                    throw new BuildException("[line:" + anonymousStartLineNumber + "] Anonymous function wasn't closed!\n> " + line);
                }
            }

            executeCommand(command, arguments, anonymousContent == null ? null : anonymousContent.toString());
        } catch (FileIgnoreException e) {
            throw e;
        } catch (Exception e) {
            Throwable cause = e.getCause();
            if (cause != null)
                throw new BuildException("[line:" + lineNumber + "] " + cause.getMessage() + "\n> " + line, cause);

            throw new BuildException("[line:" + lineNumber + "] " + e.getMessage() + "\n> " + line, e);
        }
    }

    protected void executeParameter(BuildParameter parameter, List<String> args) throws Exception {
        parameter.execute(args, inFile, outFile);
    }

    protected void executeCommand(BuildCommand command, List<String> args, String anonymousFunctionContent) throws Exception {
        command.execute(out, args, anonymousFunctionContent, inFile, outFile);
    }
}
