package com.github.tomo25neko.miniHardcore.file;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

import static org.bukkit.Bukkit.getLogger;

public class PlayerFileManager {

    private final File file;
    private Set<String> playerData;
    private final Gson gson = new Gson();

    public PlayerFileManager(File path, String fileName) {
        this.file = new File(path, fileName);
        load();
    }

    public void load() {
        try {
            if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
            if (!file.exists()) {
                playerData = new HashSet<>();
                save();
                return;
            }

            try (Reader reader = new FileReader(file, StandardCharsets.UTF_8)) {
                Type type = new TypeToken<Set<String>>() {}.getType();
                Set<String> loaded = gson.fromJson(reader, type);
                playerData = (loaded != null) ? loaded : new HashSet<>();
            }
        } catch (IOException e) {
            getLogger().severe("Failed to load " + file.getName() + ": " + e.getMessage());
        }
    }

    public void save() {
        try (Writer writer = new FileWriter(file, StandardCharsets.UTF_8)) {
            gson.toJson(playerData, writer);
        } catch (IOException e) {
            getLogger().severe("Failed to save " + file.getName() + ": " + e.getMessage());
        }
    }

    public Set<String> getPlayerData() {
        return playerData;
    }

    public boolean addPlayer(String name) {
        boolean added = playerData.add(name);
        if (added) save();
        return added;
    }

    public boolean removePlayer(String name) {
        boolean removed = playerData.remove(name);
        if (removed) save();
        return removed;
    }

    public boolean containsPlayer(String name) {
        return playerData.contains(name);
    }
}
