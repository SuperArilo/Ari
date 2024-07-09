package ari.superarilo.tool;

import com.google.gson.Gson;

import java.util.Map;

public class ObjectConvert {
    private final Gson gson = new Gson();

    public <T> T mapConvertTo(Map<?, ?> map, Class<T> clazz) {
        return this.gson.fromJson(this.gson.toJsonTree(map), clazz);
    }
}
