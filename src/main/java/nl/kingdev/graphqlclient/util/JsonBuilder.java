package nl.kingdev.graphqlclient.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class JsonBuilder {

    private JsonObject object = new JsonObject();

    public JsonBuilder append(String name, String value) {
        this.object.addProperty(name, value);
        return this;
    }
    public JsonBuilder append(String name, int value) {
        this.object.addProperty(name, value);
        return this;
    }

    public JsonBuilder append(String name, JsonElement value) {
        this.object.add(name, value);
        return this;
    }
    public JsonObject get() {
        return object;
    }

    @Override
    public String toString() {
        return object.toString();
    }
}
