package ari.superarilo.tool;

import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

public class ObjectConvert {
    private final LoaderOptions loaderOptions = new LoaderOptions();
    public ObjectConvert() {
        this.loaderOptions.setAllowRecursiveKeys(true);
        this.loaderOptions.setAllowDuplicateKeys(false);
    }

    public <T> T yamlConvertToObj(String raw, Class<T> clazz) {
        try {
            return new Yaml(new Constructor(clazz, this.loaderOptions)).load(raw);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to convert YAML to object: " + e.getMessage(), e);
        }
    }
}
