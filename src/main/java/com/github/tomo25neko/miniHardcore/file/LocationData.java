package com.github.tomo25neko.miniHardcore.file;


import com.google.gson.JsonObject;

public class LocationData {
    public int x,y,z;
    public String dimention;

    public LocationData(int x,int y,int z,String dimention) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.dimention = dimention;
    }

    public JsonObject toJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty("x", x);
        obj.addProperty("y", y);
        obj.addProperty("z", z);
        obj.addProperty("dimention", dimention);
        return obj;
    }

    public static LocationData fromJson(JsonObject obj) {
        int x = obj.get("x").getAsInt();
        int y = obj.get("y").getAsInt();
        int z = obj.get("z").getAsInt();
        String dimention = obj.get("dimention").getAsString();
        return new LocationData(x, y, z, dimention);
    }

}
