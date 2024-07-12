package ari.superarilo.tool;

import com.google.gson.Gson;
import org.json.simple.JSONObject;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;

import java.util.Map;

public class ObjectConvert {
    private final Yaml yaml;
    private final Gson gson = new Gson();

    public ObjectConvert() {
        LoaderOptions loaderOptions = new LoaderOptions();
        loaderOptions.setAllowRecursiveKeys(true);
        loaderOptions.setAllowDuplicateKeys(false);
        this.yaml = new Yaml(loaderOptions);
    }

    public <T> T convert(Object raw, Class<T> clazz) {
        return this.gson.fromJson(this.gson.toJsonTree(raw), clazz);
    }
    public <T> T yamlConvertToObj(String raw, Class<T> clazz) {
        return this.gson.fromJson(this.gson.toJsonTree(this.yaml.load(raw)), clazz);
    }
}
