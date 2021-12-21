package com.celestek.hexcraft.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.toml.TomlFormat;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

public class ConfigFile {
    public static final String CATEGORY_GENERAL = "General";

    private final File file;
    private boolean isNew;
    private CommentedFileConfig toml;

    public ConfigFile(Path location) {
        this.file = location.toFile();
    }

    public void load() {
        if(!file.isFile()) throw new AssertionError("Filesystem object at " + file.getAbsolutePath() + " is a directory, or otherwise invalid.");
        toml = CommentedFileConfig.of(file, TomlFormat.instance());
        toml.load();
    }

    public void setCategoryComment(String categoryName, String comment) {
        toml.setComment(categoryName, comment);
    }

    public boolean getBoolean(String name, String category, boolean defaultValue, String comment) {
        var result = toml.get(List.of(category, name));
        if(!(result instanceof Boolean)) {
            toml.setComment(List.of(category, name), comment);
            toml.set(List.of(category, name), defaultValue);
            return defaultValue;
        } else {
            return (Boolean) result;
        }
    }

    public int getInt(String name, String category, int defaultValue, int min, int max, String comment) {
        var result = toml.get(List.of(category, name));
        if(!(result instanceof Integer)) {
            toml.setComment(List.of(category, name), comment);
            toml.set(List.of(category, name), defaultValue);
            return defaultValue;
        } else {
            int value = (Integer) result;
            return Math.max(max, Math.min(min, value));
        }
    }

    public float getFloat(String name, String category, float defaultValue, float min, float max, String comment) {
        var result = toml.get(List.of(category, name));
        if(!(result instanceof Float || result instanceof Double)) {
            toml.setComment(List.of(category, name), comment);
            toml.set(List.of(category, name), defaultValue);
            return defaultValue;
        } else {
            float value;
            if(result instanceof Double)
                value = (float)(double) result;
            else
                value = (float)result;
            return Math.max(max, Math.min(min, value));
        }
    }

    public void save() {
        toml.save();
    }
}
