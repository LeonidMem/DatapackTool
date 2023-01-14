package ru.leonidm.datapacktool.managers;

import ru.leonidm.datapacktool.entities.BuildParameter;

import java.util.HashMap;
import java.util.Map;

public class ParameterManager {

    private final static Map<String, BuildParameter> parameters = new HashMap<>();

    /**
     * Register parameter
     *
     * @param parameter
     */
    public static void registerParameter(BuildParameter parameter) {
        parameters.put(parameter.getLabel(), parameter);
    }

    public static BuildParameter getParameter(String label) {
        return parameters.get(label);
    }

}
