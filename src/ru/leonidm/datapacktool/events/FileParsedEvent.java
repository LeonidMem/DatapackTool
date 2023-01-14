package ru.leonidm.datapacktool.events;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

/**
 * Calls when file was parsed
 */
// TODO: probably create ParsedEvent and extends from it
public class FileParsedEvent extends Event {

    private final File inFile;
    private final File outFile;
    private final FileType fileType;
    private String content;

    public FileParsedEvent(@NotNull File inFile, @Nullable File outFile, @NotNull FileType fileType,
                           @NotNull String content) {
        this.inFile = inFile;
        this.outFile = outFile;
        this.fileType = fileType;
        this.content = content;
    }

    @NotNull
    public File getInFile() {
        return inFile;
    }

    @Nullable
    public File getOutFile() {
        return outFile;
    }

    @NotNull
    public FileType getFileType() {
        return fileType;
    }

    @NotNull
    public String getContent() {
        return content;
    }

    /**
     * Change content which will be written in the out file
     *
     * @param content
     */
    public void setContent(@NotNull String content) {
        this.content = content;
    }

    public enum FileType {
        SOURCE, RESOURCE
    }
}
