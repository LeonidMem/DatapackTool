package ru.leonidm.datapacktool.entities;

import java.util.List;

public interface SubcommandExecutor {

    void run(List<String> args, List<String> keys);

    String info();

    default void exit() {
        System.out.println("Incorrect arguments!");

        String info = info();
        if(info != null) System.out.println(info);
    }
}
