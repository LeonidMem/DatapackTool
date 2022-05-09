package ru.leonidm.datapacktool.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import ru.leonidm.datapacktool.entities.BuildException;

public class JSONUtils {

    @NotNull
    public static JSONObject getObject(@NotNull JSONObject jsonObject, @NotNull String key,
                                       @Nullable String fileName) {
        Object rawJson = jsonObject.get(key);
        if(!(rawJson instanceof JSONObject))
            throw getException(key, fileName, rawJson == null);

        return (JSONObject) rawJson;
    }

    @Nullable
    public static JSONObject getObjectNullable(@NotNull JSONObject jsonObject, @NotNull String key,
                                               @Nullable String fileName) {
        Object rawJson = jsonObject.get(key);
        if(rawJson == null) return null;

        if(!(rawJson instanceof JSONObject))
            throw getException(key, fileName);

        return (JSONObject) rawJson;
    }

    @NotNull
    public static JSONArray getArray(@NotNull JSONObject jsonObject, @NotNull String key,
                                     @Nullable String fileName) {
        Object rawJson = jsonObject.get(key);
        if(!(rawJson instanceof JSONArray))
            throw getException(key, fileName, rawJson == null);

        return (JSONArray) rawJson;
    }

    @Nullable
    public static JSONArray getArrayNullable(@NotNull JSONObject jsonObject, @NotNull String key,
                                             @Nullable String fileName) {
        Object rawJson = jsonObject.get(key);
        if(rawJson == null) return null;

        if(!(rawJson instanceof JSONArray))
            throw getException(key, fileName);

        return (JSONArray) rawJson;
    }

    @NotNull
    public static <T> T getObject(@NotNull JSONObject jsonObject, @NotNull String key,
                                  @Nullable String fileName, Class<T> tClass) {
        Object rawObject = jsonObject.get(key);
        if(rawObject == null || !tClass.isAssignableFrom(rawObject.getClass()))
            throw getException(key, fileName, rawObject == null);

        return (T) rawObject;
    }

    @Nullable
    public static <T> T getObjectNullable(@NotNull JSONObject jsonObject, @NotNull String key,
                                          @Nullable String fileName, Class<T> tClass) {
        Object rawObject = jsonObject.get(key);
        if(rawObject == null) return null;

        if(!tClass.isAssignableFrom(rawObject.getClass()))
            throw getException(key, fileName);

        return (T) rawObject;
    }

    @NotNull
    private static BuildException getException(@NotNull String key, @Nullable String fileName) {
        return getException(key, fileName, false);
    }

    @NotNull
    private static BuildException getException(@NotNull String key, @Nullable String fileName, boolean isNullException) {
        if(isNullException) {
            if(fileName == null) return new BuildException("Field \"" + key + "\" must be configured!");

            return new BuildException("Field \"" + key + "\" in \"" + fileName + "\" + must be configured!");
        }

        if(fileName == null) return new BuildException("Field \"" + key + "\" is wrongly configured!");

        return new BuildException("Field \"" + key + "\" in \"" + fileName + "\" is wrongly configured!");
    }
}
