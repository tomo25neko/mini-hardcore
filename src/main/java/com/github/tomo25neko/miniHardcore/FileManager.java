package com.github.tomo25neko.miniHardcore;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

import static org.bukkit.Bukkit.getLogger;

public class FileManager {

    private final File file;
    private Set<String> data;
    private final Gson gson = new Gson();

    public FileManager(File path, String fileName) {
        this.file = new File(path,fileName);
        load(); // 読み込み + なければ作成
    }

    public void load() {
        try {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            if (!file.exists()) {
                data = new HashSet<>();
                save(); // 空のファイルを作成
                return;
            }

            try (FileReader reader = new FileReader(file, StandardCharsets.UTF_8)) {
                Type type = new TypeToken<Set<String>>() {}.getType();
                Set<String> loaded = gson.fromJson(reader, type);
                data = (loaded != null) ? loaded : new HashSet<>();
            }

        } catch (IOException e) {
            getLogger().severe("Failed to load " + file.getName() + ": " + e.getMessage());
        }
    }

    public void save() {
        try (FileWriter writer = new FileWriter(file, StandardCharsets.UTF_8)) {
            gson.toJson(data, writer);
        } catch (IOException e) {
            getLogger().severe("Failed to save " + file.getName() + ": " + e.getMessage());
        }
    }

    //使う？
    public Set<String> getData() {
        return data;
    }

    public boolean add(String entry) {
        boolean added = data.add(entry);
        if (added) save();
        return added;
    }

    public boolean remove(String entry) {
        boolean removed = data.remove(entry);
        if (removed) save();
        return removed;
    }


    public boolean contains(String entry) {
        return data.contains(entry);
    }
}
