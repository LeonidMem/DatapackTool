package ru.leonidm.datapacktool.events;

public class FilesPreEndedParseEvent extends Event {

    private boolean parseNewAnonymous = false;

    public void setParseNewAnonymous(boolean parseNewAnonymous) {
        this.parseNewAnonymous = parseNewAnonymous;
    }

    public boolean isParseNewAnonymous() {
        return parseNewAnonymous;
    }
}
