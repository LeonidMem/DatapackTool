package ru.leonidm.datapacktool.events;

public class LinePreParseEvent extends Event {

    private String content;

    public LinePreParseEvent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
