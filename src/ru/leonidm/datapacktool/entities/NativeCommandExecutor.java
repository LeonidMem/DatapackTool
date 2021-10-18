package ru.leonidm.datapacktool.entities;

import java.util.List;

public interface NativeCommandExecutor {

    void run(List<String> args, List<String> keys);

    String info();

    default void exit() {
        System.out.println("Incorrect arguments!\n" + info());
    }
}
