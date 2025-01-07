package ari.superarilo.tool;

import com.google.gson.Gson;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;

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
        try {
            Object yamlData = this.yaml.load(raw);
            return this.gson.fromJson(this.gson.toJsonTree(yamlData), clazz);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to convert YAML to object: " + e.getMessage(), e);
        }
    }
}
