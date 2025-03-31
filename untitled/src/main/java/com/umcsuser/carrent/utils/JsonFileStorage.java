package com.umcsuser.carrent.utils;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class JsonFileStorage<T> {
    private final String filename;
    private final Type type;
    private final Gson gson;

    public JsonFileStorage(String filename, Type type) {
        this.filename = filename;
        this.type = type;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    public List<T> load() {
        try {
            Path path = Paths.get(filename);
            if (!Files.exists(path)) {
                return Collections.emptyList();
            }
            String json = Files.readString(path);
            return gson.fromJson(json, type);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load data from " + filename, e);
        }
    }

    public void save(List<T> data) {
        try {
            String json = gson.toJson(data);
            Files.writeString(Paths.get(filename), json,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save data to " + filename, e);
        }
    }
}