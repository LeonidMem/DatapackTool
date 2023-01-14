package ru.leonidm.datapacktool.events;

import ru.leonidm.datapacktool.managers.EventManager;

public class Event {

    private boolean called = false;

    public final void call() throws Exception {
        if (called) throw new IllegalStateException("This event was already called!");
        called = true;

        EventManager.callEvent(this);
    }
}
