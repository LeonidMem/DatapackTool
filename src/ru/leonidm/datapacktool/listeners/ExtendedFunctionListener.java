package ru.leonidm.datapacktool.listeners;

import ru.leonidm.datapacktool.exceptions.BuildException;
import ru.leonidm.datapacktool.events.BuildListener;
import ru.leonidm.datapacktool.events.EventHandler;
import ru.leonidm.datapacktool.events.LineParsedEvent;
import ru.leonidm.datapacktool.utils.DatapackUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExtendedFunctionListener implements BuildListener {

    private static final Pattern extendedFunctionPattern =
            Pattern.compile("function ([a-z0-9._-]+:[a-z0-9/._-]+)((?: (?:\".+\"|[^ ]+))+)");

    private static final Pattern functionArgumentsPattern =
            Pattern.compile("(\"[^\"]*\"|[^ ]+)|\\h+");

    private static final Pattern argVarPattern =
            Pattern.compile("([^\\\\]|)%\\d+");

    @EventHandler
    public void onLineParse(LineParsedEvent event) throws Exception {
        String content = event.getContent();

        Matcher matcher = extendedFunctionPattern.matcher(content);
        if (!matcher.matches()) return;

        String functionName = matcher.group(1);
        File functionFile = DatapackUtils.getFunctionFromName(event.getInFile(), functionName);

        if (!functionFile.exists())
            throw new BuildException("Function \"" + functionName + "\" doesn't exist!");

        String match = matcher.group(2);
        Matcher matcher1 = functionArgumentsPattern.matcher(match);

        Map<Integer, String> args = new HashMap<>();

        int index = 0;
        while (matcher1.find()) {
            String arg = matcher1.group(0);
            if (arg.equals(" ")) continue;

            if (arg.charAt(0) == '"' && arg.charAt(arg.length() - 1) == '"') {
                args.put(index++, arg.substring(1, arg.length() - 1));
            } else {
                args.put(index++, arg);
            }
        }

        String functionContent = Files.readString(Path.of(functionFile.getAbsolutePath()));

        // TODO: use RegEx
        for (Map.Entry<Integer, String> entry : args.entrySet()) {
            functionContent = functionContent.replace("%" + entry.getKey(), entry.getValue());
        }

        Matcher matcher2 = argVarPattern.matcher(functionContent);
        while (matcher2.find()) {
            if (!matcher2.group(0).startsWith(" ")) continue;

            throw new BuildException("Can't fill argument \"" + matcher2.group(0).substring(1)
                    + "\" in function \"" + functionName + "\"!");
        }

        functionContent = functionContent.replace("\\%", "%");

        // TODO: fix normally
        functionContent = functionContent.replaceAll("(|#)\\$\\s*ignore\\s*", "");

        String newFunctionName = DatapackUtils.createAnonymousFunction(event.getOutFile(), functionContent);

        event.setContent("function " + newFunctionName);
    }
}
