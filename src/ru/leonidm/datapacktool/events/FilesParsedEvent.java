package ru.leonidm.datapacktool.events;

/**
 * Calls when all files were parsed
 */
public class FilesParsedEvent extends Event {

    private boolean parseNewAnonymous = false;

    /**
     * If true DTool will parse new anonymous files, which can be added
     * with {@link ru.leonidm.datapacktool.utils.DatapackUtils#createAnonymousFunction(java.io.File, String) createAnonymousFunction}
     *
     * Useful when you need to create the file in the end of the building
     * @param parseNewAnonymous
     */
    public void setParseNewAnonymous(boolean parseNewAnonymous) {
        this.parseNewAnonymous = parseNewAnonymous;
    }

    public boolean isParseNewAnonymous() {
        return parseNewAnonymous;
    }
}
