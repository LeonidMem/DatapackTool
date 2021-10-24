package ru.leonidm.datapacktool.subcommands;

import ru.leonidm.datapacktool.entities.SubcommandExecutor;
import ru.leonidm.datapacktool.utils.Utils;

import java.util.List;

public class HelpSubcommand implements SubcommandExecutor {

    @Override
    public void run(List<String> args, List<String> keys) {
        Utils.printHelp();
    }

    @Override
    public String info() {
        return "  help h ? - show this list";
    }
}
