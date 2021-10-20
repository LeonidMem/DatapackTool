package ru.leonidm.datapacktool.utils;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import ru.leonidm.datapacktool.commands.Build;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class DatapackUtils {
    public static String getFunctionMinecraftFormattedName(File file) {
        String namespace;
        File outDirectory = file;
        while(true) {
            if(outDirectory.getParentFile().getName().equalsIgnoreCase("data")) {
                namespace = outDirectory.getName();
                break;
            }
            outDirectory = outDirectory.getParentFile();
        }

        String regex2 = Utils.fileSeparator + "data" + Utils.fileSeparator + namespace + Utils.fileSeparator + "functions";

        String[] split = file.getParent().split(regex2, 2);
        String directory;
        if(split.length != 2) directory = "";
        else {
            directory = split[1].replace("\\", "/");
            if(directory.startsWith("/")) directory = directory.substring(1);
            if(!directory.equals("")) directory += "/";
        }

        return namespace + ":" + directory + file.getName().substring(0, file.getName().length() - 11);
    }

    public static void addValueToTag(File outFile, String minecraftFormattedTag, String category, String value) throws Exception {
        String[] splitTag = minecraftFormattedTag.split(":");
        if(splitTag.length != 2) throw new Exception("Illegal tag!");

        File directory = outFile;
        while(!(directory = directory.getParentFile()).getName().equalsIgnoreCase("data"));

        File tagFile = new File(directory, splitTag[0] + Utils.fileSeparator + "tags" + Utils.fileSeparator + category +
                Utils.fileSeparator + splitTag[1].replace("/", Utils.fileSeparator) + ".json");

        tagFile.getParentFile().mkdirs();
        if(tagFile.createNewFile()) {
            OutputStream outputStream = new FileOutputStream(tagFile);
            outputStream.write(("{\"values\":[\"" + value + "\"]}").getBytes(StandardCharsets.UTF_8));
            outputStream.close();
            return;
        }

        String content = Files.readString(tagFile.toPath());

        JSONObject jsonObject = (JSONObject) JSONValue.parse(content);
        JSONArray values = (JSONArray) jsonObject.get("values");

        if(values.contains(value)) return;

        values.add(value);
        jsonObject.put("values", values);

        tagFile.delete();
        tagFile.createNewFile();

        OutputStream outputStream = new FileOutputStream(tagFile);
        outputStream.write(jsonObject.toJSONString().getBytes(StandardCharsets.UTF_8));
        outputStream.close();
    }

    public static String createAnonymousFunction(File outFile, String anonymousFunctionContent) throws IOException {

        File anonymousFile = new File(outFile.getParentFile(), outFile.getName().substring(0, outFile.getName().length() - 11) + anonymousFunctionContent.hashCode() + ".mcfunction");
        OutputStream outputStream = new FileOutputStream(anonymousFile);
        outputStream.write(anonymousFunctionContent.getBytes(StandardCharsets.UTF_8));
        outputStream.close();

        Build.addAnonymousFunction(anonymousFile);

        return getFunctionMinecraftFormattedName(anonymousFile);
    }
}
