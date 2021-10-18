package ru.leonidm.datapacktool.events;

public class FileParsedEvent extends Event {

    private String content;

    public FileParsedEvent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
