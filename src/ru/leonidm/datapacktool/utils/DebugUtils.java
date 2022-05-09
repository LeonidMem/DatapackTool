package ru.leonidm.datapacktool.utils;

public class DebugUtils {

    public static void calledFrom(int depth) {
        StackTraceElement[] elements = Thread.getAllStackTraces().get(Thread.currentThread());
        StackTraceElement currentElement = elements[3];
        System.out.println();
        System.out.println("Stacktrace for " + currentElement.getClassName() + "#" + currentElement.getMethodName() + ":");
        for(int i = 4; i < Math.min(4 + depth, elements.length); i++) {
            StackTraceElement element = elements[i];
            System.out.println(" > " + element.getClassName() + "#" + element.getMethodName() + ":" + element.getLineNumber());
        }
        System.out.println();
    }

    public static void calledFrom() {
        StackTraceElement[] elements = Thread.getAllStackTraces().get(Thread.currentThread());
        StackTraceElement currentElement = elements[3];
        System.out.println();
        System.out.println("Stacktrace for " + currentElement.getClassName() + "#" + currentElement.getMethodName() + ":");
        StackTraceElement element = elements[4];
        System.out.println(" > " + element.getClassName() + "#" + element.getMethodName() + ":" + element.getLineNumber());
        System.out.println();
    }
}
