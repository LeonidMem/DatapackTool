package ru.leonidm.datapacktool.subcommands;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import ru.leonidm.datapacktool.ModuleLoader;
import ru.leonidm.datapacktool.entities.*;
import ru.leonidm.datapacktool.events.FileParsedEvent;
import ru.leonidm.datapacktool.exceptions.BuildException;
import ru.leonidm.datapacktool.exceptions.FileIgnoreException;
import ru.leonidm.datapacktool.listeners.ExtendedFunctionListener;
import ru.leonidm.datapacktool.utils.FileUtils;
import ru.leonidm.datapacktool.events.FilesParsedEvent;
import ru.leonidm.datapacktool.managers.EventManager;
import ru.leonidm.datapacktool.utils.JSONUtils;
import ru.leonidm.datapacktool.utils.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Watchable;
import java.util.*;
import java.util.stream.Collectors;

public class BuildSubcommand implements SubcommandExecutor {

    private static final Set<File> anonymousFunctions = new HashSet<>();
    private static final List<String> warns = new ArrayList<>();

    public static void addAnonymousFunction(File anonymousFunction) {
        anonymousFunctions.add(anonymousFunction);
    }

    public static void addWarn(String warnMessage) {
        warns.add(warnMessage);
    }

    @Override
    public void run(List<String> args, List<String> keys) {
        if(args.size() > 1) {
            exit();
            return;
        }

        boolean minifyOutput = keys.contains("-minify");

        try {
            long startMillis = System.currentTimeMillis();

            System.out.println("{Stage [1/3]} Getting information from the build.json...");

            String inPath = new File("").getAbsolutePath();
            String outPath;

            File dtoolDirectory = Path.of("dtool/").toAbsolutePath().toFile();

            Set<String> modules = new HashSet<>();

            File dtoolBuild = new File(dtoolDirectory, "build.json");
            if(dtoolBuild.exists()) {
                Object rawJson = JSONValue.parse(new FileReader(dtoolBuild));
                if(!(rawJson instanceof JSONObject))
                    throw new BuildException("\"dtool/build.json\" is wrongly configured!");
                JSONObject jsonObject = (JSONObject) rawJson;

                /* ===== */

                JSONArray jsonArgs = JSONUtils.getArrayNullable(jsonObject, "args", "dtool/build.json");
                if(jsonArgs != null) {
                    keys.addAll(jsonArgs);
                    minifyOutput = keys.contains("-minify");
                }

                /* ===== */

                outPath = JSONUtils.getObject(jsonObject, "out", "dtool/build.json", String.class);

                if(outPath.contains("/")) {
                    outPath = outPath.replace("/", Utils.fileSeparator);
                }
                else {
                    outPath = outPath.replace("\\", Utils.fileSeparator);
                }

                JSONArray jsonModules = JSONUtils.getArrayNullable(jsonObject, "modules", "dtool/build.json");
                if(jsonModules != null) {
                    for(Object rawModule : jsonModules) {
                        String module = rawModule.toString().toLowerCase();
                        if(module.endsWith(".jar")) {
                            modules.add(module);
                        }
                        else {
                            modules.add(module + ".jar");
                        }
                    }
                }
            }
            else {
                throw new BuildException("DTool's environment isn't initialized! Use \"dtool env init\" to do this!");
            }

            System.out.println("\n{Stage [2/3]} Loading the modules...");

            ModuleLoader.loadModules(!minifyOutput, file -> modules.contains(file.getName().toLowerCase()));

            for(String module : modules) {
                if(ModuleLoader.getModule(module.replace(".jar", "")) == null) {
                    throw new BuildException("Module \"" + module + "\" isn't loaded!");
                }
            }

            // TODO: check if sources are going to be deleted
            FileUtils.deleteFilesRecursively(new File(outPath));

            System.out.println("\n{Stage [3/3]} Parsing sources...\n");
            Set<File> files = FileUtils.listFilesRecursively(new File(inPath));

            Set<File> resourcesFiles = new HashSet<>();
            Set<File> functionsFiles = new HashSet<>();

            for(File file : new ArrayList<>(files)) {
                // TODO: remove file only from "root/dtool" directory
                if(file.getParentFile().getName().equals("dtool")) {
                    files.remove(file);
                    continue;
                }

                if(file.getName().endsWith(".mcfunction")) {
                    if(!file.getParentFile().equals(dtoolDirectory)) {
                        functionsFiles.add(file);
                    }

                }
                else {
                    resourcesFiles.add(file);
                }

                files.remove(file);
            }

            File dtoolInit = new File(dtoolDirectory, "init.mcfunction");
            if(dtoolInit.exists()) {
                DtoolFunction dtoolFunction = new DtoolFunction(dtoolInit, "globalset");
                dtoolFunction.parse(true);
            }

            EventManager.registerListener(new ExtendedFunctionListener());

            parseFiles(inPath, outPath, functionsFiles, minifyOutput);

            if(keys.contains("-pr")) {
                if(!minifyOutput) System.out.println();
                System.out.println("{Stage [3/3]} Parsing resources...\n");

                for(File resource : resourcesFiles) {
                    parseResources(resource, inPath, outPath, minifyOutput);
                }
            }
            else {
                for(File resourceFile : resourcesFiles) {
                    FileUtils.copy(resourceFile, new File(resourceFile.getAbsolutePath().replace(inPath, outPath)));
                }
            }

            FilesParsedEvent filesParsedEvent = new FilesParsedEvent();
            filesParsedEvent.call();

            if(filesParsedEvent.isParseNewAnonymous()) {
                parseFiles(inPath, outPath, anonymousFunctions, minifyOutput);
            }

            long endMillis = System.currentTimeMillis() ;

            if(!minifyOutput) System.out.println();
            System.out.println("=====\nSuccessfully built in " + (double) (endMillis - startMillis) / 1000 + " sec.!\n");

            if(!warns.isEmpty()) {
                System.out.println("\n[!] Got " + warns.size() + " warns:\n");
                for(String warn : warns) {
                    System.out.println("> " + warn);
                }
                System.out.println();
            }

        } catch(BuildException e) {
            if(!minifyOutput) System.err.println();
            System.err.println("=====");
            System.err.println(e.getMessage());
            if(keys.contains("-debug")) e.printStackTrace();
            System.err.println("\nError occurred! Building was cancelled!\n");
        } catch(Exception e) {
            if(!minifyOutput) System.err.println();
            System.err.println("=====");
            e.printStackTrace();
            System.err.println("\nError occurred! Building was cancelled!\n");
        }
    }

    private void parseFiles(String inPath, String outPath, Set<File> files, boolean minifyOutput) throws Exception {
        for(File file : new ArrayList<>(files)) {
            File outFile = new File(file.getAbsolutePath().replace(inPath, outPath));

            try {
                McFunction mcFunction = new McFunction(file, outFile);
                mcFunction.parse(minifyOutput);
                mcFunction.save();

                files.remove(file);
                parseFiles(inPath, outPath, anonymousFunctions, minifyOutput);
            }
            catch(FileIgnoreException ignored) {

            }
        }
    }

    private void parseResources(File inFile, String inPath, String outPath, boolean minifyOutput) throws Exception {
        Path inPath1 = Path.of(inFile.getAbsolutePath());
        if(!minifyOutput) System.out.println("Parsing " + inPath1 + "...");
        Path outPath1 = Path.of(inFile.getAbsolutePath().replace(inPath, outPath));
        String content = Files.readString(inPath1, StandardCharsets.UTF_8);

        File outFile = outPath1.toFile();
        outFile.getParentFile().mkdirs();

        // TODO: rewrite
        FileParsedEvent fileParsedEvent = new FileParsedEvent(inFile, outFile,
                FileParsedEvent.FileType.RESOURCE, content);
        fileParsedEvent.call();

        FileOutputStream outputStream = new FileOutputStream(outFile);
        outputStream.write(fileParsedEvent.getContent().getBytes(StandardCharsets.UTF_8));
        outputStream.close();
    }

    @Override
    public String info() {
        return "  build [id] - build project\n" +
               "    -pr - parse resources\n" +
               "    -minify - minify output";
    }
}

