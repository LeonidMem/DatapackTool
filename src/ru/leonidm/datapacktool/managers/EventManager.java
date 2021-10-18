package ru.leonidm.datapacktool.managers;

import ru.leonidm.datapacktool.events.BuildListener;
import ru.leonidm.datapacktool.events.Event;
import ru.leonidm.datapacktool.events.EventHandler;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class EventManager {

    private final static HashMap<Class<? extends Event>, Set<Method>> methods = new HashMap<>();
    private final static HashMap<Method, BuildListener> listeners = new HashMap<>();

    public static void registerListener(BuildListener listener) {
        try {
            for(Method method : listener.getClass().getMethods()) {

                EventHandler eventHandler = method.getAnnotation(EventHandler.class);
                if(eventHandler == null) continue;

                Class<?>[] parameterTypes = method.getParameterTypes();
                if(parameterTypes.length != 1) {
                    System.out.println("[EventManager -> " + listener.getClass().getName() + "#" + method.getName() + "] There must be only one parameter!");
                    continue;
                }

                Class<?> parameterType = parameterTypes[0];
                if(!Event.class.isAssignableFrom(parameterType)) {
                    System.out.println("[EventManager -> " + listener.getClass().getName() + "#" + method.getName() + "] Type of the first parameter must extend from Event!");
                    continue;
                }

                methods.computeIfAbsent((Class<? extends Event>) parameterType, k -> new HashSet<>()).add(method);
                listeners.put(method, listener);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void callEvent(Event event) {
        Set<Method> methods = EventManager.methods.get(event.getClass());
        if(methods == null) return;
        for(Method method : methods) {
            try {
                BuildListener listener = listeners.get(method);
                method.invoke(listener, event);
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
}
