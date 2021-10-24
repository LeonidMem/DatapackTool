package ru.leonidm.datapacktool.events;

/**
 * Calls when file was parsed
 */
public class FileParsedEvent extends Event {

    private String content;

    public FileParsedEvent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    /**
     * Change content which will be written in the out file
     * @param content
     */
    public void setContent(String content) {
        this.content = content;
    }
}
