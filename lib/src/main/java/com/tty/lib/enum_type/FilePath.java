package com.tty.lib.enum_type;

public enum FilePath implements FilePathEnum {

    Lang("lang/[lang].yml");

    private final String path;

    FilePath(String path) {
        this.path = path;
    }

    @Override
    public String getPath() {
        return this.path;
    }
}
