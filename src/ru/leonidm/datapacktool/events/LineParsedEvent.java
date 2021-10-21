package ru.leonidm.datapacktool.events;

/**
 *
 */
public class LineParsedEvent extends Event {

    private String content;

    public LineParsedEvent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
