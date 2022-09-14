package ru.leonidm.datapacktool.utils;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import ru.leonidm.datapacktool.exceptions.BuildException;
import ru.leonidm.datapacktool.subcommands.BuildSubcommand;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class DatapackUtils {

    /**
     * Returns minecraft formatted name of the function,
     * for example
     *
     * @param file File of the function
     * @return Minecraft formatted name of the function
     */
    public static String getFunctionName(File file) {
        String namespace;
        File outDirectory = file;
        while(true) {
            if(outDirectory.getParentFile().getName().equalsIgnoreCase("data")) {
                namespace = outDirectory.getName();
                break;
            }
            outDirectory = outDirectory.getParentFile();
        }

        String regex2 = Utils.regexFileSeparator + "data" + Utils.regexFileSeparator + namespace + Utils.regexFileSeparator + "functions";

        String[] split = file.getParent().split(Pattern.quote(regex2), 2);
        String directory;
        if(split.length != 2) directory = "";
        else {
            directory = split[1].replace("\\", "/");
            if(directory.startsWith("/")) directory = directory.substring(1);
            if(!directory.equals("")) directory += "/";
        }

        return namespace + ":" + directory + file.getName().substring(0, file.getName().length() - 11);
    }

    public static File getFunctionFromName(File inFile, String minecraftFormattedTag) {
        String[] splitTag = minecraftFormattedTag.split(":");
        if(splitTag.length != 2)
            throw new BuildException("Wrong formatted tag \"" + minecraftFormattedTag + "\"!");

        File directory = inFile;
        while(!(directory = directory.getParentFile()).getName().equalsIgnoreCase("data"));

        return new File(directory, splitTag[0] + Utils.fileSeparator + "functions" + Utils.fileSeparator
                + splitTag[1].replace("/", Utils.fileSeparator) + ".mcfunction");
    }

    /**
     * Adds value to the Minecraft tag
     * @param outFile Out file
     * @param minecraftFormattedTag Minecraft formatted tag (f.e. "minecraft:load")
     * @param category Minecraft category ("blocks", "functions" and other)
     * @param value Value to add
     * @throws Exception
     */
    public static void addValueToTag(File outFile, String minecraftFormattedTag, String category, String value) throws Exception {
        String[] splitTag = minecraftFormattedTag.split(":");
        if(splitTag.length != 2) throw new BuildException("Illegal tag \"" + minecraftFormattedTag + "\"!");

        File directory = outFile;
        while(!(directory = directory.getParentFile()).getName().equalsIgnoreCase("data"));

        File tagFile = new File(directory, splitTag[0] + Utils.fileSeparator + "tags" + Utils.fileSeparator
                + category + Utils.fileSeparator + splitTag[1].replace("/", Utils.fileSeparator) + ".json");

        tagFile.getParentFile().mkdirs();
        if(tagFile.createNewFile()) {
            OutputStream outputStream = new FileOutputStream(tagFile);
            outputStream.write(("{\"values\":[\"" + value + "\"]}").getBytes(StandardCharsets.UTF_8));
            outputStream.close();
            return;
        }

        Object rawJson = JSONValue.parse(new FileReader(tagFile));
        if(!(rawJson instanceof JSONObject))
            throw new BuildException("Tag \"" + minecraftFormattedTag + "\" is wrongly configured!");

        JSONObject jsonObject = (JSONObject) rawJson;

        Object rawValues = jsonObject.get("values");
        JSONArray values;
        if(rawValues == null) {
            values = new JSONArray();
        }
        else {
            if(!(rawValues instanceof JSONArray))
                throw new BuildException("Tag \"" + minecraftFormattedTag + "\" is wrongly configured!");

            values = (JSONArray) rawValues;
        }

        if(values.contains(value)) return;

        values.add(value);
        jsonObject.put("values", values);

        tagFile.delete();
        tagFile.createNewFile();

        OutputStream outputStream = new FileOutputStream(tagFile);
        outputStream.write(jsonObject.toJSONString().getBytes(StandardCharsets.UTF_8));
        outputStream.close();
    }

    /**
     * Creates anonymous function in the datapack
     * @param outFile Out file
     * @param anonymousFunctionContent Content of the anonymous function
     * @return Minecraft formatted name of the anonymous function
     * @throws IOException
     */
    public static String createAnonymousFunction(File outFile, String anonymousFunctionContent) throws IOException {
        File anonymousFile = new File(outFile.getParentFile(),
                outFile.getName().substring(0, outFile.getName().length() - 11)
                        + anonymousFunctionContent.hashCode() + ".mcfunction");

        anonymousFile.mkdirs();

        OutputStream outputStream = new FileOutputStream(anonymousFile);
        outputStream.write(anonymousFunctionContent.getBytes(StandardCharsets.UTF_8));
        outputStream.close();

        BuildSubcommand.addAnonymousFunction(anonymousFile);

        return getFunctionName(anonymousFile);
    }
}
