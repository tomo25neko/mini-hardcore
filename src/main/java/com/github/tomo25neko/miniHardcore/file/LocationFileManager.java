package com.github.tomo25neko.miniHardcore.file;

import com.google.gson.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import static org.bukkit.Bukkit.getLogger;

public class LocationFileManager {

    private final File file;
    private Map<String, LocationData> locationData;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public LocationFileManager(File path, String fileName) {
        this.file = new File(path, fileName);
        load();
    }

    public void load() {
        try {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            if (!file.exists()) {
                locationData = new HashMap<>();
                save();
                return;
            }

            try (FileReader reader = new FileReader(file, StandardCharsets.UTF_8)) {
                JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
                locationData = new HashMap<>();
                for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                    String name = entry.getKey();
                    JsonObject data = entry.getValue().getAsJsonObject();
                    LocationData location = LocationData.fromJson(data);
                    locationData.put(name, location);
                }
            }

        } catch (IOException e) {
            getLogger().severe("Failed to load " + file.getName() + ": " + e.getMessage());
        }
    }

    public void save() {
        try (FileWriter writer = new FileWriter(file, StandardCharsets.UTF_8)) {
            JsonObject root = new JsonObject();
            for (Map.Entry<String, LocationData> entry : locationData.entrySet()) {
                root.add(entry.getKey(), entry.getValue().toJson());
            }
            gson.toJson(root, writer);
        } catch (IOException e) {
            getLogger().severe("Failed to save " + file.getName() + ": " + e.getMessage());
        }
    }

    public Map<String, LocationData> getLocationData() {
        return locationData;
    }

    public void addLocation(String point,LocationData entry) {
        locationData.put(point, entry);
        save();
    }

    public boolean removeLocation(String point) {
        boolean removed = locationData.remove(point) != null;
        if (removed) save();
        return removed;
    }

    public boolean containsLocation(String point) {
        return locationData.containsKey(point);
    }

    public LocationData getLocation(String point) {
        return locationData.get(point);
    }
}
