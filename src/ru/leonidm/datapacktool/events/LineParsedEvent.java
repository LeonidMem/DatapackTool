package ru.leonidm.datapacktool.events;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

/**
 * Calls when a line was parsed
 */
public class LineParsedEvent extends Event {

    private final File inFile;
    private final File outFile;
    private String content;
    private final int lineNumber;

    public LineParsedEvent(@NotNull File inFile, @Nullable File outFile, @NotNull String content, int lineNumber) {
        this.inFile = inFile;
        this.outFile = outFile;
        this.content = content;
        this.lineNumber = lineNumber;
    }

    @NotNull
    public String getContent() {
        return content;
    }

    @NotNull
    public File getInFile() {
        return inFile;
    }

    @Nullable
    public File getOutFile() {
        return outFile;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    /**
     * Change content which will be written in the out file
     * @param content
     */
    public void setContent(@NotNull String content) {
        this.content = content;
    }
}
