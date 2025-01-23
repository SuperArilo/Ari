package ari.superarilo.tool;

import ari.superarilo.enumType.FunctionType;
import com.google.gson.Gson;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class ObjectConvert {
    private final Gson gson = new Gson();
    private final Yaml yaml;
    public ObjectConvert() {
        LoaderOptions loaderOptions = new LoaderOptions();
        loaderOptions.setAllowRecursiveKeys(true);
        loaderOptions.setAllowDuplicateKeys(false);
        this.yaml = new Yaml(loaderOptions);
    }

    public <T> T yamlConvertToObj(String raw, Type type) {
        Object intermediateObj = yaml.load(raw);
        if (intermediateObj instanceof Map || intermediateObj instanceof List) {
            return gson.fromJson(gson.toJson(intermediateObj), type);
        }
        return gson.fromJson(gson.toJsonTree(intermediateObj), type);
    }
    public FunctionType ItemNBT_TypeCheck(String rawType) {
        if(rawType == null) return null;
        FunctionType type;
        try {
            type = FunctionType.valueOf(rawType.toUpperCase());
            return type;
        } catch (Exception e) {
            Log.debug(Level.INFO, "Function type error", e);
            return null;
        }
    }
}
