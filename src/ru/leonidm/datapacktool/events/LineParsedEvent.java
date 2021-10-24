package ru.leonidm.datapacktool.events;

/**
 * Calls when a line was parsed
 */
public class LineParsedEvent extends Event {

    private String content;

    public LineParsedEvent(String content) {
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
