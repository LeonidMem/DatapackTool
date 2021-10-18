package ru.leonidm.datapacktool.commands;

import ru.leonidm.datapacktool.Utils;
import ru.leonidm.datapacktool.entities.NativeCommandExecutor;

import java.util.List;

public class Help implements NativeCommandExecutor {

    @Override
    public void run(List<String> args, List<String> keys) {
        Utils.printHelp();
    }

    @Override
    public String info() {
        return "  help h ? - show this list";
    }
}
